package io.cryptorush.userservice.domain.customer

import io.cryptorush.userservice.domain.geoip.CountryResolutionService
import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserRepository
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.domain.user.UserValidator
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import spock.lang.Specification

class DefaultCustomerServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userValidator = new UserValidator(userRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def countryResolutionService = Mock(CountryResolutionService)
    def customerRepository = Mock(CustomerRepository)

    def service = new DefaultCustomerService(userRepository, userValidator, passwordEncoder,
            countryResolutionService, customerRepository)

    def "create new customer user, success case"() {
        given:
        def dateOfBirth = new Date()
        def countryOfResidence = "US"
        def identityNumber = "identity number"
        def passportNumber = "passport number"
        def registrationIp = "33.33.33.33"
        def registrationCountry = "LV"

        def customer = new Customer(dateOfBirth: dateOfBirth, countryOfResidence: countryOfResidence, identityNumber:
                identityNumber, passportNumber: passportNumber, registrationIp: registrationIp)

        def login = "some login"
        def email = "some email"
        def password = "some password"
        def name = "some name"
        def surname = "some surname"
        def user = new User(login: login, email: email, type: UserType.CUSTOMER, password: password, name: name, surname:
                surname, customer: customer)

        and: "specified login or email is not taken"
        userRepository.findByLoginOrEmail(login, email) >> Optional.empty()

        and:
        passwordEncoder.encode(password) >> "encrypted password"

        and:
        countryResolutionService.getCountryCodeByIp(registrationIp) >> Mono.just(registrationCountry)

        when:
        def res = service.registerNewCustomer(user)

        then:
        1 * userRepository.save(u -> {
            u.login == login &&
                    u.name == name &&
                    u.surname == surname &&
                    u.email == email &&
                    u.password == "encrypted password" &&
                    u.type == UserType.CUSTOMER &&
                    u.customer.dateOfBirth == dateOfBirth &&
                    u.customer.countryOfResidence == countryOfResidence &&
                    u.customer.identityNumber == identityNumber &&
                    u.customer.passportNumber == passportNumber &&
                    u.customer.registrationIp == registrationIp
        }) >> new User(id: 1000L, customer: new Customer(id: 666L))
        1 * customerRepository.updateRegistrationCountry(666L, registrationCountry)
        res.id == 1000L
        res.customer.id == 666L
    }
}
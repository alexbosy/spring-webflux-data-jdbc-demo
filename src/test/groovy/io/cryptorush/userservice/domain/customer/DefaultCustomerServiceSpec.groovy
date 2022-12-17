package io.cryptorush.userservice.domain.customer

import io.cryptorush.userservice.domain.geoip.GeoIpService
import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserRepository
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.domain.user.UserValidator
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import spock.lang.Specification

class DefaultCustomerServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userValidator = new UserValidator(userRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def countryResolutionService = Mock(GeoIpService)
    def customerRepository = Mock(CustomerRepository)

    def service = new DefaultCustomerService(userRepository, userValidator, passwordEncoder,
            countryResolutionService, customerRepository)

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

    def "create new customer user"() {
        given: "specified login or email is not taken"
        userRepository.findByLoginOrEmail(login, email) >> []

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

    def "update customer user by login, success case"() {
        given:
        customerRepository.findCustomerUserByLogin(login) >> Optional.of(new User(id: 1000L, login: login, customer:
                new Customer(dateOfBirth: new Date(), identityNumber: "number", passportNumber: "pass",
                        countryOfResidence: "some")))

        and: "specified login or email is not taken"
        userRepository.findByLoginOrEmailExceptId(login, email, 1000L) >> []

        when:
        user.login == null
        service.updateCustomerUser(login, user)

        then:
        1 * userRepository.save({ User u ->
            u.login == login
                    && u.id == 1000L &&
                    u.name == name &&
                    u.surname == surname &&
                    u.email == email &&
                    u.type == null &&
                    u.customer.dateOfBirth == dateOfBirth &&
                    u.customer.passportNumber == passportNumber &&
                    u.customer.identityNumber == identityNumber &&
                    u.customer.countryOfResidence == countryOfResidence
        } as User)
    }

    def "update customer user by login, user not found case"() {
        given:
        customerRepository.findCustomerUserByLogin(login) >> Optional.empty()

        when:
        service.updateCustomerUser(login, user)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "get customer user by login"() {
        given:
        user.id = 10L
        user.customer.id = 1000L
        user.customer.registrationCountry = "LV"
        user.customer.registrationIp = "88.34.55.66"

        customerRepository.findCustomerUserByLogin(login) >> Optional.of(user)

        when:
        def foundUser = service.getCustomerUserByLogin(login)

        then:
        foundUser.id == 10L
        foundUser.login == login
        foundUser.name == name
        foundUser.surname == surname
        foundUser.email == email
        foundUser.type == UserType.CUSTOMER
        foundUser.password == password
        def customer = foundUser.getCustomer()
        customer.id == 1000L
        customer.dateOfBirth == dateOfBirth
        customer.identityNumber == identityNumber
        customer.passportNumber == passportNumber
        customer.countryOfResidence == countryOfResidence
        customer.registrationCountry == "LV"
        customer.registrationIp == "88.34.55.66"
    }

    def "get customer user by login, user not found case"() {
        given:
        customerRepository.findCustomerUserByLogin(login) >> Optional.empty()

        when:
        service.getCustomerUserByLogin(login)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "get customer user public profile by login"() {
        given:
        def profile = new CustomerPublicProfile(login, name, surname, email, dateOfBirth, countryOfResidence)
        customerRepository.findCustomerPublicProfileByLogin(login) >> Optional.of(profile)

        when:
        def foundProfile = service.getCustomerPublicProfileByLogin(login)

        then:
        foundProfile.login() == login
    }

    def "get customer user public profile by login, user not found case"() {
        given:
        customerRepository.findCustomerPublicProfileByLogin(login) >> Optional.empty()

        when:
        service.getCustomerPublicProfileByLogin(login)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "delete customer user by supplied user id"() {
        given:
        def userId = 1000L

        when:
        service.deleteCustomerUserByUserId(userId)

        then:
        1 * customerRepository.hardDeleteByUserId(userId) >> 1
        1 * userRepository.hardDeleteById(userId)
    }

    def "delete customer user by supplied user id, user not found case"() {
        given:
        def userId = 1000L

        when:
        service.deleteCustomerUserByUserId(userId)

        then:
        1 * customerRepository.hardDeleteByUserId(userId) >> 0
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }
}

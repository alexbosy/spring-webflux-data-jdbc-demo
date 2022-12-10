package io.cryptorush.userservice.rest.customer

import io.cryptorush.userservice.domain.customer.Customer
import io.cryptorush.userservice.domain.customer.CustomerPublicProfile
import io.cryptorush.userservice.domain.customer.CustomerService
import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.rest.customer.dto.CustomerCreationRequestDTO
import io.cryptorush.userservice.rest.customer.mapper.CustomerUserMapperImpl
import io.cryptorush.userservice.rest.util.IpResolver
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class CustomerControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def userService = Mock(UserService)
    def customerService = Mock(CustomerService)
    def ipResolver = Mock(IpResolver)
    def customerUserMapper = new CustomerUserMapperImpl()

    def controller = new CustomerController(scheduler, userService, customerService, ipResolver, customerUserMapper)

    def customerId = 200L
    def dateOfBirth = new Date()
    def countryOfResidence = "US"
    def identityNumber = "identity number"
    def passportNumber = "passport number"
    def registrationIp = "33.33.33.33"
    def registrationCountry = "LV"

    def customer = new Customer(id: customerId, dateOfBirth: dateOfBirth, countryOfResidence: countryOfResidence,
            identityNumber: identityNumber, passportNumber: passportNumber, registrationIp: registrationIp,
            registrationCountry: registrationCountry)

    def userId = 100L
    def login = "login"
    def name = "name"
    def surname = "surname"
    def email = "email"
    def type = UserType.CUSTOMER

    def user = new User(id: userId, login: login, name: name, surname: surname, email: email, type: type,
            customer: customer)

    def "GET /customers?offset={offset}&limit={limit} - get customers list with specified offset and limit"() {
        given:
        def offset = 0
        def limit = 1

        when:
        def result = controller.getCustomers(offset, limit).block()

        then:
        1 * userService.getAllCustomerUsers(offset, limit) >> [user]
        result.size() == 1
        def customerDTO = result[0]
        customerDTO.id() == customerId
        customerDTO.dateOfBirth() == dateOfBirth
        customerDTO.countryOfResidence() == countryOfResidence
        customerDTO.identityNumber() == identityNumber
        customerDTO.passportNumber() == passportNumber
        customerDTO.registrationIp() == registrationIp
        customerDTO.registrationCountry() == registrationCountry
        customerDTO.userId() == userId
        customerDTO.login() == login
        customerDTO.name() == name
        customerDTO.surname() == surname
        customerDTO.email() == email
    }

    def "POST /customer/registration - register a new customer"() {
        given:
        def login = "login"
        def name = "name"
        def surname = "surname"
        def email = "email"
        def password = "password"

        def requestDto = new CustomerCreationRequestDTO(dateOfBirth: dateOfBirth, countryOfResidence:
                countryOfResidence, identityNumber: identityNumber, passportNumber: passportNumber, login: login,
                name: name, surname: surname, email: email, password: password)

        def inetSocketAddress = new InetSocketAddress(InetAddress.getByName("88.88.88.88"), 8080)
        def request = MockServerHttpRequest.post("some url").remoteAddress(inetSocketAddress).build()
        ipResolver.resolveIpAddress(request.getRemoteAddress()) >> "88.88.88.88"

        when:
        def resDTO = controller.registerNewCustomer(requestDto, request).block()

        then:
        1 * customerService.registerNewCustomer(u -> {
            u.login == login &&
                    u.name == name &&
                    u.surname == surname &&
                    u.email == email &&
                    u.password == password &&
                    u.type == UserType.CUSTOMER
        }) >> new User(login: login, name: name, surname: surname, email: email,
                customer: new Customer(dateOfBirth: dateOfBirth, countryOfResidence: countryOfResidence,
                        identityNumber: identityNumber, passportNumber: passportNumber))
        resDTO.login() == login
        resDTO.name() == name
        resDTO.surname() == surname
        resDTO.email() == email
        resDTO.dateOfBirth() == dateOfBirth
        resDTO.countryOfResidence() == countryOfResidence
        resDTO.identityNumber() == identityNumber
        resDTO.passportNumber() == passportNumber
    }

    def "GET /customer/{login} - find customer user by login"() {
        given:
        customerService.getCustomerUserByLogin(login) >> user

        when:
        def customerDTO = controller.getCustomer(login).block()

        then:
        customerDTO.id() == customerId
        customerDTO.dateOfBirth() == dateOfBirth
        customerDTO.countryOfResidence() == countryOfResidence
        customerDTO.identityNumber() == identityNumber
        customerDTO.passportNumber() == passportNumber
        customerDTO.registrationIp() == registrationIp
        customerDTO.registrationCountry() == registrationCountry
        customerDTO.userId() == userId
        customerDTO.login() == login
        customerDTO.name() == name
        customerDTO.surname() == surname
        customerDTO.email() == email
    }

    def "GET /customer/profile/{login} - find customer user public profile by login"() {
        given:
        def profile = new CustomerPublicProfile(login, name, surname, email, dateOfBirth, countryOfResidence)
        customerService.getCustomerPublicProfileByLogin(login) >> profile

        when:
        def profileDTO = controller.getCustomerPublicProfile(login).block()

        then:
        profileDTO.login() == login
        profileDTO.name() == name
        profileDTO.surname() == surname
        profileDTO.email() == email
        profileDTO.dateOfBirth() == dateOfBirth
        profileDTO.countryOfResidence() == countryOfResidence
    }

    def "DELETE /customer/{userId} - delete customer user by supplied user id"() {
        given:
        def userId = 1000L

        when:
        def result = controller.deleteUser(userId).block()

        then:
        1 * customerService.deleteCustomerUserByUserId(userId)
        result.statusCode == HttpStatus.NO_CONTENT
    }
}

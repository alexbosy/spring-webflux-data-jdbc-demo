package io.cryptorush.userservice.rest.customer

import io.cryptorush.userservice.domain.customer.Customer
import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class CustomerControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def userService = Mock(UserService)
    def controller = new CustomerController(scheduler, userService)

    def "GET /customers?offset={offset}&limit={limit} - get customers list with specified offset and limit"() {
        given:
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

        def offset = 0
        def limit = 1

        when:
        def result = controller.getCustomers(offset, limit).block()

        then:
        1 * userService.getAllCustomerUsers(offset, limit) >> [user]
        result.size() == 1
        def customerDTO = result[0]
        customerDTO.getId() == customerId
        customerDTO.dateOfBirth == dateOfBirth
        customerDTO.countryOfResidence == countryOfResidence
        customerDTO.identityNumber == identityNumber
        customerDTO.passportNumber == passportNumber
        customerDTO.registrationIp == registrationIp
        customerDTO.registrationCountry == registrationCountry
        customerDTO.userId == userId
        customerDTO.login == login
        customerDTO.name == name
        customerDTO.surname == surname
        customerDTO.email == email
    }


}

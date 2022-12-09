package io.cryptorush.userservice.rest.customer


import io.cryptorush.userservice.rest.client.TestRESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.time.LocalDate

@ActiveProfiles("at-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerATSpec extends Specification {

    def testClient = new TestRESTClient()

    def uniqueLogin = "at-" + System.currentTimeMillis()
    def uniqueEmail = System.currentTimeMillis() + "@at-tests.lv"
    def name = "some name"
    def surname = "some surname"
    def password = "some password"
    def countryOfResidence = "US"
    def identityNumber = "identity number"
    def passportNumber = "passport number"


    def dateNow = LocalDate.now()
    def dateOfBirth = dateNow.minusYears(25)
    def dateOfBirthStr = dateOfBirth.format("dd-MM-yyyy")

    def payload = ["dateOfBirth"   : dateOfBirthStr, "countryOfResidence": countryOfResidence, "identityNumber": identityNumber,
                   "passportNumber": passportNumber, "login": uniqueLogin, "name": name,
                   "surname"       : surname, "email": uniqueEmail, "password": password]


    def "POST /customer/registration"() {
        when: "register a new customer user"
        def res = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "45.22.33.233"])

        then:
        res.status == 200
        res.data["login"] == uniqueLogin
        res.data["name"] == name
        res.data["surname"] == surname
        res.data["email"] == uniqueEmail
        res.data["dateOfBirth"] == dateOfBirthStr
        res.data["countryOfResidence"] == countryOfResidence
        res.data["identityNumber"] == identityNumber
        res.data["passportNumber"] == passportNumber

        and: "trying to create a new user with existing login"
        def res2 = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "45.22.33.233"])
        res2.status == 403
        res2.data["login"] == "Supplied login is already taken"

        and: "trying to create a new user with existing email"
        payload.put("login", "at-" + System.currentTimeMillis())
        def res3 = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "45.22.33.233"])
        res3.status == 403
        res3.data["email"] == "Supplied email is already taken"
    }

    def "POST /customer/registration, error case with empty payload"() {
        when:
        def res = testClient.post('/customer/registration', [:])

        then:
        res.status == 400
        res.data["countryOfResidence"] == "Country of residence can not be empty"
        res.data["identityNumber"] == "Identity number can not be empty"
        res.data["passportNumber"] == "Passport number can not be empty"
        res.data["login"] == "Login can not be empty"
        res.data["name"] == "Name can not be empty"
        res.data["surname"] == "Surname can not be empty"
        res.data["email"] == "Email can not be empty"
        res.data["password"] == "Password can not be empty"
    }

    def "POST /customer/registration, error case with invalid data"() {
        when:
        def dateOfBirthStrInFuture = LocalDate.now().plusMonths(1).format("dd-MM-yyyy")
        def smallLogin = "12345"
        def invalidCountryCode = "ABC"
        def invalidEmail = "invalid email"
        def smallPassword = "1234567"
        def payload = ["dateOfBirth"   : dateOfBirthStrInFuture, "countryOfResidence": invalidCountryCode, "identityNumber": identityNumber,
                       "passportNumber": passportNumber, "login": smallLogin, "name": name,
                       "surname"       : surname, "email": invalidEmail, "password": smallPassword]
        def res = testClient.post('/customer/registration', payload)

        then:
        res.status == 400
        res.data["dateOfBirth"] == "Date of birth must be in the past"
        res.data["password"] == "Password min length is 8 chars"
        res.data["login"] == "Login min length is 6 chars"
        res.data["countryOfResidence"] == "Country of residence max length is 2 chars"
        res.data["email"] == "Email is not valid"
    }

    def "GET /customer/{login}"() {
        when: "register a new customer user"
        def res = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])
        sleep 1500

        then:
        res.status == 200
        res.data["login"] == uniqueLogin

        and: "try to find created user by login"
        def res2 = testClient.get("/customer/${uniqueLogin}")
        res2.status == 200
        res2.data["id"] != null
        res2.data["userId"] != null
        res2.data["login"] == uniqueLogin
        res2.data["name"] == name
        res2.data["surname"] == surname
        res2.data["email"] == uniqueEmail
        res2.data["dateOfBirth"] == dateOfBirthStr
        res2.data["countryOfResidence"] == countryOfResidence
        res2.data["identityNumber"] == identityNumber
        res2.data["passportNumber"] == passportNumber
        res2.data["registrationIp"] == "88.88.88.233"
        res2.data["registrationCountry"] == "NO"
    }

    def "GET /customers?offset={offset}&limit={limit}"() {
        //TODO: must be finished, when CRUD API will be ready
        expect:
        1 == 1
    }

    def cleanupSpec() {
        //added small delay for async methods execution
        sleep 1500
        //TODO: replace real external service calls with WireMock or MockWebServer
    }
}

package io.cryptorush.userservice.rest.customer


import io.cryptorush.userservice.rest.client.TestRESTClient
import io.fabric8.mockwebserver.DefaultMockServer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
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

    @Shared
    DefaultMockServer server = new DefaultMockServer()

    def setupSpec() {
        server.start(8088)
        server.expect()
                .get().withPath("/json/88.88.88.233")
                .andReturn(200, '{"country_code":"NO"}')
                .withHeader("Content-Type", "application/json; charset=utf-8").always()

    }

    def "POST /customer/registration"() {
        when: "register a new customer user"
        def res = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])

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
        def res2 = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])
        res2.status == 403
        res2.data["login"] == "Supplied login is already taken"

        and: "trying to create a new user with existing email"
        payload.put("login", "at-" + System.currentTimeMillis())
        def res3 = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])
        res3.status == 403
        res3.data["email"] == "Supplied email is already taken"

        cleanup:
        def res4 = testClient.get("/customer/${uniqueLogin}")
        testClient.delete("/customer/${res4.data["userId"]}")
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
        def payload = ["dateOfBirth"   : dateOfBirthStrInFuture, "countryOfResidence": invalidCountryCode,
                       "identityNumber": identityNumber, "passportNumber": passportNumber, "login": smallLogin, "name": name,
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
        sleep 1000 //wait for async ip resolution finished

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

        cleanup:
        testClient.delete("/customer/${res2.data["userId"]}")
    }

    def "GET /customer/profile/{login}"() {
        when: "register a new customer user"
        def res = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])

        then:
        res.status == 200
        res.data["login"] == uniqueLogin

        and: "try to get created customer user public profile by login"
        def res2 = testClient.get("/customer/profile/${uniqueLogin}")
        res2.status == 200
        res2.data["login"] == uniqueLogin
        res2.data["name"] == name
        res2.data["surname"] == surname
        res2.data["email"] == uniqueEmail
        res2.data["dateOfBirth"] == dateOfBirthStr
        res2.data["countryOfResidence"] == countryOfResidence

        and: "try to find not existing customer user public profile by login"
        def res3 = testClient.get("/customer/profile/not-existing${uniqueLogin}")
        res3.status == 404
        res3.data["error"] == "User not found"

        cleanup:
        def res4 = testClient.get("/customer/${uniqueLogin}")
        testClient.delete("/customer/${res4.data["userId"]}")
    }

    def "GET and UPDATE /customer/my/profile"() {
        when: "register a new customer user"
        def res = testClient.post('/customer/registration', payload, ["X-Forwarded-For": "88.88.88.233"])

        then:
        res.status == 200
        res.data["login"] == uniqueLogin

        and: "authenticate"
        def res2 = testClient.post('/auth', ["login": uniqueLogin, "password": payload.password])
        res2.status == 200
        res2.data["token"] != null

        and: "try to get currently authenticated customer full profile"
        def jwt = res2.data["token"]
        def res3 = testClient.get("/customer/my/profile", [:], ["Authorization": "Bearer ${jwt}"])
        res3.status == 200
        res3.data["login"] == uniqueLogin
        res3.data["name"] == name
        res3.data["surname"] == surname
        res3.data["email"] == uniqueEmail
        res3.data["dateOfBirth"] == dateOfBirthStr
        res3.data["countryOfResidence"] == countryOfResidence
        res3.data["identityNumber"] == identityNumber
        res3.data["passportNumber"] == passportNumber

        cleanup:
        def res5 = testClient.get("/customer/${uniqueLogin}")
        testClient.delete("/customer/${res5.data["userId"]}")
    }

    def "DELETE /customer/{userId}"() {
        when: "create a new customer user"
        def res = testClient.post('/customer/registration', payload)

        then:
        res.status == 200

        and: "try to find created user by login"
        def res2 = testClient.get("/customer/${uniqueLogin}")
        res2.status == 200

        and: "delete this created customer user"
        def createdCustomerUserId = res2.data["userId"]
        def res3 = testClient.delete("/customer/${createdCustomerUserId}")
        res3.status == 204

        and: "check this user was deleted"
        def res4 = testClient.get("/customer/${uniqueLogin}")
        res4.status == 404

        and: "delete not existing customer user"
        def res5 = testClient.delete("/customer/${0}")
        res5.status == 404
        res5.data["error"] == "User not found"
    }

    def "GET /customers?offset={offset}&limit={limit}"() {
        when: "create a 2 new customer users"
        def res1 = testClient.post('/customer/registration', payload)
        payload["login"] = "at-" + System.currentTimeMillis()
        payload["email"] = System.currentTimeMillis() + "@at-tests.lv"
        def res2 = testClient.post('/customer/registration', payload)

        then:
        res1.status == 200
        def user1Login = res1.data["login"]
        res2.status == 200
        def user2Login = res2.data["login"]

        and: "get all customer users with offset 0 and limit 2"
        def offset = 0
        def limit = 2
        def result = testClient.get("/customers", ["offset": offset, "limit": limit])
        result.status == 200
        result.data.size == 2

        cleanup:
        def res3 = testClient.get("/customer/${user1Login}")
        testClient.delete("/customer/${res3.data["userId"]}")
        def res4 = testClient.get("/customer/${user2Login}")
        testClient.delete("/customer/${res4.data["userId"]}")
    }

    def cleanupSpec() {
        server.shutdown()
    }
}

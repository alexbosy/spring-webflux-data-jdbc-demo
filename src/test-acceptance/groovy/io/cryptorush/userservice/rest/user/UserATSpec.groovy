package io.cryptorush.userservice.rest.user


import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.rest.client.TestRESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("at-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserATSpec extends Specification {

    def testClient = new TestRESTClient()

    def uniqueLogin = "at-" + System.currentTimeMillis()
    def uniqueEmail = System.currentTimeMillis() + "@at-tests.lv"
    def payload = ["login": uniqueLogin, "name": "some name", "surname": "some surname",
                   "email": uniqueEmail, "password": "some password", "type": UserType.ADMIN.name()]

    def "POST /user"() {
        when: "creating a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200
        res.data["id"] >= 1
        res.data["login"] == uniqueLogin

        and: "trying to create a new user with existing login"
        def res2 = testClient.post('/user', payload)
        res2.status == 403
        res2.data["login"] == "Supplied login is already taken"

        and: "trying to create a new user with existing email"
        payload.put("login", "at-" + System.currentTimeMillis())
        def res3 = testClient.post('/user', payload)
        res3.status == 403
        res3.data["email"] == "Supplied email is already taken"
    }

    def "POST /user, error case with empty payload"() {
        expect:
        def payload = [:]
        def res = testClient.post('/user', payload)
        res.status == 400
        res.data["login"] == "Login can not be empty"
        res.data["name"] == "Name can not be empty"
        res.data["surname"] == "Surname can not be empty"
        res.data["email"] == "Email can not be empty"
        res.data["password"] == "Password can not be empty"
        res.data["type"] == "Type can not be empty"
    }

    def "GET /user/{id}"() {
        when: "creating a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "find user by id"
        def createdUserId = res.data["id"]
        def res2 = testClient.get("/user/${createdUserId}")
        res2.status == 200
        res2.data["id"] == createdUserId
        res2.data["login"] == payload.login
        res2.data["name"] == payload.name
        res2.data["surname"] == payload.surname
        res2.data["email"] == payload.email
        res2.data["type"] == payload.type
    }

    def "GET /user/{id}, user not found case"() {
        when:
        def notExistingId = 0L
        def res = testClient.get("/user/${notExistingId}")

        then:
        res.status == 404
        res.data["error"] == "User not found"
    }
}

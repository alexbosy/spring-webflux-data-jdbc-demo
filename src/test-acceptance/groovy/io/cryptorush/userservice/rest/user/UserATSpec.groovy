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
        def createdUserId = res.data["id"]
        createdUserId >= 1
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

        cleanup:
        testClient.delete("/user/${createdUserId}")
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
        when: "create a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "authenticate"
        def res2 = testClient.post('/auth', ["login": res.data["login"], "password": payload.password])
        res2.status == 200
        res2.data["token"] != null

        and: "find user by id"
        def createdUserId = res.data["id"]
        def jwt = res2.data["token"]
        def res3 = testClient.get("/user/${createdUserId}", [:], ["Authorization": "Bearer ${jwt}"])
        res3.status == 200
        res3.data["id"] == createdUserId
        res3.data["login"] == payload.login
        res3.data["name"] == payload.name
        res3.data["surname"] == payload.surname
        res3.data["email"] == payload.email
        res3.data["type"] == payload.type

        cleanup:
        testClient.delete("/user/${createdUserId}")
    }

    def "GET /me"() {
        given:
        when: "create a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "authenticate"
        def res2 = testClient.post('/auth', ["login": res.data["login"], "password": payload.password])
        res2.status == 200
        res2.data["token"] != null

        and: "get current authenticated user"
        def createdUserId = res.data["id"]
        def jwt = res2.data["token"]
        def res3 = testClient.get("/me", [:], ["Authorization": "Bearer ${jwt}"])
        res3.status == 200
        res3.data["id"] == createdUserId
        res3.data["login"] == payload.login
        res3.data["name"] == payload.name
        res3.data["surname"] == payload.surname
        res3.data["email"] == payload.email
        res3.data["type"] == payload.type

        cleanup:
        testClient.delete("/user/${createdUserId}")
    }

    def "GET /user/{id}, user not found case"() {
        when: "create a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "authenticate"
        def res2 = testClient.post('/auth', ["login": res.data["login"], "password": payload.password])
        res2.status == 200
        res2.data["token"] != null

        and:
        def notExistingId = 0L
        def jwt = res2.data["token"]
        def res3 = testClient.get("/user/${notExistingId}", [:], ["Authorization": "Bearer ${jwt}"])
        res3.status == 404
        res3.data["error"] == "User not found"
    }

    def "DELETE /user/{id}"() {
        when: "creat a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "delete this created user"
        def createdUserId = res.data["id"]
        def res2 = testClient.delete("/user/${createdUserId}")
        res2.status == 204

        and: "create second user"
        def result = testClient.post('/user', payload)
        result.status == 200

        and: "authenticate"
        def res3 = testClient.post('/auth', ["login": result.data["login"], "password": payload.password])
        res3.status == 200
        res3.data["token"] != null

        and: "check this user was deleted"
        def jwt = res3.data["token"]
        def res4 = testClient.get("/user/${createdUserId}", [:], ["Authorization": "Bearer ${jwt}"])
        res4.status == 404

        and: "delete second user"
        def secondUserId = result.data["id"]
        def res5 = testClient.delete("/user/${secondUserId}")
        res5.status == 204
    }

    def "DELETE /user/{id}, user not found case"() {
        when:
        def notExistingId = 0L
        def res = testClient.delete("/user/${notExistingId}")

        then:
        res.status == 404
        res.data["error"] == "User not found"
    }

    def "PUT /user/{id}"() {
        when: "create a new user"
        def res = testClient.post('/user', payload)

        then:
        res.status == 200

        and: "authenticate"
        def auth = testClient.post('/auth', ["login": res.data["login"], "password": payload.password])
        auth.status == 200
        auth.data["token"] != null

        and: "find user by id"
        def jwt = auth.data["token"]
        def createdUserId = res.data["id"]
        def res2 = testClient.get("/user/${createdUserId}", [:], ["Authorization": "Bearer ${jwt}"])
        res2.status == 200
        res2.data["id"] == createdUserId
        res2.data["login"] == payload.login
        res2.data["name"] == payload.name
        res2.data["surname"] == payload.surname
        res2.data["email"] == payload.email
        res2.data["type"] == payload.type

        and: "update user by id"
        def updatePayload = ["login": "new login", "name": "new name", "surname": "new surname",
                             "email": "new@email.com", "type": UserType.ADMIN.name()]
        def res3 = testClient.put("/user/${createdUserId}", updatePayload, ["Authorization": "Bearer ${jwt}"])
        res3.status == 200
        res3.data["id"] == createdUserId
        res3.data["login"] == "new login"
        res3.data["name"] == "new name"
        res3.data["surname"] == "new surname"
        res3.data["email"] == "new@email.com"
        res3.data["type"] == UserType.ADMIN.name()

        cleanup:
        testClient.delete("/user/${createdUserId}")
    }

    def "GET /users?offset={offset}&limit={limit}"() {
        when: "create a 2 new users"
        def res1 = testClient.post('/user', payload)
        payload["login"] = "at-" + System.currentTimeMillis()
        payload["email"] = System.currentTimeMillis() + "@at-tests.lv"
        def res2 = testClient.post('/user', payload)

        then:
        res1.status == 200
        def user1Id = res1.data["id"]
        res2.status == 200
        def user2Id = res2.data["id"]

        and: "get all users with offset 0 and limit 2"
        def offset = 0
        def limit = 2
        def result = testClient.get("/users", ["offset": offset, "limit": limit])
        result.status == 200
        result.data.size == 2

        cleanup:
        testClient.delete("/user/${user1Id}")
        testClient.delete("/user/${user2Id}")
    }
}

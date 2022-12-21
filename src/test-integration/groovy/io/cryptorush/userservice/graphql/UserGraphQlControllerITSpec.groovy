package io.cryptorush.userservice.graphql

import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.graphql.mapper.UserGqlMapper
import io.cryptorush.userservice.graphql.mapper.UserGqlMapperImpl
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.graphql.test.tester.GraphQlTester
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

@GraphQlTest(controllers = [UserGraphQlController.class])
class UserGraphQlControllerITSpec extends Specification {

    @Autowired
    GraphQlTester graphQlTester

    @Autowired
    Scheduler scheduler

    @SpringBean
    UserService userService = Mock()

    @Autowired
    UserGqlMapper userGqlMapper

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Qualifier("graphql-scheduler")
        Scheduler scheduler() {
            return Schedulers.immediate()
        }

        @Bean
        UserGqlMapper mapper() {
            return new UserGqlMapperImpl()
        }
    }

    def id = 666L
    def someUser = new User(id: id, login: "super login", name: "name", surname: "surname", email:
            "some email", type: UserType.ADMIN)

    def "get user by id"() {
        given:
        def query = """
                                query {
                                    userById(id: ${id}) {
                                            id
                                            login
                                            name
                                            surname
                                            email
                                            type
                                    }
                                }
        """
        userService.getById(id) >> someUser

        when:
        def res = graphQlTester.document(query).execute()

        then:
        res.path("data.userById.id").entity(Long.class).get() == id
        res.path("data.userById.login").entity(String.class).get() == "super login"
        res.path("data.userById.name").entity(String.class).get() == "name"
        res.path("data.userById.surname").entity(String.class).get() == "surname"
        res.path("data.userById.email").entity(String.class).get() == "some email"
        res.path("data.userById.type").entity(String.class).get() == UserType.ADMIN.name()
    }

    def "get all users"() {
        given:
        def offset = 0
        def limit = 1
        def query = """
                                query {
                                    allUsers(offset: ${offset}, limit: ${limit}) {
                                            id
                                            login
                                            name
                                            surname
                                            email
                                            type
                                    }
                                }
        """
        userService.getAllSystemUsers(offset, limit) >> [someUser]

        when:
        def res = graphQlTester.document(query).execute()

        then:
        res.path("data.allUsers[0].id").entity(Long.class).get() == id
        res.path("data.allUsers[0].login").entity(String.class).get() == "super login"
        res.path("data.allUsers[0].name").entity(String.class).get() == "name"
        res.path("data.allUsers[0].surname").entity(String.class).get() == "surname"
        res.path("data.allUsers[0].email").entity(String.class).get() == "some email"
        res.path("data.allUsers[0].type").entity(String.class).get() == UserType.ADMIN.name()
    }

    def "create new system user"() {
        given:
        def query = """
                                mutation {
                                    createUser(systemUserInput: {
                                        login: "graphql-login",
                                        name: "some name",
                                        surname: "some surname",
                                        email: "some@email.com",
                                        password: "password",
                                        type: ADMIN
    
                                    }) {
                                            id
                                            login
                                    }
                                }
                        """

        when:
        def res = graphQlTester.document(query).execute()

        then:
        1 * userService.createSystemUser({ User user ->
            user.login == "graphql-login" &&
                    user.name == "some name" &&
                    user.surname == "some surname" &&
                    user.email == "some@email.com" &&
                    user.type == UserType.ADMIN
        }) >> new User(id: id, login: "graphql-login")
        res.path("data.createUser.id").entity(Long.class).get() == id
        res.path("data.createUser.login").entity(String.class).get() == "graphql-login"
    }
}

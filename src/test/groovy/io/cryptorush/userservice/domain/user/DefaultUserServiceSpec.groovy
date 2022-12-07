package io.cryptorush.userservice.domain.user

import io.cryptorush.userservice.domain.customer.Customer
import io.cryptorush.userservice.domain.user.validation.EmailIsTakenExceptionField
import io.cryptorush.userservice.domain.user.validation.InvalidUserTypeExceptionField
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenExceptionField
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class DefaultUserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userValidator = new UserValidator(userRepository)
    def passwordEncoder = Mock(PasswordEncoder)

    def userService = new DefaultUserService(userRepository, userValidator, passwordEncoder)

    def "create new system user, success case"() {
        given:
        def login = "some login"
        def email = "some email"
        def password = "some password"
        def name = "some name"
        def surname = "some surname"
        def type = supportedType
        def user = new User(login: login, email: email, type: type, password: password, name: name, surname: surname)

        and: "specified login or email is not taken"
        userRepository.findByLoginOrEmail(login, email) >> Optional.empty()

        and:
        passwordEncoder.encode(password) >> "encrypted password"

        when:
        userService.createSystemUser(user)

        then:
        1 * userRepository.save({ User u ->
            u.password == "encrypted password" &&
                    u.login == login &&
                    u.name == name &&
                    u.surname == surname &&
                    u.email == email &&
                    u.type == type
        } as User) >> new User(id: 666L)

        where:
        supportedType << [UserType.ADMIN, UserType.MANAGER]
    }

    def "create new system user, error case with unsupported user type"() {
        given:
        def user = new User(type: UserType.CUSTOMER)

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(InvalidUserTypeExceptionField)
        e.message == "Forbidden user type"
        e.fieldName == "type"
    }

    def "create new system user, error case with taken login"() {
        given:
        def user = new User(login: "taken login", type: UserType.ADMIN)

        and: "specified login is taken"
        userRepository.findByLoginOrEmail("taken login", _) >> Optional.of(user)

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(LoginIsTakenExceptionField)
        e.message == "Supplied login is already taken"
        e.fieldName == "login"
    }

    def "create new system user, error case with taken email"() {
        given:
        def user = new User(email: "taken email", type: UserType.ADMIN)

        and: "specified email is taken"
        userRepository.findByLoginOrEmail(_, "taken email") >> Optional.of(new User(login: "some login", email:
                "taken email"))

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(EmailIsTakenExceptionField)
        e.message == "Supplied email is already taken"
        e.fieldName == "email"
    }

    def "update existing user"() {
        given:
        def existingUserId = 666L
        def updatedLogin = "updated login"
        def updatedEmail = "updated email"
        def updatedPassword = "updated password"
        def updatedName = "some name"
        def updatedSurname = "some surname"
        def updatedType = supportedType
        def user = new User(id: existingUserId, login: updatedLogin, email: updatedEmail, type: updatedType, password:
                updatedPassword, name: updatedName, surname: updatedSurname)

        and:
        userRepository.findById(existingUserId) >> Optional.of(new User(id: existingUserId))

        and: "specified login or email is not taken"
        userRepository.findByLoginOrEmailExceptId(updatedLogin, updatedEmail, existingUserId) >> Optional.empty()

        when:
        userService.updateUser(user)

        then:
        1 * userRepository.save({ User u ->
            u.login == updatedLogin &&
                    u.name == updatedName &&
                    u.surname == updatedSurname &&
                    u.email == updatedEmail &&
                    u.type == updatedType
        } as User)

        where:
        supportedType << [UserType.ADMIN, UserType.MANAGER]
    }

    def "update exising user, error case with taken email"() {
        given:
        def id = 100L
        def user = new User(id: id, email: "taken email", type: UserType.ADMIN)
        userRepository.findById(id) >> Optional.of(new User(id: id))

        and: "specified email is taken"
        userRepository.findByLoginOrEmailExceptId(_, "taken email", id) >> Optional.of(new User(login: "some login", email:
                "taken email"))

        when:
        userService.updateUser(user)

        then:
        def e = thrown(EmailIsTakenExceptionField)
        e.message == "Supplied email is already taken"
        e.fieldName == "email"
    }

    def "update exising user, error case with taken login"() {
        given:
        def id = 100L
        def user = new User(id: id, login: "taken login", type: UserType.ADMIN)
        userRepository.findById(id) >> Optional.of(new User(id: id))

        and: "specified email is taken"
        userRepository.findByLoginOrEmailExceptId("taken login", _, id) >> Optional.of(new User(login:
                "taken login", email:
                "some email"))

        when:
        userService.updateUser(user)

        then:
        def e = thrown(LoginIsTakenExceptionField)
        e.message == "Supplied login is already taken"
        e.fieldName == "login"
    }

    def "update exising user, error case, when user not found"() {
        given:
        def existingId = 1000L
        def user = new User(id: existingId)
        userRepository.findById(existingId) >> Optional.empty()

        when:
        userService.updateUser(user)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "get user by id"() {
        given:
        def id = 1000L
        def user = new User(id: id, login: "login", email: "email", type: UserType.MANAGER, password: "encrypted", name:
                "name", surname: "surname")
        userRepository.findById(id) >> Optional.of(user)

        when:
        def foundUser = userService.getById(id)

        then:
        foundUser.id == id
        foundUser.login == "login"
        foundUser.email == "email"
        foundUser.name == "name"
        foundUser.surname == "surname"
        foundUser.type == UserType.MANAGER
        foundUser.password == "encrypted"
    }

    def "get user by id, not found case"() {
        given:
        def id = 1000L
        userRepository.findById(id) >> Optional.empty()

        when:
        userService.getById(id)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "delete user by id"() {
        given:
        def id = 1000L
        userRepository.hardDeleteById(id) >> 1

        when:
        userService.deleteSystemUserById(id)

        then:
        noExceptionThrown()
    }

    def "delete user by id, not found case"() {
        given:
        def id = 1000L
        userRepository.hardDeleteById(id) >> 0

        when:
        userService.deleteSystemUserById(id)

        then:
        def e = thrown(UserNotFoundException)
        e.message == "User not found"
    }

    def "get all system users with specified offset and limit "() {
        given:
        def offset = 1
        def limit = DefaultUserService.MAX_LIMIT + 1
        userRepository.getAllSystemUsers(1, DefaultUserService.MAX_LIMIT) >> [new User(id: 100L), new User(id: 101L)]

        when:
        def users = userService.getAllSystemUsers(offset, limit)

        then:
        users.size() == 2
        users[0].id == 100L
        users[1].id == 101L
    }

    def "get all customer users with specified offset and limit "() {
        given:
        def offset = 0
        def limit = DefaultUserService.MAX_LIMIT + 1
        userRepository.getAllCustomerUsers(0, DefaultUserService.MAX_LIMIT) >> [new Customer(id: 1000L), new Customer(id:
                2000L)]

        when:
        def users = userService.getAllCustomerUsers(offset, limit)

        then:
        users.size() == 2
        users[0].id == 1000L
        users[1].id == 2000L
    }
}

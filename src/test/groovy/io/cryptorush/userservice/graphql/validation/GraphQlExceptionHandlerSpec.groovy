package io.cryptorush.userservice.graphql.validation

import graphql.execution.ExecutionStepInfo
import graphql.language.Field
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLEnumType
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenExceptionField
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException
import org.hibernate.validator.internal.engine.ConstraintViolationImpl
import org.springframework.graphql.execution.ErrorType
import spock.lang.Specification

import javax.validation.ConstraintViolationException

class GraphQlExceptionHandlerSpec extends Specification {

    def handler = new GraphQlExceptionHandler()
    def env = Mock(DataFetchingEnvironment)

    def setup() {
        env.getField() >> new Field("test")
        env.getExecutionStepInfo() >> ExecutionStepInfo.newExecutionStepInfo()
                .type(GraphQLEnumType.newEnum().name("enum").build())
                .build()
    }

    def "handle UserNotFoundException"() {
        when:
        def e = new UserNotFoundException()
        def res = handler.resolveException(e, env).block().first()

        then:
        res.message == e.message
        res.getErrorType() == ErrorType.NOT_FOUND
    }

    def "handle BusinessFieldValidationException"() {
        when:
        def e = new LoginIsTakenExceptionField()
        def res = handler.resolveException(e, env).block().first()

        then:
        res.message == e.message
        res.getErrorType() == ErrorType.FORBIDDEN
    }

    def "handle ConstraintViolationException"() {
        when:
        def constraintViolation = ConstraintViolationImpl.forBeanValidation("",
                null, null, "Error text!", this.class, null,
                null, null, null, null, null)
        def e = new ConstraintViolationException("", [constraintViolation] as Set)
        def res = handler.resolveException(e, env).block().first()

        then:
        res.message == "Error text!"
        res.getErrorType() == ErrorType.BAD_REQUEST
    }
}

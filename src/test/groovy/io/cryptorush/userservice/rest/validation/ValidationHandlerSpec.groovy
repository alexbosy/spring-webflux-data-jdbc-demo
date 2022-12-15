package io.cryptorush.userservice.rest.validation

import io.cryptorush.userservice.domain.auth.AuthException
import io.cryptorush.userservice.domain.user.validation.EmailIsTakenExceptionField
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenExceptionField
import io.cryptorush.userservice.domain.user.validation.UserNotFoundException
import org.springframework.core.annotation.SynthesizingMethodParameter
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class ValidationHandlerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def validationHandler = new ValidationHandler(scheduler)

    def "handle hibernate validator exceptions"() {
        given:
        def fieldError1 = new FieldError("object1", "field1", "error message1")
        def fieldError2 = new FieldError("object2", "field2", "error message2")

        def dummyParameter = ValidationHandler.class.getDeclaredMethods()[0].getParameters()[0]
        def requestValidationException = new WebExchangeBindException(SynthesizingMethodParameter.forParameter(dummyParameter),
                new BeanPropertyBindingResult(null, "some name"))
        requestValidationException.addError(fieldError1)
        requestValidationException.addError(fieldError2)

        when:
        def result = validationHandler.handleRequestValidationException(requestValidationException).block()

        then:
        result.body["field1"] == "error message1"
        result.body["field2"] == "error message2"
    }

    def "handle business field validation exceptions"() {
        when:
        def result = validationHandler.handleBusinessFieldValidationException(businessValidationException).block()

        then:
        result.body[businessValidationException.fieldName] == businessValidationException.message

        where:
        businessValidationException << [new LoginIsTakenExceptionField(), new EmailIsTakenExceptionField()]
    }

    def "handle not found exceptions"() {
        when:
        def result = validationHandler.handleNotFoundException(notFoundException).block()

        then:
        result.body["error"] == notFoundException.message

        where:
        notFoundException << [new UserNotFoundException()]
    }

    def "handle authentication exception"() {

        when:
        def e = new AuthException()
        def res = validationHandler.handleAuthException(e).block()

        then:
        res.body["error"] == e.message
    }
}

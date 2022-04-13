package io.cryptorush.userservice

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import spock.lang.Specification

@SpringBootTest()
class ApplicationITSpec extends Specification {

    @Autowired
    ApplicationContext context

    def "test context loading"() {
        expect:
        context != null
        context.containsBean("application")
    }
}

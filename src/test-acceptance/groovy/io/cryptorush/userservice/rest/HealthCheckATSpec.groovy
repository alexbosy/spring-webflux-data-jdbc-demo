package io.cryptorush.userservice.rest

import io.cryptorush.userservice.rest.client.TestRESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("at-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class HealthCheckATSpec extends Specification {

    def testClient = new TestRESTClient()

    def "GET /check"() {
        expect:
        def res = testClient.get('/check')
        res.status == 200
        res.data.text == "user-service:[OK]"
    }
}

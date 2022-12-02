package io.cryptorush.userservice.rest.customer

import io.cryptorush.userservice.rest.client.TestRESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@ActiveProfiles("at-tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class CustomerATSpec extends Specification {

    def testClient = new TestRESTClient()

    def "GET /customers?offset={offset}&limit={limit}"() {
        //TODO: must be finished, when CRUD API will be ready
        expect:
        1 == 1
    }
}

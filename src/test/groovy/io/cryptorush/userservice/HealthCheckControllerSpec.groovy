package io.cryptorush.userservice

import io.cryptorush.userservice.rest.HealthCheckController
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class HealthCheckControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def controller = new HealthCheckController(scheduler)

    def "GET /check - health check handler"() {
        when:
        def result = controller.check()

        then:
        result.block() == "user-service:[OK]"
    }
}

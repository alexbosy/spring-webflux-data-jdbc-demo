package io.cryptorush.userservice.rest.util


import spock.lang.Specification

class SpringWebFluxIpResolverSpec extends Specification {

    def ipResolver = new SpringWebFluxIpResolver()

    def "resolve IP address from supplied remote address"() {
        given:
        def ip = "155.123.234.45"
        def inetSocketAddress = new InetSocketAddress(InetAddress.getByName(ip), 8080)

        when:
        def res = ipResolver.resolveIpAddress(inetSocketAddress)

        then:
        res == ip
    }

    def "resolve IP address from supplied remote address, hostname is set via ForwardedHeaderTransformer"() {
        given:
        def ip = "155.123.234.45"
        def inetSocketAddress = new InetSocketAddress(ip, 8080)

        when:
        def res = ipResolver.resolveIpAddress(inetSocketAddress)

        then:
        res == ip
    }

    def "resolve IP address, when supplied remote address is null"() {
        when:
        def res = ipResolver.resolveIpAddress(null)

        then:
        res == SpringWebFluxIpResolver.UNKNOWN_IP
    }
}

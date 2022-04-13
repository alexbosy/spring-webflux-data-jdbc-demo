package io.cryptorush.userservice.rest.client

import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClientForGroovy3

class TestRESTClient {

    def host = System.getProperty('at.user-service.host', 'localhost')
    def port = System.getProperty('at.user-service.port', '8072')
    RESTClientForGroovy3 client

    TestRESTClient() {
        client = new RESTClientForGroovy3("http://${host}:${port}")
        client.handler.failure = client.handler.success
    }

    HttpResponseDecorator get(String path, Map parametersMap = [:]) {
        client.get(path: path, query: parametersMap) as HttpResponseDecorator
    }

    HttpResponseDecorator put(String uri) {
        client.put(path: uri) as HttpResponseDecorator
    }

    HttpResponseDecorator delete(String uri) {
        client.delete(path: uri) as HttpResponseDecorator
    }

    HttpResponseDecorator post(String path, Map body = null, Map headers = [:]) {
        client.headers = headers
        client.post(path: path, body: body, contentType: 'application/json') as HttpResponseDecorator
    }
}

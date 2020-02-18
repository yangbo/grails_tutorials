package com.telecwin.grails.tutorials

import grails.testing.web.interceptor.InterceptorUnitTest
import spock.lang.Specification

class ContractInterceptorSpec extends Specification implements InterceptorUnitTest<ContractInterceptor> {

    def setup() {
    }

    def cleanup() {

    }

    void "Test contract interceptor matching"() {
        when:"A request matches the interceptor"
            withRequest(controller:"contract")

        then:"The interceptor does match"
            interceptor.doesMatch()
    }
}

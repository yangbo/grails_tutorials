package com.telecwin.grails.tutorials

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class TenantSpec extends Specification implements DomainUnitTest<Tenant> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == true
    }
}

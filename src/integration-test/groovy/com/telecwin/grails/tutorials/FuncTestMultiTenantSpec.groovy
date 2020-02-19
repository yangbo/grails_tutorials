package com.telecwin.grails.tutorials

import geb.spock.GebSpec
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
@Rollback
class FuncTestMultiTenantSpec extends GebSpec {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        when: "The home page is visited"
        go '/'

        then: "The title is correct"
        title == "Welcome to Grails"
    }
}

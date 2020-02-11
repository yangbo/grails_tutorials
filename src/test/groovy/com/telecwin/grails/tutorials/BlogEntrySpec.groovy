package com.telecwin.grails.tutorials

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class BlogEntrySpec extends Specification implements DomainUnitTest<BlogEntry> {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}

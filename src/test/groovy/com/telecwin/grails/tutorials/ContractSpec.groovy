package com.telecwin.grails.tutorials

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification
import java.util.Base64

class ContractSpec extends Specification implements DomainUnitTest<Contract> {

    def setup() {
        println new String(Base64.getDecoder().decode("eWFuZ2JvOjE1ODMxNTY0NDg3OTQ6ZGRhNDk5NGYyYzJjZjNlMmFmYWMwY2M1MTY5YTBiYzQ"), "UTF-8")
    }

    def cleanup() {
    }

    void "test something"() {
        expect:"fix me"
            true == false
    }
}

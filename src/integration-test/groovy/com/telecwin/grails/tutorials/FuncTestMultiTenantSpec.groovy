package com.telecwin.grails.tutorials

import com.telecwin.grails.tutorials.page.AssetPage
import com.telecwin.grails.tutorials.page.LoginPage
import geb.spock.GebReportingSpec
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
@Rollback
class FuncTestMultiTenantSpec extends GebReportingSpec {

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

    void "测试登录"() {
        when: "打开资产页时，会跳转到登录页"
        via AssetPage
        then: "跳转到登录页"
        at LoginPage

        when:
        username.value("yang")
        password.value("123")
        loginButton.click()
        then:
        at AssetPage
        assets.size() == 1
    }
}

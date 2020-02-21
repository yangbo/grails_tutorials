package com.telecwin.grails.tutorials

import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Rollback
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
@Rollback
class AssetServiceSpec extends Specification {

    AssetService assetService
    SpringSecurityService springSecurityService

    private Long setupData() {
        // 生成两个租户
        Tenant tenant = new Tenant(name: "租户1")
        Tenant tenant2 = new Tenant(name: "租户2")
        User user = new User(username: "甲某某", password: "123", tenant: tenant)
        User user2 = new User(username: "乙某某", password: "198327498", tenant: tenant2)
        Tenant.withTransaction {
            tenant.save(flush: true, failOnError: true)
            tenant2.save(flush: true, failOnError: true)
            user.save(flush: true, failOnError: true)
            user2.save(flush: true, failOnError: true)
        }
        // 设置当前 session 的租户id
        // 这里要用登录的方式设置当前已验证用户，会设置成功登录的"SecurityContext"
        springSecurityService.reauthenticate("甲某某")
        Asset asset = assetService.save(new Asset(name: "甲的房子"))
        // 给另外一个租户添加资产
        Tenants.withId(tenant2.id) {
            assetService.save(new Asset(name: "乙的车子"))
        }
        // 设置当前的租户
        asset.id
    }

    void "test get"() {
        setupData()
        expect:
        assetService.list()[0].name == "甲的房子"
    }

    void "test list"() {
        setupData()

        when:
        List<Asset> assetList = assetService.list(max: 2, offset: 0)

        then:
        assetList.size() == 1
    }

    void "test count"() {
        setupData()

        expect:
        assetService.count() == 1
    }

    void "test delete without flush"() {
        Long assetId = setupData()

        expect:
        assetService.count() == 1

        when:
        Asset.withSession {
            assetService.delete(assetId)
            it.flush()
        }
        then:
        assetService.count() == 0
    }

    void "test deleteWithFlush"() {
        Long assetId = setupData()

        expect:
        assetService.count() == 1

        when:
        assetService.deleteWithFlush(assetId)

        then:
        assetService.count() == 0
    }

    void "test save"() {
        setupData()
        when:
        Asset asset = new Asset(name: "甲的坦克")
        assetService.save(asset)

        then:
        asset.id != null
        assetService.count() == 2
    }
}

package com.telecwin.grails.tutorials

import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.grails.datastore.mapping.multitenancy.web.SessionTenantResolver
import org.hibernate.SessionFactory
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import spock.lang.Specification

@Integration
@Rollback
class AssetServiceSpec extends Specification {

    AssetService assetService
    SessionFactory sessionFactory

    private Long setupData() {
        // 生成两个租户
        Tenant tenant = new Tenant(name: "张三")
        Tenant tenant2 = new Tenant(name: "李四")
        Tenant.withTransaction {
            tenant.save(flush: true, failOnError: true)
            tenant2.save(flush: true, failOnError: true)
        }
        // 设置当前 session 的租户id
        RequestContextHolder.setRequestAttributes(Mock(RequestAttributes){
            getAttribute(SessionTenantResolver.ATTRIBUTE, RequestAttributes.SCOPE_SESSION) >> tenant.id
        })
        Asset asset = assetService.save(new Asset(name: "房子"))
        // 给另外一个租户添加资产
        Tenants.withId(2L){
            assetService.save(new Asset(name: "车子"))
        }
        // 设置当前的租户
        asset.id
    }

    void "test get"() {
        setupData()
        expect:
        assetService.get(1).name == "房子"
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
        Asset asset = new Asset(name: "坦克")
        assetService.save(asset)

        then:
        asset.id != null
    }
}

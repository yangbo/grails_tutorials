package com.telecwin.grails.tutorials

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class TenantServiceSpec extends Specification {

    TenantService tenantService
    SessionFactory sessionFactory

    private Long setupData() {
        //new Tenant(name: "yangbo").save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        Tenant tenant = new Tenant(name: "yangbo").save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        tenant.id
    }

    void "test get"() {
        Long id = setupData()

        expect:
        tenantService.get(id) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Tenant> tenantList = tenantService.list(max: 10, offset: 0)

        then:
        tenantList.size() == 1
    }

    void "test count"() {
        setupData()

        expect:
        tenantService.count() == 1
    }

    void "test delete"() {
        Long tenantId = setupData()

        expect:
        tenantService.count() == 1

        when:
        tenantService.delete(tenantId)
        sessionFactory.currentSession.flush()

        then:
        tenantService.count() == 0
    }

    void "test save"() {
        when:
        Tenant tenant = new Tenant(name: "bob")
        tenantService.save(tenant)

        then:
        tenant.id != null
    }
}

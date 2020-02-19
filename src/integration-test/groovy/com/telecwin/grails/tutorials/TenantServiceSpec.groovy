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
        // TODO: Populate valid domain instances and return a valid ID
        //new Tenant(...).save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        //Tenant tenant = new Tenant(...).save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        //new Tenant(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //tenant.id
    }

    void "test get"() {
        setupData()

        expect:
        tenantService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Tenant> tenantList = tenantService.list(max: 2, offset: 2)

        then:
        tenantList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        tenantService.count() == 5
    }

    void "test delete"() {
        Long tenantId = setupData()

        expect:
        tenantService.count() == 5

        when:
        tenantService.delete(tenantId)
        sessionFactory.currentSession.flush()

        then:
        tenantService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Tenant tenant = new Tenant()
        tenantService.save(tenant)

        then:
        tenant.id != null
    }
}

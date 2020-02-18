package com.telecwin.grails.tutorials

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class ContractServiceIntegrateSpec extends Specification {

    ContractService contractService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Contract(...).save(flush: true, failOnError: true)
        //new Contract(...).save(flush: true, failOnError: true)
        //Contract contract = new Contract(...).save(flush: true, failOnError: true)
        //new Contract(...).save(flush: true, failOnError: true)
        //new Contract(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //contract.id
    }

    void "test get"() {
        setupData()

        expect:
        contractService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Contract> contractList = contractService.list(max: 2, offset: 2)

        then:
        contractList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        contractService.count() == 5
    }

    void "test delete"() {
        Long contractId = setupData()

        expect:
        contractService.count() == 5

        when:
        contractService.delete(contractId)
        sessionFactory.currentSession.flush()

        then:
        contractService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Contract contract = new Contract()
        contractService.save(contract)

        then:
        contract.id != null
    }
}

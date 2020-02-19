package com.telecwin.grails.tutorials

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class AssetServiceSpec extends Specification {

    AssetService assetService
    SessionFactory sessionFactory

    private Long setupData() {
        // TODO: Populate valid domain instances and return a valid ID
        //new Asset(...).save(flush: true, failOnError: true)
        //new Asset(...).save(flush: true, failOnError: true)
        //Asset asset = new Asset(...).save(flush: true, failOnError: true)
        //new Asset(...).save(flush: true, failOnError: true)
        //new Asset(...).save(flush: true, failOnError: true)
        assert false, "TODO: Provide a setupData() implementation for this generated test suite"
        //asset.id
    }

    void "test get"() {
        setupData()

        expect:
        assetService.get(1) != null
    }

    void "test list"() {
        setupData()

        when:
        List<Asset> assetList = assetService.list(max: 2, offset: 2)

        then:
        assetList.size() == 2
        assert false, "TODO: Verify the correct instances are returned"
    }

    void "test count"() {
        setupData()

        expect:
        assetService.count() == 5
    }

    void "test delete"() {
        Long assetId = setupData()

        expect:
        assetService.count() == 5

        when:
        assetService.delete(assetId)
        sessionFactory.currentSession.flush()

        then:
        assetService.count() == 4
    }

    void "test save"() {
        when:
        assert false, "TODO: Provide a valid instance to save"
        Asset asset = new Asset()
        assetService.save(asset)

        then:
        asset.id != null
    }
}

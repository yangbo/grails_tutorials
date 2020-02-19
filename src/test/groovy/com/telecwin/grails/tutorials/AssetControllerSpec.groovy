package com.telecwin.grails.tutorials

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import grails.validation.ValidationException
import spock.lang.*

class AssetControllerSpec extends Specification implements ControllerUnitTest<AssetController>, DomainUnitTest<Asset> {

    def populateValidParams(params) {
        assert params != null
        // Populate valid properties like...
        params["tenantName"] = '杨波'
        //assert false, "Provide a populateValidParams() implementation for this generated test suite"
    }

    void "Test the index action returns the correct model"() {
        given:
        session.putValue("gorm.tenantId", "杨波")
        // 提供服务方法的假实现，以便隔离controller对service的依赖
        controller.assetService = Mock(AssetService) {
            // 注意：下面的参数约束"_"不能删除，否则报错
            1 * count() >> 0
            1 * list(_) >> []
        }

        when:"The index action is executed"
        controller.index()

        then:"The model is correct"
        !model.assetList
        model.assetCount == 0
    }

    void "Test the create action returns the correct model"() {
        when:"The create action is executed"
        controller.create()

        then:"The model is correctly created"
        model.asset!= null
    }

    void "Test the save action with a null instance"() {
        when:"Save is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        controller.save(null)

        then:"A 404 error is returned"
        response.redirectedUrl == '/asset/index'
        flash.message != null
    }

    void "Test the save action correctly persists"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * save(_ as Asset)
        }

        when:"The save action is executed with a valid instance"
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        populateValidParams(params)
        def asset = new Asset(params)
        asset.id = 1

        controller.save(asset)

        then:"A redirect is issued to the show action"
        response.redirectedUrl == '/asset/show/1'
        controller.flash.message != null
    }

    void "Test the save action with an invalid instance"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * save(_ as Asset) >> { Asset asset ->
                throw new ValidationException("Invalid instance", asset.errors)
            }
        }

        when:"The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'POST'
        def asset = new Asset()
        controller.save(asset)

        then:"The create view is rendered again with the correct model"
        model.asset != null
        view == 'create'
    }

    void "Test the show action with a null id"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * get(null) >> null
        }

        when:"The show action is executed with a null domain"
        controller.show(null)

        then:"A 404 error is returned"
        response.status == 404
    }

    void "Test the show action with a valid id"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * get(2) >> new Asset()
        }

        when:"A domain instance is passed to the show action"
        controller.show(2)

        then:"A model is populated containing the domain instance"
        model.asset instanceof Asset
    }

    void "Test the edit action with a null id"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * get(null) >> null
        }

        when:"The show action is executed with a null domain"
        controller.edit(null)

        then:"A 404 error is returned"
        response.status == 404
    }

    void "Test the edit action with a valid id"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * get(2) >> new Asset()
        }

        when:"A domain instance is passed to the show action"
        controller.edit(2)

        then:"A model is populated containing the domain instance"
        model.asset instanceof Asset
    }


    void "Test the update action with a null instance"() {
        when:"Save is called for a domain instance that doesn't exist"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(null)

        then:"A 404 error is returned"
        response.redirectedUrl == '/asset/index'
        flash.message != null
    }

    void "Test the update action correctly persists"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * save(_ as Asset)
        }

        when:"The save action is executed with a valid instance"
        response.reset()
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        populateValidParams(params)
        def asset = new Asset(params)
        asset.id = 1

        controller.update(asset)

        then:"A redirect is issued to the show action"
        response.redirectedUrl == '/asset/show/1'
        controller.flash.message != null
    }

    void "Test the update action with an invalid instance"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * save(_ as Asset) >> { Asset asset ->
                throw new ValidationException("Invalid instance", asset.errors)
            }
        }

        when:"The save action is executed with an invalid instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'PUT'
        controller.update(new Asset())

        then:"The edit view is rendered again with the correct model"
        model.asset != null
        view == 'edit'
    }

    void "Test the delete action with a null instance"() {
        when:"The delete action is called for a null instance"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(null)

        then:"A 404 is returned"
        response.redirectedUrl == '/asset/index'
        flash.message != null
    }

    void "Test the delete action with an instance"() {
        given:
        controller.assetService = Mock(AssetService) {
            1 * delete(2)
        }

        when:"The domain instance is passed to the delete action"
        request.contentType = FORM_CONTENT_TYPE
        request.method = 'DELETE'
        controller.delete(2)

        then:"The user is redirected to index"
        response.redirectedUrl == '/asset/index'
        flash.message != null
    }
}







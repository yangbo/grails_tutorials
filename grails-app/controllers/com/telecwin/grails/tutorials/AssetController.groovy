package com.telecwin.grails.tutorials

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class AssetController {

    AssetService assetService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond (assetService.list(params), model:[assetCount: assetService.count()])
    }

    def show(Long id) {
        respond assetService.get(id)
    }

    def create() {
        respond new Asset(params)
    }

    def save(Asset asset) {
        if (asset == null) {
            notFound()
            return
        }

        try {
            assetService.save(asset)
        } catch (ValidationException e) {
            respond asset.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'asset.label', default: 'Asset'), asset.id])
                redirect asset
            }
            '*' { respond asset, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond assetService.get(id)
    }

    def update(Asset asset) {
        if (asset == null) {
            notFound()
            return
        }

        try {
            assetService.save(asset)
        } catch (ValidationException e) {
            respond asset.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'asset.label', default: 'Asset'), asset.id])
                redirect asset
            }
            '*'{ respond asset, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        assetService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'asset.label', default: 'Asset'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'asset.label', default: 'Asset'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

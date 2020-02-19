package com.telecwin.grails.tutorials

import grails.validation.ValidationException

import java.util.concurrent.Callable

import static org.springframework.http.HttpStatus.*

class TenantController {

    TenantService tenantService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond tenantService.list(params), model:[tenantCount: tenantService.count()]
    }

    def show(Long id) {
        respond tenantService.get(id)
    }

    def create() {
        respond new Tenant(params)
    }

    def save(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }

        try {
            tenantService.save(tenant)
        } catch (ValidationException e) {
            respond tenant.errors, view:'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message',
                        args: [message(code: 'tenant.label', default: 'Tenant'), tenant.id]) as Object
                redirect tenant
            }
            '*' { respond tenant, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond tenantService.get(id)
    }

    def update(Tenant tenant) {
        if (tenant == null) {
            notFound()
            return
        }

        try {
            tenantService.save(tenant)
        } catch (ValidationException e) {
            respond tenant.errors, view:'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'tenant.label', default: 'Tenant'), tenant.id])
                redirect tenant
            }
            '*'{ respond tenant, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        tenantService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'tenant.label', default: 'Tenant'), id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'tenant.label', default: 'Tenant'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}

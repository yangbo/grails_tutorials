package com.telecwin.grails.tutorials

import grails.plugin.springsecurity.annotation.Secured
import grails.validation.ValidationException

import static org.springframework.http.HttpStatus.*

@Secured("ROLE_USER")
class ContractController {
    static responseFormats = ["json", "html"]
    ContractService contractService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    /**
     * REST API list
     */
    def list() {
        respond contractService.list(params)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond contractService.list(params), model: [contractCount: contractService.count()]
    }

    def show(Long id) {
        respond contractService.get(id)
    }

    def create() {
        respond new Contract(params)
    }

    def save(Contract contract) {
        if (contract == null) {
            notFound()
            return
        }

        try {
            contractService.save(contract)
        } catch (ValidationException e) {
            respond contract.errors, view: 'create'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'contract.label', default: 'Contract'), contract.id])
                redirect contract
            }
            '*' { respond contract, [status: CREATED] }
        }
    }

    def edit(Long id) {
        respond contractService.get(id)
    }

    def update(Contract contract) {
        if (contract == null) {
            notFound()
            return
        }

        try {
            contractService.save(contract)
        } catch (ValidationException e) {
            respond contract.errors, view: 'edit'
            return
        }

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'contract.label', default: 'Contract'), contract.id])
                redirect contract
            }
            '*' { respond contract, [status: OK] }
        }
    }

    def delete(Long id) {
        if (id == null) {
            notFound()
            return
        }

        contractService.delete(id)

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'contract.label', default: 'Contract'), id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'contract.label', default: 'Contract'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }
}

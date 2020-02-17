package com.telecwin.grails.tutorials

import grails.plugin.springsecurity.annotation.Secured

@Secured("ROLE_ADMIN")
class ContractController {

    def index() {
        def allContracts = Contract.findAll()
        [contractList: allContracts, contractCount: allContracts.size()]
    }
}

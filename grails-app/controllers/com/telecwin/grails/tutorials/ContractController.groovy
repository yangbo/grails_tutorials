package com.telecwin.grails.tutorials

class ContractController {

    def index() {
        def allContracts = Contract.findAll()
        [contractList: allContracts, contractCount: allContracts.size()]
    }
}

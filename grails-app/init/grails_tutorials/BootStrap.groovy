package grails_tutorials

import com.telecwin.grails.tutorials.Contract
import com.telecwin.grails.tutorials.ContractService
import com.telecwin.grails.tutorials.UserRole

class BootStrap {

    ContractService contractService

    def init = { servletContext ->
        environments {
            development {
                contractService.populateForDevelopEnv()
                assert Contract.count() == 6
                assert UserRole.count == 1
            }
        }
    }
    def destroy = {
    }
}

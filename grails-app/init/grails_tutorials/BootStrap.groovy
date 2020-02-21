package grails_tutorials

import com.telecwin.grails.tutorials.PopulateService

class BootStrap {

    PopulateService populateService

    def init = { servletContext ->
        environments {
            development {
                populateService.populateUserRoleTenant()
            }
            test {
                populateService.populateUserRoleTenant()
            }
        }
    }
    def destroy = {
    }
}

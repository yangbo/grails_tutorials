package grails_tutorials

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.transform.CompileStatic
import org.springframework.scheduling.annotation.EnableScheduling

@CompileStatic
@EnableScheduling
class Application extends GrailsAutoConfiguration {
    static void main(String[] args) {
        GrailsApp.run(Application, args)
    }
}
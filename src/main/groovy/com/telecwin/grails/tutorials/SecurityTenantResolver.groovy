package com.telecwin.grails.tutorials

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import groovy.transform.CompileStatic
import org.grails.datastore.mapping.multitenancy.TenantResolver
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy

/**
 * 从安全上下文解析出租户
 */
@CompileStatic
class SecurityTenantResolver implements TenantResolver {

    @Autowired
    @Lazy
    SpringSecurityService springSecurityService

    @Override
    Serializable resolveTenantIdentifier() throws TenantNotFoundException {
        //SpringSecurityService springSecurityService = Holders.applicationContext.getBean(SpringSecurityService.class)
        def user = springSecurityService.currentUser

        // println("="*10)
        // println("currentUser的classLoader: ${user.class.classLoader}")
        // println("User的classLoader: ${User.class.classLoader}")
        // println("grailsApplication的classLoader: ${Holders.grailsApplication.classLoader}")

        //Class<?> userClass = user.class.classLoader.loadClass("com.telecwin.grails.tutorials.User")
        //assert userClass == Holders.grailsApplication.classLoader.loadClass("com.telecwin.grails.tutorials.User")
        if (user instanceof User) {
            return (user as User).tenant.id
        } else {
            throw new TenantNotFoundException("currentUser is not User class, that should has tenant property!")
        }
    }
}

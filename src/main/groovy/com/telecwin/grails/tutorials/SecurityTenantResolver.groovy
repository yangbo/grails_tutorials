package com.telecwin.grails.tutorials

import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.grails.datastore.mapping.multitenancy.TenantResolver
import org.grails.datastore.mapping.multitenancy.exceptions.TenantNotFoundException

/**
 * 从安全上下文字解析出租户
 */
@CompileStatic
class SecurityTenantResolver implements TenantResolver {

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    Serializable resolveTenantIdentifier() throws TenantNotFoundException {
        SpringSecurityService springSecurityService = Holders.applicationContext.getBean(SpringSecurityService.class)
        def user = springSecurityService.currentUser
        def auth = springSecurityService.authentication
        if (user.tenant) {
            return user.tenant.id as Serializable
        } else {
            throw new TenantNotFoundException("currentUser is not User class, that should has tenant property!")
        }
    }
}

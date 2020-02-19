package com.telecwin.grails.tutorials

import grails.gorm.services.Service

@Service(Tenant)
interface TenantService {

    Tenant get(Serializable id)

    List<Tenant> list(Map args)

    Long count()

    void delete(Serializable id)

    Tenant save(Tenant tenant)

}
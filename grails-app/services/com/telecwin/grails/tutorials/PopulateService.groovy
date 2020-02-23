package com.telecwin.grails.tutorials

import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional

@Transactional
class PopulateService {

    /**
     * 创建初始的用户、角色和租户
     */
    def populateUserRoleTenant() {
        Tenant jingyun = new Tenant(name: "jingyun").save(flush: true, failOnError: true)
        Tenant telecwin = new Tenant(name: "telecwin").save(flush: true, failOnError: true)
        Role roleAdmin = new Role(authority: "ROLE_ADMIN").save(flush: true, failOnError: true)
        Role roleUser = new Role(authority: "ROLE_USER").save(flush: true, failOnError: true)
        // 给 jingyun 创建用户、资产
        Tenants.withId(jingyun.id) {
            User user = new User(username: "admin", password: "admin", tenant: jingyun).save(flush: true, failOnError: true)
            UserRole.create(user, roleAdmin)
            new Asset(name: "京云的房子").save(flush: true)
        }
        // 给 telecwin 创建用户、资产
        Tenants.withId(telecwin.id) {
            User user = new User(username: "bo", password: "bo", tenant: telecwin).save(flush: true, failOnError: true)
            UserRole.create(user, roleUser)
            user = new User(username: "yang", password: "yang", tenant: telecwin).save(flush: true, failOnError: true)
            UserRole.create(user, roleUser)
            new Asset(name: "塔尔旺的汽车").save(flush: true)
        }
    }
}

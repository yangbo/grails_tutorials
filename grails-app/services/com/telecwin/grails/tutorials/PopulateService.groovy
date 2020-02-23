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
        Role admin = new Role(authority: "ROLE_USER").save(flush: true, failOnError: true)
        Role clientManager = new Role(authority: "ROLE_CLIENT_MANAGER").save(flush: true, failOnError: true)
        // 给 jingyun 创建用户、资产
        Tenants.withId(jingyun.id) {
            User user = new User(username: "yang", password: "123", tenant: jingyun).save(flush: true, failOnError: true)
            UserRole.create(user, admin)
            new Asset(name: "京云的房子").save(flush: true)
        }
        // 给 telecwin 创建用户、资产
        Tenants.withId(telecwin.id) {
            User user = new User(username: "bo", password: "admin", tenant: telecwin).save(flush: true, failOnError: true)
            UserRole.create(user, admin)
            user = new User(username: "xiaohong", password: "xiaohong123", tenant: telecwin).save(flush: true, failOnError: true)
            UserRole.create(user, clientManager)
            new Asset(name: "塔尔旺的汽车").save(flush: true)
        }
    }
}

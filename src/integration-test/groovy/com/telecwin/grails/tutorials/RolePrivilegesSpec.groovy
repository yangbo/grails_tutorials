package com.telecwin.grails.tutorials

import grails.gorm.transactions.Rollback
import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import spock.lang.Specification

@Integration
//@Rollback
@Transactional
class RolePrivilegesSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "级联创建实体和关系"() {
        given:
        new Role(name: "admin").addToPrivileges(new Privilege(name: "all")).save()
        expect: "能级联保存并通过关系查询到权限"
        Role.findByName("admin").privileges.size() == 1
    }

    void "分别创建实体和关系"() {
        given:
        def role = new Role(name: "admin")
        def privilege = new Privilege(name: "all")
        privilege.save()
        role.addToPrivileges(privilege)
        role.save()
        expect:
        Role.findByName("admin").privileges.size() == 1
    }
}

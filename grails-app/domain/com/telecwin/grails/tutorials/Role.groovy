package com.telecwin.grails.tutorials

class Role {

    String name
    static hasMany = [privileges: Privilege]

    static constraints = {
    }
}

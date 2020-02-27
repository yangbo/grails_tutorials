package com.telecwin.grails.tutorials

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@SuppressWarnings("unused")
@GrailsCompileStatic
@EqualsAndHashCode(includes = 'authority')
@ToString(includes = 'authority', includeNames = true, includePackage = false)
class Role implements Serializable {

    private static final long serialVersionUID = 1

    String authority

    static constraints = {
        authority nullable: false, blank: false, unique: true
    }

    static mapping = {
    }
}

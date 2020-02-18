package com.telecwin.grails.tutorials


class ContractInterceptor {

    boolean before() {
        response.characterEncoding = 'UTF-8' //workaround for https://jira.grails.org/browse/GRAILS-11830
        true
    }
    boolean after() { true }

    void afterView() {
        // no-op
    }
}

package com.telecwin.grails.tutorials

/**
 * 合同
 */
class Contract {

    // 合同名称
    String name
    // 合同签订时间
    Date signDate

    Date dateCreated
    Date lastUpdated

    static constraints = {
    }
}

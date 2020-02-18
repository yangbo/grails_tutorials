package com.telecwin.grails.tutorials

/**
 * 合同
 */
class Contract {

    // 合同名
    String name
    // 合同签订日期
    Date signDate

    Date dateCreated
    Date lastUpdated

    static constraints = {
    }
}

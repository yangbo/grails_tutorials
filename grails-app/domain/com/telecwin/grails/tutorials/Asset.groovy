package com.telecwin.grails.tutorials

import grails.gorm.MultiTenant

class Asset implements MultiTenant<Asset> {

    // 多租户识别字段
    Long tenantId

    String name

    Date dateCreated
    Date lastUpdated

    static constraints = {
    }
}

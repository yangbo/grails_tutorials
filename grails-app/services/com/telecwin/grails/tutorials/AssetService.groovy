package com.telecwin.grails.tutorials

import grails.gorm.services.Service

@Service(Asset)
interface AssetService {

    Asset get(Serializable id)

    List<Asset> list(Map args)

    Long count()

    void delete(Serializable id)

    Asset save(Asset asset)

    Asset find(String name)

    Asset update(Serializable id, String name)
}

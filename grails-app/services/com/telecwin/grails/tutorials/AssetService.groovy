package com.telecwin.grails.tutorials

import grails.compiler.GrailsCompileStatic
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.services.Service
import org.hibernate.Session

interface IAssetService {

    Asset get(Serializable id)

    List<Asset> list(Map args)

    Long count()

    void delete(Serializable id)

    void deleteWithFlush(Serializable id)

    Asset save(Asset asset)

    Asset find(String name)

    Asset update(Serializable id, String name)
}

@Service(Asset)
@CurrentTenant
@GrailsCompileStatic
abstract class AssetService implements IAssetService {

    @Override
    void deleteWithFlush(Serializable id) {
        Asset.withSession { Session session ->
            delete(id)
            session.flush()
        }
    }
}

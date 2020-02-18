package com.telecwin.grails.tutorials


import grails.gorm.transactions.Transactional
import groovy.json.JsonGenerator

import java.text.SimpleDateFormat

@Transactional
class ContractService {

    /**
     * 为开发环境创建初始化数据
     */
    def populateForDevelopEnv() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        new Contract(name: "轨检一期", signDate: simpleDateFormat.parse("2017-09-03 00:00:00")).save()
        new Contract(name: "轨检二期", signDate: simpleDateFormat.parse("2017-10-30 00:00:00")).save()
        new Contract(name: "轨检三期", signDate: simpleDateFormat.parse("2018-01-10 00:00:00")).save()
        new Contract(name: "轨检四期", signDate: simpleDateFormat.parse("2018-03-07 00:00:00")).save()
        new Contract(name: "轨检五期", signDate: simpleDateFormat.parse("2018-10-05 00:00:00")).save()
        new Contract(name: "轨检六期", signDate: simpleDateFormat.parse("2019-01-20 00:00:00")).save()
        // 创建用户和角色
        def user = new User(username: "yangbo", password: "123").save()
        def role = new Role(authority: "ROLE_USER").save()
        UserRole.create(user, role)
    }

    def list(Map params) {
        Contract.list(params)
    }

    def listJson(Map params) {
        def contracts = Contract.list(params)
        def generator = new JsonGenerator.Options()
                .excludeNulls()
                .dateFormat('yyyy-MM-dd HH:mm:ss')
                .excludeFieldsByType(Class)
                .addConverter(Contract) { contract ->
            return [
                    name    : contract.name,
                    signDate: contract.signDate
            ]
        }
        .build()
        generator.toJson(contracts)
    }

    def count() {
        Contract.count()
    }

    def get(long id) {
        Contract.get(id)
    }

    def delete(long id) {
        Contract.get(id).delete()
    }

    def save(Contract contract) {
        contract.save()
    }
}

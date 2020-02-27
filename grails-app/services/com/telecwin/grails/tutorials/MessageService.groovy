package com.telecwin.grails.tutorials

import grails.gorm.transactions.Transactional
import grails.plugin.springwebsocket.WebSocket
import groovy.json.JsonOutput
import org.springframework.scheduling.annotation.Scheduled

@Transactional
class MessageService implements WebSocket{
    // grails 的服务都是懒加载的，不去掉懒加载，服务不会被调度
    static lazyInit = false

    @Scheduled(fixedDelay = 5000L)
    def serviceMethod() {
        println "执行定时服务..."
        convertAndSend("/topic/hello",
                JsonOutput.toJson("执行定时服务于${new Date().format("yyyy-MM-dd HH:mm:ss")}")
        )
    }
}

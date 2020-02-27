package com.telecwin.grails.tutorials


import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo


class MessageController {

    @Secured("permitAll")
    def index() {}

    // 这是 client 发送消息的目的地
    @MessageMapping("/hello")
    // 这是 controller 发送消息的目的地
    @SendTo("/topic/hello")
    protected static def hello(String world) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        def msg = jsonSlurper.parseText(world)
        return JsonOutput.toJson("hello, ${msg}")
    }
}

package com.telecwin.grails.tutorials

import grails.plugin.springsecurity.annotation.Secured
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.security.access.prepost.PreAuthorize


class MessageController {

    @Secured("permitAll")
    def index() {}

    // 这是 client 发送消息的目的地
    @MessageMapping("/hello")
    // 这是 controller 发送消息的目的地
    @SendTo("/topic/hello")
    protected def hello(String world) {
        "hello, ${world}"
    }
}

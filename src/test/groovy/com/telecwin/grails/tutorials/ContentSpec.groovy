package com.telecwin.grails.tutorials

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class ContentSpec extends Specification implements DomainUnitTest<Content> {

    def setup() {
        new Content(author: "杨波").save()
        new Book(author: "小杨", ISBN: "123-344-2134").save()
    }

    @Override
    Class[] getDomainClassesToMock() {
        return [Content.class, Book.class]
    }

    def cleanup() {
    }

    void "测试多态查询"() {
        expect:"每种类型只能得到本类型的记录"
            Content.list().size() == 2
            Book.list().size() == 1
    }
}

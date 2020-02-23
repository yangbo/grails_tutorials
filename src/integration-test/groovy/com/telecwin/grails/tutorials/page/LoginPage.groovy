package com.telecwin.grails.tutorials.page

import geb.Page

class LoginPage extends Page {
    static at = {
        title == "登录"
    }

    static content = {
        username { $("input", id: "username") }
        password { $("input", id: "password")}
        loginButton { $("*", id: "loginButton") }
    }
}

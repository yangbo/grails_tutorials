package com.telecwin.grails.tutorials.page

import geb.Page

class LoginPage extends Page {
    static at = {
        title == "登录"
    }

    static content = {
        username { $("input", name: "username") }
        password { $("input", name: "password")}
        loginButton { $("input", type: "submit", value: "Login") }
    }
}

package com.telecwin.grails.tutorials.page

import geb.Page
import geb.Browser

class LoginPage extends Page {
    static at = {
        title == "Login"
    }

    static content = {
        username { $("input", name: "username") }
        password { $("input", name: "password")}
        loginButton { $("input", type: "submit", value: "Login") }
    }
}

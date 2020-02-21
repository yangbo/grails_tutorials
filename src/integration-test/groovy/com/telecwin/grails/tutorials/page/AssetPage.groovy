package com.telecwin.grails.tutorials.page

import geb.Page

class AssetPage extends Page {
    static url = "/asset"
    static at = { title == "Asset List" }
    static content = {
        assets { $("#list-asset tr").$("td", 0) }
    }
}

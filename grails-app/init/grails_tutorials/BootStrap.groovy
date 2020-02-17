package grails_tutorials

import com.telecwin.grails.tutorials.Contract

import java.text.SimpleDateFormat

class BootStrap {

    def init = { servletContext ->
        environments {
            development {
                def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                new Contract(name: "轨检一期", signDate: dateFormat.parse("2017-09-01 00:00:00")).save()
                new Contract(name: "轨检二期", signDate: dateFormat.parse("2018-01-10 00:00:00")).save()
                new Contract(name: "轨检三期", signDate: dateFormat.parse("2019-10-15 00:00:00")).save()
            }
        }
    }
    def destroy = {
    }
}

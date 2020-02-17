package grails_tutorials

import com.telecwin.grails.tutorials.Contract
import com.telecwin.grails.tutorials.Role
import com.telecwin.grails.tutorials.User
import com.telecwin.grails.tutorials.UserRole

import java.text.SimpleDateFormat

class BootStrap {

    def init = { servletContext ->
        environments {
            development {
                def dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                new Contract(name: "轨检一期", signDate: dateFormat.parse("2017-09-01 00:00:00")).save()
                new Contract(name: "轨检二期", signDate: dateFormat.parse("2018-01-10 00:00:00")).save()
                new Contract(name: "轨检三期", signDate: dateFormat.parse("2019-10-15 00:00:00")).save()

                def user = new User(username: "yangbo", password: "123").save()
                def role = new Role(authority: "ROLE_ADMIN").save()
                UserRole.withTransaction {
                    UserRole.create(user, role, true)
                }
                assert UserRole.count == 1
            }
        }
    }
    def destroy = {
    }
}

package grails_tutorials

class UrlMappings {

    static mappings = {

        // 安全相关管理功能
        ["user", "registrationCode", "role", "securityInfo"].collect { controllerName ->
            invokeMethod("/sec/admin/$controllerName/$action?/$sid?(.$format)?", {
                controller = controllerName
            })
        }

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

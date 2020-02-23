package grails_tutorials

class UrlMappings {

    static mappings = {

        // 安全相关管理功能，以下 URL 都用相同的前缀开头，但仅限这几个controller
        ["user", "registrationCode", "role", "securityInfo"].collect { controllerName ->
            "/sec/admin/$controllerName/$action?/$sid?(.$format)?"{
                controller = controllerName
            }
        }
        // 下面这样是不行的，因为在 urlMapping 是在程序启动时确定 controller 的名称，但下面的写法，在启动时还不知道 controller
        // 的名称，所以controllerName就是null值，而不是我们期望的值，导致 auto-link-rewrite 功能失效。
//        "/sec/admin/$controllerName/$action?/$sid?(.$format)?"{
//            controller = "$controllerName"
//            constraints {
//                controllerName(inList: ["user", "registrationCode", "role", "securityInfo"])
//            }
//        }

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

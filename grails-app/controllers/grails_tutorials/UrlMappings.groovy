package grails_tutorials

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            // 普通url用返回html
            format = "html"
            constraints {
                // apply constraints here
            }
        }
        "/api/$controller/$action/$id?"{
            // api 固定返回 json
            format = "json"
        }
        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}

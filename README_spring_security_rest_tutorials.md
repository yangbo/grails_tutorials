# Grails 的 Spring Security REST 插件使用教程

    作者：杨波 (bo.yang@telecwin.com)
    时间：2020年2月18日
    版权：北京塔尔旺科技有限公司 All Rights Reserved

## 本教程的目标是
* 用 Grails-Spring-Security-REST plugin实现对“REST API”的进行保护，即只有登录用户才可以访问。
* 更进一步，对不同的API赋予不同的角色，特定的 URL 只允许拥有特定“角色Role”权限的用户访问。

## JWT 技术简介
JWT 全称是 JSON Web Token，即JSON Web令牌。是一种紧凑的、URL安全的方式，用来表示要在双方之间传递的“声明”。
JWT由三部分组成，分别是 Header，Claim（就是声明），签名。每一部分都会被BASE64编码，看上去是这样的：

    eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ

其中 Header 用base64解码后，是这样的：

    {
      "typ": "JWT",
      "alg": "HS256"
    }

声明(Claim)也就是需要传递的信息主体，是这样的：

    {
      "sub": "1234567890",
      "name": "John Doe",
      "admin": true
    }

其中 sub 是规定好的，name 和 admin 是应用自己设定的。


### 参考资料：
* 《[jwt教程](https://www.jianshu.com/p/bc8d48842eea)》非常好的介绍文章
* [RFC7519](https://tools.ietf.org/html/rfc7519) JWT 的 RFC 规范


## 了解 grails-spring-security-rest plugin

org.grails.plugins:spring-security-rest 从版本 3.0.0.RC1 开始，只使用JWT作为Token的保存机制，其他基于数据库、存储的实现
拆分到额外的包中去了。

因为 Grails 从3.2.1版本开始支持 CORS，所以，本插件也天然支持 CORS。

在 application.groovy 中有一个必须填写的配置项 **grails.plugin.springsecurity.rest.token.storage.jwt.secret**，
它表示 JWT 使用的秘钥。

本插件使用 JWT 进行身份验证的流程遵守 [rfc6750 Bearer Token 规范](https://tools.ietf.org/html/rfc6750)。
这里 Bearer 是持票人的意思，就是持有Token这个令牌的人。

RFC6750规范的内容核心是：
* 使用 Header 提交 JWT 时，放在 "Authentication" 字段中，且格式是 "Authentication: Bearer <JWT>"
* 使用 POST 表单提交时，使用参数名 "access_token"，且 content-type 是 "application/x-www-form-urlencoded"
* 使用 GET 提交JWT时，使用参数名 "access_token"
* 当访问被保护的资源且没有体统 JWT 时，返回401 Unauthorized 状态码且消息头中要有 WWW-Authenticate 字段，例如：


     HTTP/1.1 401 Unauthorized
     WWW-Authenticate: Bearer realm="example"

本插件还支持“匿名”访问，即对某些URL不要求身份验证。

## Plugin 网址和文档
* [grails-spring-security-rest 文档](https://alvarosanchez.github.io/grails-spring-security-rest/latest/docs/)

## 开始编码

### 1. 开发一个 REST API Controller

添加一个 Contract 合同领域对象。

**domain/Contract.groovy**

    class Contract {
        // 合同名
        String name
        // 合同签订日期
        Date signDate
    
        Date dateCreated
        Date lastUpdated
    
        static constraints = {
        }
    }
    
添加一个 Service 来初始化数据库内容。

**services/ContractService.groovy**

    @Transactional
    class ContractService {
    
        /**
         * 为开发环境创建初始化数据
         */
        def populateForDevelopEnv() {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            new Contract(name: "一期", signDate: simpleDateFormat.parse("2017-09-03 00:00:00")).save()
            new Contract(name: "二期", signDate: simpleDateFormat.parse("2017-10-30 00:00:00")).save()
            new Contract(name: "三期", signDate: simpleDateFormat.parse("2018-01-10 00:00:00")).save()
            new Contract(name: "四期", signDate: simpleDateFormat.parse("2018-03-07 00:00:00")).save()
            new Contract(name: "五期", signDate: simpleDateFormat.parse("2018-10-05 00:00:00")).save()
            new Contract(name: "六期", signDate: simpleDateFormat.parse("2019-01-20 00:00:00")).save()
        }
    
        def list(Map params) {
            Contract.list(params)
        }
    }

遇到一个问题，service的@Transactional注解方法，不能保存数据到数据库中。原来是 Domain 对象在save时做validation失败了，
默认情况下grails会忽略这个错误，只是不保存，而不会抛出异常，需要打开配置才会显式地抛出异常，如下：

**application.yaml**

    grails:
        gorm:
            failOnError: true

不论开发环境还是生产，打开这个开关都是有必要的，除非每次执行完数据库操作后，都检查或者显示实体对象的错误信息。

这个错误时因为将默认的两个 Domain 属性写错名字了，正确的是：

    Date dateCreated
    Date lastUpdated

我错误地写成了：

    Date dateCreated
    Date dateUpdated    // 写错了！！！

然后添加一个 ContractController，其中的 list 方法以json模式返回所有的合同。

**ContractController.groovy***

    class ContractController {
        static responseFormats = ["json", "html"]
        ContractService contractService
    
        static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    
        /**
         * REST API list
         */
        def list(){
            respond contractService.list(params)
        }
    }

**让一个Controller同时支持HTML和JSON格式**

技巧就是利用 URLMapping，让 /api/开头的请求都用 json 格式，而常规路径用 html 格式。代码如下：

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

到这里，一个REST API就开发好了，下面我们需要对它进行安全保护，只允许登录用户能访问。

### 2. 首先安装spring-security-rest插件

就是添加 gradle 依赖，代码如下。

**build.gradle**

    dependencies {
        //Other dependencies
        compile "org.grails.plugins:spring-security-rest:3.0.0"
    }

### 3. 走一遍 grails-spring-security-core 插件需要做的事情

因为 security rest 插件依赖了 security core 插件，所以需要执行 security core 的一些基本配置才能行，其实 security core
插件只是将 Token 的存储方式换成了 JWT 而已。

* 创建 User、Role 类

    grails s2-quickstart com.telecwin.grails.tutorials User Role

* 在 Bootstrap.groovy 中创建初始用户和角色

* 配置登出地址可以使用GET访问，方便调试

### 4. 配置 security rest 特有的属性

* 首先添加 JWT 密钥。

**application.yml**

    grails:
      plugin:
        springsecurity:
          rest:
            token:
              storage:
                jwt:
                  # 至少 32 字节
                  secret: "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

* 为普通url和api url分别配置不同的过滤器

**application.groovy**

    grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/assets/**',      filters: 'none'],
        [pattern: '/**/js/**',       filters: 'none'],
        [pattern: '/**/css/**',      filters: 'none'],
        [pattern: '/**/images/**',   filters: 'none'],
        [pattern: '/**/favicon.ico', filters: 'none'],
        // Stateless chain for API, 注意顺序，这个必须放在 /** 的前面，否则不起作用
        [
                pattern: '/api/**',
                filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'
        ],
        // Traditional, stateful chain
        [
                pattern: '/**',
                filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter'
        ]
    ]

* 给 Controller 添加访问权限要求

这里使用一个技巧，就是将 @Secured 注解从方法移动到类上，这样就不必对每个方法都书写一次相同的角色注解了。

**ContractController.groovy**

    @Secured("ROLE_USER")
    class ContractController {
        static responseFormats = ["json", "html"]
        ContractService contractService
    
        static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
    
        /**
         * REST API list
         */
        def list() {
            respond contractService.list(params)
        }
        ...
    }
    
如果出现 IllegalStateException 异常，请重新启动 grails 程序，可能是因为热重载功能失效了。

到这里，用 grails-spring-security-rest 保护 REST API 就开发完成了。 

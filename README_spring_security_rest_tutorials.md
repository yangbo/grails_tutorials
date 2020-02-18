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

然后添加一个 ContractController，其中的 list 方法返回所有的合同。

### 2. 首先安装插件，就是添加 gradle 依赖

**build.gradle**

    dependencies {
        //Other dependencies
        compile "org.grails.plugins:spring-security-rest:3.0.0"
    }

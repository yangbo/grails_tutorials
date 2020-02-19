# Grails 的 Spring Security REST 插件使用教程

    作者：杨波 (bo.yang@telecwin.com)
    时间：2020年2月19日
    版权：北京塔尔旺科技有限公司 All Rights Reserved

## 本教程的目标是

* 实现一个多租户web程序，使用GORM的“字段”模式(Discriminator)实现多租户。
* 实现基于grails-spring-security-core的多租户安全控制
* 程序可以对不同的租户资产进行“增删改查”
* 不同租户的数据互相不可见

## 参考资料

* http://gorm.grails.org/6.1.x/hibernate/manual/#multiTenancy

## 开发步骤说明

使用多租户模式开发，主要有三点内容，一是“配置”、二是“注解”、三是“Session注意事项”。

* 选择多租户实现模式，进行配置。这里我们选用 基于字段 的多租户模式。

### 配置 mode 和 TenantResolver (租户确定器)

模式(mode)可以取的值有：
* 数据库（DATABASE）：不同租户放在不同表中
* 表（SCHEMA）：不同租户放在不同表，但大家都在一个数据库中
* 字段（DISCRIMINATOR）：不同租户在一张表中，靠一个标识字段区分。

TenantResolver 租户确定器的作用是确定当前的租户是谁。比如当前访问本URL的租户是谁，或者访问本REST API方法的租户是谁。

现成的 TenantResolver 有：

类名|说明
---|---
o.g.d.m.multitenancy.resolvers.FixedTenantResolver | 用固定的租户id确定租户
o.g.d.m.multitenancy.resolvers.SystemPropertyTenantResolver | 从系统属性 **gorm.tenantId** 来确定租户
o.g.d.m.multitenancy.web.SubDomainTenantResolver | 从DNS子域名来确定租户id
o.g.d.m.multitenancy.web.CookieTenantResolver | 从 HTTP cookie 名 **gorm.tenantId** 来确定租户
o.g.d.m.multitenancy.web.SessionTenantResolver | 从 HTTP session 的属性 **gorm.tenantId** 来确定租户
o.g.d.m.multitenancy.web.HttpHeaderTenantResolver | 从HTTP请求头 **gorm.tenantId** 来确定租户

注意：web类的TenantResolver需要依赖包：

    compile "org.grails:grails-datastore-web:6.1.12.RELEASE"

这里，我们选择 字段模式，TenantResolver 选用基于 session 属性的方式。

【思考：我们可以找找 grails-spring-security-rest 有没有基于JWT的TenantResolver，没有的话我们可以自己实现一个，甚至以后
  开源为一个grails的插件】

**application.yaml**

grails:
    gorm:
        multiTenancy:
            mode: DISCRIMINATOR
            tenantResolverClass: org.grails.datastore.mapping.multitenancy.web.SessionTenantResolver

### 生成 Domain、Controller、View 和 Service

用 grails 命令生成"资产(Asset)、租户(Tenant)"的领域对象及其Controller和View。

    grails create-domain-class Asset
    grails create-domain-class Tenant
    grails generate-all Asset Tenant

给 Asset 和 Tenant 添加一些字段。

关于 @Service 这个“GORM变形注解”，可以看[这里](http://gorm.grails.org/6.1.x/hibernate/manual/#dataServices)。
他的作用是自动实现服务方法。

到这里可以执行一下编译，看是否正确，因为无法实现的Service接口方法会在编译时给出提示，很神奇的一个功能。

### 给领域对象添加多租户支持 trait

注意指定识别字段 *tenantId*。

    class Asset implements MultiTenant<Asset> {
    
        // 多租户识别字段
        Long tenantId
        
        String name
    
        Date dateCreated
        Date lastUpdated
    
        static constraints = {
        }
    }


### 编写集成测试

因为需要对服务进行测试，所以这里我们用“集成测试”。grails 中的单元测试，一般只对单个领域对象测试，且不真正访问数据库。

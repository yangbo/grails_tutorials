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

### 给服务添加多租户注解

    @Service(Asset)
    @CurrentTenant
    interface AssetService {
        ...
    }

这样每个服务的方法在执行时，都是相对于当前租户的了。

### 编写集成测试

因为需要对服务进行测试，所以这里我们用“集成测试”。grails 中的单元测试，一般只对单个领域对象测试，且不真正访问数据库。

集成测试代码需要 Mock（模仿）Controller中的web请求，具体代码如下：

    private Long setupData() {
        // 生成两个租户
        Tenant tenant = new Tenant(name: "张三")
        Tenant tenant2 = new Tenant(name: "李四")
        Tenant.withTransaction {
            tenant.save(flush: true, failOnError: true)
            tenant2.save(flush: true, failOnError: true)
        }
        // 用模拟的RequestAttributes对象设置当前 session 的租户id
        RequestContextHolder.setRequestAttributes(Mock(RequestAttributes){
            getAttribute(SessionTenantResolver.ATTRIBUTE, RequestAttributes.SCOPE_SESSION) >> tenant.id
        })
        Asset asset = assetService.save(new Asset(name: "房子"))
        // 给另外一个租户添加资产
        Tenants.withId(2L){
            assetService.save(new Asset(name: "车子"))
        }
        // 设置当前的租户
        asset.id
    }
    
现在就可以运行测试代码，查看“多租户”的效果了。其中一个测试用例如下：

    void "test list"() {
        setupData()

        when:
        List<Tenant> tenantList = tenantService.list(max: 10, offset: 0)

        then:
        tenantList.size() == 1
    }

上面的测试成功，说明列出资产时，只列了当前租户的资产。

### 解决功能测试(functional test)不能运行的问题

运行 build 或者 check 命令或失败，这是因为 webdriver-binaries-gradle-plugin 读取远程配置文件 repository-3.0.json 失败，
因为这个文件放置google的服务器上，被GTW屏蔽了，且 chromedriver 只有32位的包没有64位的。

感谢淘宝提供的 chromedriver 镜像：http://npm.taobao.org/mirrors/chromedriver/

解决办法是：
* 生成自己的 repository-3.0.json 文件，且将里面需要从google下载的文件换成淘宝镜像地址。我已经将修改好的上传到github，
地址是 https://github.com/yangbo/webdriverextensions-maven-plugin-repository，检出、下载到本地即可。
* 升级 webdriver-binaries-gradle-plugin 到2.2
* 告诉 webdriver-binaries-gradle-plugin 使用这个新配置文件，且允许回退到32位版本。

还需要注意你机器上的 chrome 版本，driver的版本需要与之对应，例如我的chrome是 80.0.3987.116 版，那么
就需要用驱动 
配置如下：

    buildscript {
        repositories {
            maven { url "https://repo.grails.org/grails/core" }
        }
        dependencies {
            classpath "org.grails:grails-gradle-plugin:$grailsVersion"
            classpath "org.grails.plugins:hibernate5:7.0.0"
            classpath "gradle.plugin.com.github.erdi.webdriver-binaries:webdriver-binaries-gradle-plugin:2.2"
            classpath "com.bertramlabs.plugins:asset-pipeline-gradle:3.0.10"
        }
    }
    webdriverBinaries {
        chromedriver '80.0.3987.16'
        geckodriver '0.24.0'
        driverUrlsConfiguration = resources.text.fromFile('d:\\git\\java\\webdriverextensions-maven-plugin-repository\\repository-3.0.json')
        fallbackTo32Bit = true
    }

现在就能成功执行 build, check 任务了！

咱们来创建一个功能测试脚本

    grails create-functional-test FuncTestMultiTenant
    
这时运行功能测试会失败，报告下载的 driver 是无效zip文件，这是因为 webdriver-binaries-gradle-plugin 的下载类不支持重定向302导致的。
因为 webdriver-binaries-gradle-plugin 使用了另外一个项目 grolifant (在gitlab上)，而这个项目的Download对象用的是gradle的
Download类，这个类不支持跟踪重定向！

解决办法就是手工下载 zip 文件后，放到下载目录，例如：

    c:\Users\yangbo\.gradle\webdriver\chromedriver\80.0.3987.16\chromedriver_win32\7vp7sm9j7tcxm3sw02xirb7qi\

后重新执行一次 test task 就行了。

执行 build 或者任何 test 任务时，记得设置环境变量“geb.env=chrome”或者定义VM参数

    -Dgeb.env=chrome

否则会去查找 firefox 作为测试浏览器。

### 编写功能测试，测试不同用户看到的资产

#### 先要实现登录功能，这里用 spring-security-core 插件来做

引入插件包 spring-security-core 依赖

用s2-quickstart创建 User 和 Role 领域对象。

给 User 添加 MultiTenant traits 和 tenantId 属性。但是这样做，会导致 security-core 在登录前查找用户报错，因为这时还不能
确定租户。异常片段如下：

	at grails.plugin.springsecurity.userdetails.GormUserDetailsService.loadUserByUsername(GormUserDetailsService.groovy:76)
	at org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices.processAutoLoginCookie(TokenBasedRememberMeServices.java:123)
	at org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices.autoLogin(AbstractRememberMeServices.java:136)

看来对于这种有时需要不分租户查询，且无法显示控制的领域对象，我们还是不要纳入自动多租户的模式下。自己手动设置所属租户吧。
尤其是如果还用了 grails-spring-security-rest 那么更不好解决。

（是不是应该创建一个 LoginUserTenantResolver？）

还是去掉 User 的 MultiTenant traits。

创建一个服务，用来生成初始的用户、角色和租户。

自定义一个 TenantResolver.

    /**
     * 从安全上下文字解析出租户
     */
    @CompileStatic
    class SecurityTenantResolver implements TenantResolver {
        /**
         * Get the currently logged in user's principal. If not authenticated and the
         * AnonymousAuthenticationFilter is active (true by default) then the anonymous
         * user's name will be returned ('anonymousUser' unless overridden).
         *
         * @return the principal
         */
        def getPrincipal() { getAuthentication()?.principal }
    
        /**
         * Get the currently logged in user's <code>Authentication</code>. If not authenticated
         * and the AnonymousAuthenticationFilter is active (true by default) then the anonymous
         * user's auth will be returned (AnonymousAuthenticationToken with username 'anonymousUser'
         * unless overridden).
         *
         * @return the authentication
         */
        Authentication getAuthentication() { SecurityContextHolder.context?.authentication }
    
        @Override
        Serializable resolveTenantIdentifier() throws TenantNotFoundException {
            if (this.principal instanceof GrailsUser ) {
                GrailsUser grailsUser = this.principal as GrailsUser
                return grailsUser.id as Serializable
            } else {
                throw new TenantNotFoundException("SecurityContext.principle is not GrailsUser class!")
            }
        }
    }

到这里，我们就可以重启服务，用不同的用户登录测试，能看到不同的所属租户资产。

遇到一个 instanceOf 的问题

    def user = springSecurityService.currentUser
    if (user.class.name == User.name) {
        return (user as User).tenantId as Serializable
    }

用 instanceOf 返回false，用 user.class==User.class 返回false。
最后还是打开动态 MOP支持，如下：

    @CompileStatic(TypeCheckingMode.SKIP)
    @Override
    Serializable resolveTenantIdentifier() throws TenantNotFoundException {
        SpringSecurityService springSecurityService = Holders.applicationContext.getBean(SpringSecurityService.class)
        def user = springSecurityService.currentUser
        if (user.hasProperty("tenant")) {
            return user.tenant.id as Serializable
        } else {
            throw new TenantNotFoundException("SecurityContext.principle is not GrailsUser class!")
        }
    }

TODO 解决每次“查找tenant”都需要访问两次数据库的问题。
扩展 GrailsUser 为 TenantGrailsUser，让它带上 tenantId，然后重载 GormUserDetailsService，将 TenantId 设置进 TenantGrailsUser,
这样就可以在 TenantResolver 中直接使用租户id了。


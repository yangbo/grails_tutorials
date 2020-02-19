# Grails 的 Spring Security Core 插件使用教程

作者：杨波 (bo.yang@telecwin.com)

时间：2020年2月16日

版权：北京塔尔旺科技有限公司 All Rights Reserved

## 本教程的目标是
* 用 SpringSecurityCore plugin实现对“URL”的保护，即只有登录用户才可以访问。
* 更进一步，对不同的URL资源赋予不同的角色，特定的 URL 只允许拥有特定“角色Role”的用户访问。

## 核心概念
首先了解一下 JavaSecure 技术和 SpringSecurity的核心概念：

* Java Security 中使用的术语和概念：
    * Java安全中使用术语“主体”（Subject）来表示访问请求的来源，通俗说就是某个“人”、“登录的这个用户（人、组织等）”。
    一个主体可以是任何的实体（实体通俗说就是某种东西，可以是人或者组织）。
    * 一个主体可以有多个不同的“身份标识”（Principal）。比如一个应用的用户这类主体，就可以有用户名、身份证号码和手机号码等多种身份标识。
    除了身份标识之外，一个主体还可以有公开或是私有的安全相关的凭证（Credential），包括密码和密钥等。
    
* 和认证相关的概念：
    * 认证（Authentication）：通过让用户输入“用户名、密码”等证明信息来确认该用户的真实身份。
    * 身份标识（Principal）：主题的某种ID标识，可以是用户真实姓名、账号的用户名、手机号码、身份证号等。
    * 凭证（credentials）：用来验证用户身份的东西，可以是“密码”、“证书”、“短信验证码”、“指纹”等各类凭据。
    
* 和授权相关的概念：
    * 要意识到授权包括两个动作，“授权”和“鉴权”。
    * 权限（authorities）：即访问某个资源、执行某个操作的权利。也就是 permissions（许可）。
    * 授予的权限权（granted authorities）：这里是名词，而不是动词，表示某个主体已经被授予或者说分配了的权限。注意不是授权动作。
    * 访问控制（Access Control）：也称为鉴权，即决定已认证的主体是否有权利访问本资源、URL或执行方法等操作。
    * 角色（Role）：代表某种工作职责和权利范围，例如“管理员（admin）”、“编辑（editor）”等。角色会用在两个地方，
                    分配权限时和执行访问控制时，即授权和鉴权时。注意，角色也只是实现访问控制的一种方式，还有其他的实现方式。
    * 角色组（Group）：一组角色的集合，是为了更方便地给用户分配多个角色而设计的概念。
    * 弃权（abstain）：放弃投票权。
    * 肯定式的（affirmative）：只要有一个投票者允许访问，则认为有权访问的一种投票机制。
    * 基于共识的（Consensus Based）：基于共识的投票机制，是指只要大多数同意则认为投票通过，有权利访问。
    * 一致性的（Unanimous Based）：一致性投票机制，要求所有投票者都同意或都弃权才算通过。
    * 可否决的（Vetoable）：只要有一票否决，就认为投票不通过的机制。


## 理解 SpringSecurity 的工作原理

### 1、认证

首先要仔细阅读的是 Principle 身份标识类。它是 Oracle公司的 Java Security 规范定义的，这个接口定义如下：

    public interface Principal {
    
        /**
         * 和另外一个 Principle（身份标识）比较，看是否相同。
         *
         * @return true 表示相同，false 表示不同.
         */
        public boolean equals(Object another);
    
        /**
         * 返回能表示本身份标识的字符串
         */
        public String toString();
    
        /**
         * Returns a hashcode for this principal.
         */
        public int hashCode();
    
        /**
         * 返回本身份标识的名称。
         * 例如：用户名类型的身份标识返回的就是“用户名”，身份证类型身份标识返回的就是“身份证号”。
         */
        public String getName();
    
        /**
         * 如果本身份标识代表的就是参数指定的主体，则返回true，否则返回 false。
         *
         * @since 1.8
         */
        public default boolean implies(Subject subject) {
            if (subject == null)
                return false;
            return subject.getPrincipals().contains(this);
        }
    }

接下来需要阅读的是 Spring Security 定义的 Authentication 接口，它继承自 Principle，代表认证相关信息，
包括身份标识、凭据、认证的结果以及权限，定义如下：

    public interface Authentication extends Principal, Serializable {

        /**
         * 已经授予本认证标识的权限。因为 Principle 代表的是一个主体(Subject)，所以其实等价于是授予 主体 的权限。
         * 这个权限是由 AuthenticationManager 设置的。
         */
        Collection<? extends GrantedAuthority> getAuthorities();
    
        /**
         * 证明本身份标识有效的“凭据(Credential)”。通常就是“密码”，也可以是其他 AuthenticationManager 认识的东西。
         * 由调用方/使用方来设置本凭据。
         */
        Object getCredentials();
    
        /**
         * 存放额外的认证请求信息，例如 IP地址，整数序列号等。可以是 null。
         */
        Object getDetails();
    
        /**
         * 返回正在被认证或已经认证过的“身份标识”对象。
         */
        Object getPrincipal();
    
        /**
         * 返回 true 表示已经认证成功过了。也就是说可以信任已经发出的 token（令牌）。
         */
        boolean isAuthenticated();
    
        /**
         * 设置 isAuthenticated 属性。true 表示可以信任已经发出的令牌(Token)，false 表示不再信任该令牌。
         */
        void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
    }

SpringSecurity 中，认证这个动作，是由 AuthenticationManager 接口的实现类来完成的。

    public interface AuthenticationManager {
    
      Authentication authenticate(Authentication authentication)
        throws AuthenticationException;
    
    }

AuthenticationManager 接口是一个策略接口(Strategy Interface)，表示它可能有不同的实现类来实现不同的“策略”，通俗说就是
实现不同的认证模式。默认是由 ProviderManager 类来实现这个接口。ProviderManager 实现认证的模式就是将认证工作代理给一组
更具体的 AuthenticationProvider 来做。这样的设计模式就是一个“责任链”模式。

ProviderManager 可以有一个parent(父)ProviderManager对象，当它自己的所有 providers 认证提供者都不能决定本次认证时，
它就会询问它的父对象，来完成认证。这样就形成了一个树形结构，以便对资源进行逻辑分组，比如所有的“/api/**”用一个
认证管理器，而“/user/**”用另外一个认证管理器。根节点就代表了公共认证器，这就可以形成一个树形结构，结构如下图所示。
![用ProviderManager形成的AuthenticationManager树](./doc_images/authentication.png)

#### UserDetails 和 UserDetailsService

SpringSecurity框架中，为“身份标识”（principle）这个概念提供了一个具体的定义，即 UserDetails 接口。这个接口定义了
userName, password, authorities(权限), expired(过期), lock(锁定), credentialExpire(密码过期), enable(用户可用) 这样一些
属性。UserDetails 接口的具体对象，就是 Authentication.getPrinciple() 所返回的对象，它是SpringSecurity框架与具体应用程序
之间的一个适配器，让SpringSecurity可用适应各种不同的应用程序。

怎么创建的这 UserDetails 对象呢？

这是由 UserDetailsService 接口创建的，这个接口只有一个方法：

    public interface UserDetailsService {
        /**
         * 通过用户名查找对象
         *
         * @return 一个完整的用户详情对象，不能是null
         *
         * @throws UsernameNotFoundException 如果用户不存在或没有任何授权（GrantedAuthority）
         */
        UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    }

#### 怎么来创建、配置一个 AuthenticationManager 呢？

用 AuthenticationManagerBuilder 这个工具类可用创建和配 AuthenticationManager，例如下面的代码演示了创建一个顶层、全局的 
AuthenticationManager。

    @Configuration
    public class ApplicationSecurity extends WebSecurityConfigurerAdapter {
       ... // web stuff here
      @Autowired
      public void initialize(AuthenticationManagerBuilder builder, DataSource dataSource) {
        builder.jdbcAuthentication().dataSource(dataSource).withUser("dave").password("secret").roles("USER");
      }
    }

### 2、授权和鉴权(访问控制)以及资源的权限

SpringSecurity 中，决定一个用户是否有权限访问某资源，是由 AccessDecisionVoter 接口的具体实现类来完成的。这个接口有下面的
方法：

    boolean supports(ConfigAttribute attribute);
    
    boolean supports(Class<?> clazz);
    
    int vote(Authentication authentication, S object,
            Collection<ConfigAttribute> attributes);

其中的 vote 方法是关键。vote 方法决定一个“认证”对象（即 Authentication 对象），是否能访问某个资源 S object。
资源的所有者为了进一步描述“允许谁访问本资源”这种规则，于是就用一组 ConfigAttribute 对象来描述，
这就是 Collection<ConfigAttribute> attributes 参数。ConfigAttribute 是一个接口，它只有一个简单的方法，返回一个字符串，
这个字符串将描述这种“访问规则”，最常见的是返回“用户角色(User Role)”的定义，例如“ROLE_ADMIN 或者 ROLE_AUDIT”。

下面是我之前错误的理解：

    原来我认为“角色”就是不同权限的集合，一个用户拥有了某个“角色”那么他就有了一组对应的权限。
    从这个意义上说，我之前理解的“角色”其实对应SpringSecurity中的“角色组”，
    而之前我理解的“权限”对应SpringSecurity的“角色（Role）”。
    
    其实 SpringSecurity 中权限是由 GrantedAuthorities 接口表示的，它只有一个返回String的方法 getAuthority()，因此
    权限通常就是用“角色”来表示的，如“ROLE_ADMIN”表示管理员权限，也就是说角色也代表了它所拥有的权限。

在 Spring Security 中，给用户授予的权限由 Authenticate 接口的 authorities 方法提供，这个“权限”通常就是一组分配给
用户的角色字符串，如“ROLE_ADMIN”等。

## Grails Spring-Security-Core plugin 使用教程

### 工作总览
1. 引入依赖包 'org.grails.plugins:spring-security-core:4.0.0.RC3'
2. 创建不安全的 web 应用
3. 创建 spring-security-core 配置和必要的领域对象

#### 1.创建不安全的 web 应用

创建一个 Contract Domain对象。

创建一个 Controller，列出所有的合同。

可以用 grails 命令，方便地创建 domain、controller 和 view。

#### 2.引入依赖包 'org.grails.plugins:spring-security-core:4.0.0.RC3'

build.gradle 文件中添加一行依赖声明

    dependencies {
      compile 'org.grails.plugins:spring-security-core:4.0.0.RC3'
    }

#### 3.创建 spring-security-core 配置和必要的领域对象

    $ grails s2-quickstart com.telecwin.grails.tutorials User Role
    
注意：
* 包名不能省略。
* 要检查领域名称是否是数据库的保留关键字，例如有的数据库就不能使用 User、Group、Role作为表名。最好避免使用这些常见的名称
作为领域对象名。如果一定要使用，需要用mapping指定引用模式，如：


    static mapping = {
       table '`user`'
    }


执行 s2-quickstart 命令后，会创建以下文件：

    resources.groovy
    application.groovy
    Role.groovy
    User.groovy
    UserRole.groovy
    UserPasswordEncoderListener.groovy

然后，我们在 BootStrap.groovy 中初始化数据库内容。

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
                    UserRole.create(user, role)
                }
                assert UserRole.count == 1
            }
        }
    }
    
注意：UserRole 必须用withTransaction，因为 init 闭包不会在事务中或者 session 中运行，需要显式创建一个事务，
因为 UserRole.create() save时设置了flush=false，即不会立即保存到数据库中。

在 application.groovy 中添加一行:

    grails.plugin.springsecurity.logout.postOnly = false
    
告诉 grails spring-security-core plugin 支持 GET 模式的登出，这样方便测试，否则要编写一个 form 来提交登出，测试比较费事。

这时，访问 contract controller (/contract/index)，就会跳转到 login 页面，输入正确的用户名密码后，就能进入 /home 页面，但访问
/contract 页面还是提示没有权限，这是因为没有配置访问 /contract url 所需的权限。

spring-security-ui plugin 支持用户、角色的创建界面，但 core 插件是没有的。

### 测试 RememberMe 功能

登录时勾选上“Remember Me”，就可以在关闭浏览器重新打开浏览器后，自动完成登录，访问需要登录的URL。

注意，如果重启了web服务，那么记住的token就会失效，需要重新登录，如果想要避免这种情况，需要使用“持久化记住我”模式的实现。

这是通过在 cookie 中记录了一个 token，然后通过 token 验证用户是否已经成功登录来实现的。

Token 的格式类似于“yangbo:1583156448794:dda4994f2c2cf3e2afac0cc5169a0bc4”，即

    “username : expiryTime : Md5Hex(username:expiryTime:password:key)”
    
这样的格式。具体实现可以查看 TokenBasedRememberMeServices 类。另外一种更安全的实现方法是持久性Token，
由 PersistentTokenBasedRememberMeServices 类实现。

到这里，我们已经完成了最基本的“安全化一个web应用”的开发。

## 下一步工作
* 添加 security UI，使用 security-ui plugin对用户、角色、权限进行管理，实现用户注册、找回密码、ACL 等功能。
* 使用 Group 简化角色的分配
* 使用 grails-spring-security-rest plugin 实现无状态的 REST 安全化。

## 值得一读的 SpringSecurity 文档

* https://docs.spring.io/spring-security/site/docs/current/reference/html5/#overall-architecture
* https://docs.spring.io/spring-security/site/docs/current/reference/html5/#tech-intro-authentication

## 其他安全相关的 plugins

* [grails-spring-security-ui Grails安全UI插件](https://grails-plugins.github.io/grails-spring-security-ui/latest/index.html#introduction)
提供对安全领域对象的CRUD功能，即增删改查“用户、角色、权限”等对象。

* [grails-spring-security-acl Grails ACL 插件](https://grails-plugins.github.io/grails-spring-security-acl/latest/index.html)，将权限功能增强到可以对每个实体对象进行授权。

## Grails 相关技巧

Spring Security 使用注意事项：

* 在 Spring Security 中，需要给每一个被保护的URL映射一个角色（Role），可以使用“层级角色”（Hierarchical Roles）技术
来简化这个映射配置。
* 在 Spring Security 中，想要方便地给一个用户一次性地分配多个角色，可以将多个角色定义为一个“角色组”（Group），然后
给这个用户授予“角色组”即可。

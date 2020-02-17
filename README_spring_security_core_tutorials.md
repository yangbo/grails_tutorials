Grails 的 Spring Security Core 插件使用教程
========================
作者：杨波 (bo.yang@telecwin.com)

时间：2020年2月16日

版权：北京塔尔旺科技有限公司 All Rights Reserved

本教程的目标是：
* 用 SpringSecurityCore plugin实现对“URL”的保护，即只有登录用户才可以访问。
* 更进一步，对不同的URL资源赋予不同的角色，特定的 URL 只允许拥有特定“角色Role”的用户访问。

首先了解一下SpringSecurity的核心概念：
* 和认证相关的概念：
    * 认证（Authentication）：通过让用户输入“用户名、密码”等证明信息来确认该用户的真实身份。
    * 用户（Principal）：代表要认证的用户，通常包含登录时使用的用户名加密码信息。
    * 凭证（credentials）：用来验证用户身份的东西，通常是“密码”。
* 和授权相关的概念：
    * 要意识到授权包括两个动作，“授权”和“鉴权”。
    * 权限（authorities）：即访问某个资源、执行某个操作的权利。也就是 permissions（许可）。
    * 授予的权限权（granted authorities）：这里是名词，而不是动词，表示某个用户已经被授予或者说分配了的权限。注意不是授权动作。
    * 访问控制（Access Control）：也称为鉴权，即决定已认证的用户是否有权利访问本资源、URL或执行方法等操作。
    * 角色（Role）：代表某种工作职责和权利范围，例如“管理员（admin）”、“编辑（editor）”等。角色会用在两个地方，
                    分配权限时和执行访问控制时，即授权和鉴权时。
    * 角色组（Group）：一组角色的集合，是为了更方便地给用户分配多个角色而设计的概念。
    * 弃权（abstain）：放弃投票权。
    * 肯定式的（affirmative）：只要有一个投票者允许访问，则认为有权访问的一种投票机制。
    * 基于共识的（Consensus Based）：基于共识的投票机制，是指只要大多数同意则认为投票通过，有权利访问。
    * 一致性的（Unanimous Based）：一致性投票机制，要求所有投票者都同意或都弃权才算通过。
    * 可否决的（Vetoable）：只要有一票否决，就认为投票不通过的机制。


理解 SpringSecurity 的“访问控制”工作原理
==========================================

SpringSecurity 中，决定一个用户是否有权限访问某资源，是由 AccessDecisionVoter 接口的具体实现类来完成的。这个接口有下面的
方法：

    boolean supports(ConfigAttribute attribute);
    
    boolean supports(Class<?> clazz);
    
    int vote(Authentication authentication, S object,
            Collection<ConfigAttribute> attributes);

其中的 vote 方法是关键。vote 方法决定一个用户（即 Authentication 对象代表的用户），是否能访问某个资源 S object。
资源的所有者为了进一步描述“允许谁访问本资源”这种规则，于是就用一组 ConfigAttribute 对象来描述，
这就是 Collection<ConfigAttribute> attributes 参数。ConfigAttribute 是一个接口，它只有一个简单的方法，返回一个字符串，
这个字符串将描述这种“访问规则”，最常见的是返回“User Role”用户角色的定义，例如“ROLE_ADMIN 或者 ROLE_AUDIT”。

下面是我之前错误的理解：

    原来我认为“角色”就是不同权限的集合，一个用户拥有了某个“角色”那么他就有了一组对应的权限。
    从这个意义上说，我之前理解的“角色”其实对应SpringSecurity中的“角色组”，
    而之前我理解的“权限”对应SpringSecurity的“角色（Role）”。

Spring 的定义：

    在 Spring Security 中，给用户授予的权限由 Authenticate 接口的 authorities 方法提供，这个“权限”通常就是一组分配给
    用户的角色字符串，如“ROLE_ADMIN”等。


值得一读的 SpringSecurity 文档
==============================

* https://docs.spring.io/spring-security/site/docs/current/reference/html5/#overall-architecture


Grails 相关技巧
===============
Spring Security 使用注意事项：

* 在 Spring Security 中，需要给每一个被保护的URL映射一个角色（Role），可以使用“层级角色”（Hierarchical Roles）技术
来简化这个映射配置。
* 在 Spring Security 中，想要方便地给一个用户一次性地分配多个角色，可以将多个角色定义为一个“角色组”（Group），然后
给这个用户授予“角色组”即可。


grails.plugin.springsecurity.logout.postOnly = false

// URL与权限映射的方式：有 Annotation, Requestmap 和 InterceptUrlMap 三种，由 SecurityConfigType 枚举定义
grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.telecwin.grails.tutorials.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.telecwin.grails.tutorials.UserRole'
grails.plugin.springsecurity.authority.className = 'com.telecwin.grails.tutorials.Role'
// security ui 必要的属性
grails.plugin.springsecurity.userLookup.authoritiesPropertyName = "authorities"

// 这个是属于用注解方式将URL与权限进行映射的方式
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
	[pattern: '/**',              access: ['permitAll']]
]

// URL直接拦截的权限映射
grails.plugin.springsecurity.interceptUrlMap = [
		[pattern: '/',               access: ['permitAll']],
		[pattern: '/error',          access: ['permitAll']],
		[pattern: '/index',          access: ['permitAll']],
		[pattern: '/index.gsp',      access: ['permitAll']],
		[pattern: '/shutdown',       access: ['ROLE_ADMIN']],
		[pattern: '/assets/**',      access: ['permitAll']],
		[pattern: '/**/js/**',       access: ['permitAll']],
		[pattern: '/**/css/**',      access: ['permitAll']],
		[pattern: '/**/images/**',   access: ['permitAll']],
		[pattern: '/**/favicon.ico', access: ['permitAll']],
		[pattern: '/login',          access: ['permitAll']],
		[pattern: '/login/**',       access: ['permitAll']],
		[pattern: '/logout',         access: ['permitAll']],
		[pattern: '/logout/**',      access: ['permitAll']],
	    [pattern: '/register/**',    access: ['permitAll']],
	    [pattern: '/sec/admin/**',   access: ['ROLE_ADMIN']],
	    [pattern: '/**',      		  access: ['ROLE_USER']],
]

// URL 使用过滤器的定义
grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

// 定义角色层级
grails.plugin.springsecurity.roleHierarchy = '''
   ROLE_ADMIN > ROLE_USER
'''
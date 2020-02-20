import com.telecwin.grails.tutorials.SecurityTenantResolver
import com.telecwin.grails.tutorials.UserPasswordEncoderListener
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    tenantResolver(SecurityTenantResolver)
}

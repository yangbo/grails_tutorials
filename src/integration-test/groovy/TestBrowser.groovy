import com.telecwin.grails.tutorials.page.AssetPage
import com.telecwin.grails.tutorials.page.LoginPage
import geb.Browser
import geb.Configuration

/**
 * 快速开发、验证 geb Page、测试流程的测试类
 */
class TestBrowser {
    public static void main(String[] args) {
        println("hello geb")
        test()
    }

    static test(){
        // 从系统属性读取 environment，默认是 chrome，然后使用集成测试资源目录下的配置文件 GebConfig.groovy来设置 geb
        ConfigObject config = new ConfigSlurper(System.getProperty("geb.env", "chrome")).parse(
                this.getResourceAsStream("/GebConfig.groovy").getText("UTF-8"))
        println(config.toProperties())

        GroovyShell
        Browser.drive(new Configuration(config)) {
            go "http://localhost:8080/asset/index"
            at LoginPage
            assert page instanceof LoginPage
            username.value "yang"
            password.value "123"
            loginButton.click(AssetPage)
            report "成功登录"
            assert at(AssetPage)
            println("测试成功！")
        }
    }
}

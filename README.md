## WEB站点安全过滤器

通过字符串正则表达式匹配的方式，对攻击的请求进行拦截，包括xss跨站脚本攻击、sql注入、crlf、host篡改、svn文件泄漏等。



## servlet2.5配置

web.xml

```xml
<filter>
		<filter-name>xss</filter-name>
		<filter-class>com.pengl.secury.core.XSSFilter</filter-class>
		<init-param>
			<param-name>xss-config</param-name>
			<param-value>com/doone/pengl/config/secury/xss-config.xml</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>xss</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
```





## servlet2.5配置（Spring管理bean）

spring配置

```xml
<bean id="xssfilter" class="com.pengl.secury.core.XSSFilter"></bean>
```

web.xml

```xml
<filter>

    <filter-name>xssfilter</filter-name>

    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>

    <init-param>  

        <param-name>targetFilterLifecycle</param-name>  

        <param-value>true</param-value>  

    </init-param>

    <init-param>

    	<param-name>xss-config</param-name>

    	<param-value>com/doone/pengl/config/secury/xss-config.xml</param-value>

    </init-param>

  </filter>

  <filter-mapping>

    <filter-name>xssfilter</filter-name>

    <url-pattern>/*</url-pattern>

  </filter-mapping>

```



## springboot配置

```
@Configuration
public class FilterConfiguration {

    @Bean
    @Order(Integer.MAX_VALUE)
    public FilterRegistrationBean xssFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //注入过滤器
        registration.setFilter(new XSSFilter());
        //拦截规则
        registration.addUrlPatterns("/*");
        //初始化参数
        registration.addInitParameter("xss-config", "com/doone/ah/store/common/secury/n-xss-config.xml");
        //过滤器名称
        registration.setName("xssFilter");
        return registration;
    }
}
```

## 注意
配置文件中的部分参数需要更改，比如host白名单，最好是将配置文件复制出来放到项目下，然后改好参数后，在配置filter的时候，传入新的配置文件路径
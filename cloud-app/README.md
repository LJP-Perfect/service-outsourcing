## 项目简介

cloud-app这个小项目目前初步搭建好了微服务脚手架的一部分。使用shiro+jwt构建鉴权体系，spring cloud zuul做API网关进行路由转发，nginx通过反向代理对网关进行高可用和负载均衡和静态化，Eureka进行服务治理（Feign进行服务调用）。

## 技术选型

- SpringBoot 2.0.4.RELEASE
- SpringCloud Finchley.M9
- Shiro 1.4.0
- JWT 3.4.0
- Maven 3.5.4
- Jdk 8

## 项目关键

项目使用shiro+jwt的鉴权机制，通过JWTFilter将jwt整合到shiro，JWTToken存放用户名。

`JWTFilter部分代码`

```java
@Override
protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        if(isLoginAttempt(request,response)){
            try{
                //检查token
                executeLogin(request,response);
                return true;
            }catch (Exception e){
                //token错误
                responseError(response,e.getMessage());
            }
        }
        //请求头不存在Token，可能是未登录时（游客）的访问状态，则无需检查token
        return true;
    }
```

shiro中添加JWTFilter，对所有之前（其他过滤器）未匹配到的请求进行过滤。

`ShiroConfig部分代码`

```java
	@Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager){
        ShiroFilterFactoryBean filterFactoryBean=new ShiroFilterFactoryBean();
        Map<String, Filter> filterMap=new HashMap<>();
        filterMap.put("jwt",new JWTFilter());
        filterFactoryBean.setFilters(filterMap);
        filterFactoryBean.setSecurityManager(securityManager);
        Map<String,String> filterChainDefinitionMap=new HashMap<>();
        filterChainDefinitionMap.put("/logout","anon");
        filterChainDefinitionMap.put("/login","anon");
        filterChainDefinitionMap.put("/**","jwt");

        filterFactoryBean.setLoginUrl("/login");
        filterFactoryBean.setSuccessUrl("/index");
        filterFactoryBean.setUnauthorizedUrl("/403");
        filterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return filterFactoryBean;
    }
```

## 项目出现的BUG

#### 打包时，报缺少公共模块jar包的错误。

公共模块不需要打包，所以不需要添加maven插件，否则会报错，因为其他模块在打包的时候会自动添加依赖进去，如果这里打包了，其他的模块就找不到该依赖了。

父项目添加springboot的maven插件打包时会报Unable to find main class。

```verilog
[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:2.0.4.RELEASE:repackage (default) on project common-util: Execu
tion default of goal org.springframework.boot:spring-boot-maven-plugin:2.0.4.RELEASE:repackage failed: Unable to find main class
```

改成以下的编译和测试的maven插件，项目可以打包成功。这个问题还没有找到合理的解释方案，我想可能是因为spring-boot-maven-plugin要打包成可执行jar包，而公共模块里是没有启动类（main class）的，所以使用此插件会报错。

```xml
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.1</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>2.19.1</version>
                <configuration>
                    <skipTests>true</skipTests>    <!--默认关掉单元测试 -->
                </configuration>
            </plugin>

        </plugins>
    </build>
```

子模块的依赖中加入docker-maven-plugin的插件。此插件可以快速的打包镜像、上传镜像等。

最好加入spring-boot-maven-plugin插件，我记得打包过程中出现过问题就是缺少这个插件的原因（好像是空指针异常，记不太清了）。

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>com.spotify</groupId>
            <artifactId>docker-maven-plugin</artifactId>
            <version>1.0.0</version>
            <executions>
                <execution>
                    <id>build-image</id>
                    <phase>package</phase>
                    <goals>
                        <goal>build</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <imageName>${project.parent.artifactId}/${project.artifactId}</imageName>
                <dockerHost> http://192.168.43.2:2375</dockerHost>
                <dockerDirectory>${project.basedir}</dockerDirectory>
                <resources>
                    <resource>
                        <targetPath>/</targetPath>
                        <directory>${project.build.directory}</directory>
                        <include>${project.build.finalName}.jar</include>
                    </resource>
                </resources>
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>org.apache.httpcomponents</groupId>
                    <artifactId>httpclient</artifactId>
                    <version>4.5.6</version>
                </dependency>
                <dependency>
                    <groupId>javax.activation</groupId>
                    <artifactId>activation</artifactId>
                    <version>1.1.1</version>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

#### docker不能使用localhost

因为 bridge 是 Docker 默认的网络模式，换句话说，我一个 host 上的各个 container 从 docker 获取的 IP 都是不一样的（在主机编译部署后大家都是 localhost，但是在 docker 里面情况就不一样了）。解决方法利用docker的主机名，为eureka的容器配置一个主机名（任意即可），比如eureka-server。

eureka-server模块配置文件

```yaml
eureka:
  client:
    fetch-registry: false
    register-with-eureka: false
    service-url:
      defaultZone: http://eureka-server:8000/eureka/
```

eureka-client模块配置文件

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8000/eureka/
```

#### 整合shiro出现No bean of type 'org.apache.shiro.realm

本来是把shiro的一些配置和JWTToken类这些放到公共模块中。但是一直报如上错误，提示我创建shiro.ini，但是这样的话采用硬编码方式把认证授权信息写在INI文件中,可维护性差。没办法，只能把shiro有关的配置和LoginController放在一个模块中。

#### zuul会过滤敏感头部，导致丢失token。

zuul会过滤掉Cookie, Set-Cookie, Authorization的header信息，导致jwt的token在转发时丢失。

在`ZuulProperties`源码中可以看到默认的sensitiveHeaders如下。（如果头部出现含有sensitiveHeaders的信息，就不会添加到转发的请求中）

```java
private Set<String> sensitiveHeaders = new LinkedHashSet(Arrays.asList("Cookie", "Set-Cookie", "Authorization"));
```

只要修改zuul的sensitiveHeaders配置即可。设置为空，设置为空，设置为空。

```yaml
zuul:
  sensitive-headers: 
```

#### nginx的http模块。

我只要在nginx配置文件加入`http{}`，就会报`“http" directive is not allowed here in /etc/nginx/conf.d/app.conf`错误。真的是个谜之错误，-_-。

## 参考资料

[Spirng boot maven多模块打包踩坑](https://blog.csdn.net/Ser_Bad/article/details/78433340)

[Spring Cloud实战小贴士：Zuul处理Cookie和重定向](http://blog.didispace.com/spring-cloud-zuul-cookie-redirect/)

[gitee项目 micro-service](https://gitee.com/wjc666/micro-service)

[[exception is feign.RetryableException: Connection refused (Connection refused) executing GET http：//......](https://www.cnblogs.com/zhikou/p/8629851.html)](https://www.cnblogs.com/zhikou/p/8629851.html)


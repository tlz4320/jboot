![](./doc/docs/static/images/jboot-logo.png)

Jboot 是一个基于 JFinal、Dubbo、Seata、Sentinel、ShardingSphere、Nacos 等开发的国产框架。

其特点是：

- 1、基于 JFinal 完整的 MVC + ORM 支持。
- 2、支持多数据源、分库分表和分布式事务。
- 3、支持 Dubbo RPC 的完整功能，有超过 1亿+ 用户产品正在使用。
- 4、完整的单点限流和分布式限流功能
- 5、支持基基于 Apollo 和 Nacos 的分布式配置中心
- 6、完整的分布式缓存、分布式session、分布式附件支持
- 7、内置功能强劲的门户网关
- 8、完整的单元测试支持
- 9、完善代码生成工具 和 API 文档生成工具
- 10、Docker、K8S 友好


## 开始

**maven 依赖**

```xml
<dependency>
    <groupId>io.jboot</groupId>
    <artifactId>jboot</artifactId>
    <version>4.1.7</version>
</dependency>
```

**Hello World**

```java
@RequestMapping("/")
public class Helloworld extends JbootController {

    public void index(){
        renderText("hello world");
    }

    public static void main(String[] args){
        JbootApplication.run(args);
    }
}
```


## 帮助文档

- 文档请访问：[www.jboot.com.cn](http://www.jboot.com.cn)
- Demos 请访问：[这里](./src/test/java/io/jboot/test)

## 我的修改

- 修改JFinal版本到最新版本，过去的版本proxy存在问题
- 删除部分用不到的类，特别是cglib早就应该淘汰了，对于没完成的Redis其他连接工具的类进行清理
- 升级了Jedis到最新版本，提高安全性和并发速度
- 升级大量依赖包括log4j到最新版，避免存在安全问题
- 删除部分完全用不到的依赖，不知道引入的原因
- 修改Gateway，修复了Cookies丢失问题
- 目前Proxy还是存在奇怪的问题


# SpringCloudStudy





![image-20230502155052380](https://yumoimgbed.oss-cn-shenzhen.aliyuncs.com/img/image-20230502155052380.png)

# 概述

- 服务发现与注册：Eureka
- 服务调用：
- 服务熔断
- 负载均衡
- 服务降级
- 服务消息队列
- 配置中心管理
- 服务网关
- 服务监控
- 全链路追踪
- 自动化构建部署
- 服务定时任务调度操作

![image-20230502182439421](https://yumoimgbed.oss-cn-shenzhen.aliyuncs.com/img/image-20230502182439421.png)

# Eureka

## 基础

**服务治理**

Spring Cloud封装了Netflix公司开发的Eureka模块来实现**服务治理**

在传统的rpc远程调用框架中，管理每个服务与服务之间依赖关系比较复杂，管理比较复杂，所以需要使用服务治理，管理服务于
服务之间依赖关系，可以实现服务调用、负载均衡、容错等，实现服务发现与注册。

------

**服务注册与发现**

- Eureka采用了**CS**的设计架构，Eureka Server作为服务注册功能的服务器，它是服务注册中心。

  而系统中的其他微服务，使用Eureka的客户端连接到Eureka Server并维持心跳连接。

  这样系统的维护人员就可以通过Eureka Server来监控系统中各个微服务是否正常运行。

- 在服务注册与发现中，有一个注册中心。当服务器启动的时候，会把当前自己服务器的信息比如服务地址、通讯地址等以别名方式注册到注册中心上。

  另一方（消费者、服务提供者)，以该别名的方式去注册中心上获取到实际的服务通讯地址，然后再实现本地RPC调用.

RPC远程调用框架核心设计思想在于注册中心，因为使用注册中心管理每个服务与服务之间的一个依赖关系（服务治理概念）。

在任何RPC远程框架中，都会有一个注册中心（存放服务地址相关信息（接口地址））

![image-20230503152556116](https://yumoimgbed.oss-cn-shenzhen.aliyuncs.com/img/image-20230503152556116.png)

------

**Eurek的两个组件：Eureka server 和 Eureka Client**

- **Eureka Server**提供服务注册服务

各个微服务节点通过配置启动后，会在EurekaServer中进行注册，这样EurekaServer中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观看到。

- **Eureka Client**通过注册中心进行访问

是一个Java客户端，用于简化Eureka Server的交互，客户端同时也具备一个内置的、使用轮询(round-robin)负载算法的负载均衡器。

在应用启动后，将会向Eureka Server发送**心跳**（默认周期为30秒）。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除（默认90秒）

------

## 单机部署

cloud-eureka-server7001

```yml
server:
  port: 7001

# 单机版
eureka:
  instance:
    hostname: localhost  #eureka服务端的实例名字
  client:
    register-with-eureka: false    #表示不向注册中心注册自己
    fetch-registry: false   #表示自己就是注册中心，职责是维护服务实例，并不需要去检索服务
    service-url:
      #设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://localhost:7001/eureka/
```

**服务注册**

cloud-provider-payment8001

```yml
server:
  port: 8001

spring:
  application:
    name: cloud-payment-service

  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/spring_cloud?useUnicode=true&characterEncoding=utf-8&useSSL=false
    username: root
    password: 123456

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: top.yumoyumo.entities



eureka:
  client:
    register-with-eureka: true #是否向注册中心注册自己
    fetchRegistry: true #是否从注册中心抓取已有的注册信息 默认true，集群必须设置为true
    service-url:
      #      设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://localhost:17001/eureka #单机版
  instance:
    instance-id: payment8001
    prefer-ip-address: true  #访问路径可以显示IP地址
#    Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
#    lease-renewal-interval-in-seconds: 1
#    Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
#    lease-expiration-duration-in-seconds: 2

```



## Eureka集群原理

服务注册：将服务信息注册进注册中心

服务发现：从注册中心上获取服务信息

实质：存key服务名，取value调用地址

- 先启动eureka注册中心
- 启动服务提供者paymen支付服务
- 支付服务启动后会把自身信息（比如服务地址以别名方式注册进eureka)
- 消费者order服务在需要调用接口时，使用服务别名去注册中心获取实际的RPC远程调用地址
- 消费者获得调用地址后，底层实际是利用HttpClient技术实现远程调用
- 消费者获得服务地址后会缓存在本地内存中，默认每间隔30秒更新一次服务调用地址

## 集群配置

互相注册

cloud-eureka-server7001

```yml
server:
  port: 7001

# 单机版
eureka:
  instance:
    hostname: eureka7001.com  #eureka服务端的实例名字
  client:
    register-with-eureka: false    #表示不向注册中心注册自己
    fetch-registry: false   #表示自己就是注册中心，职责是维护服务实例，并不需要去检索服务
    service-url:
      #设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://eureka7002.com:7002/eureka/
```

cloud-eureka-server7002

```
server:
  port: 7002

# 单机版
eureka:
  instance:
    hostname: eureka7002.com  #eureka服务端的实例名字
  client:
    register-with-eureka: false    #表示不向注册中心注册自己
    fetch-registry: false   #表示自己就是注册中心，职责是维护服务实例，并不需要去检索服务
    service-url:
      #设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://eureka7001.com:7001/eureka/
```

------

**服务注册**

cloud-provider-payment8001修改部分

```yml
eureka:
  client:
    register-with-eureka: true #是否向注册中心注册自己
    fetchRegistry: true #是否从注册中心抓取已有的注册信息 默认true，集群必须设置为true
    service-url:
      #      设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
#      defaultZone: http://localhost:17001/eureka #单机版
      defaultZone: http://eureka7001.com:7001/eureka,http://eureka7002.com:7002/eureka  #集群版
  instance:
    instance-id: payment8001
    prefer-ip-address: true  #访问路径可以显示IP地址
#    Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
#    lease-renewal-interval-in-seconds: 1
#    Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
#    lease-expiration-duration-in-seconds: 2
```

------

## 负载均衡

消费者订单模块的RestTemplate开启负载均衡

```java
@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
```

OrderController

```java
@RestController
public class OrderController {
//    public static final String PAYMENT_URL = "http://localhost:8001";
    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";
    @Resource
    private RestTemplate restTemplate;

    @PostMapping("/consumer/payment/create")
    public Result create(@RequestBody Payment payment){
       return restTemplate.postForObject(PAYMENT_URL+"/payment/create",payment,Result.class);
    }
    @GetMapping("/consumer/payment/get/{id}")
    public Result getPaymentById(@PathVariable("id") Long id){
        return restTemplate.getForObject(PAYMENT_URL+"/payment/getPaymentById/"+id, Result.class);
    }

}
```

## 服务发现Discovery

```java
@SpringBootApplication
@EnableEurekaClient
@EnableDiscoveryClient
public class PaymentMain8001 {
    public static void main(String[] args) {
        SpringApplication.run(PaymentMain8001.class,args);
    }
}
```

```java
@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {

    @Resource
    private DiscoveryClient discoveryClient;

    @GetMapping("/discovery")
    public Object discovery(){

        List<String> services = discoveryClient.getServices();
        log.info("services:{}",services);
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        List<String> instancesInfo = new LinkedList<>();
        for (ServiceInstance instance : instances) instancesInfo.add(instance.getInstanceId()+"\t"+instance.getHost()+"\t"+instance.getPort()+"\t"+instance.getUri());
        log.info("instances:{}",instancesInfo);
        return this.discoveryClient;
    }
}

```

```json
{
    "services": [
        "cloud-payment-service",
        "cloud-order-service"
    ],
    "order": 0
}
```

------

## 自我保护机制

![image-20230503184947117](https://yumoimgbed.oss-cn-shenzhen.aliyuncs.com/img/image-20230503184947117.png)

某时间一个微服务不可用了，Eureka不会立刻清理，依旧会对该微服务的信息进行保存

属于**CAP理论**中的AP分支

**为什么会产生Eureka自我保护机制？**

为了防止【EurekaClienti可以正常运行，但是与EurekaServer网络不通】情况，EurekaServer不会立刻将EurekaClient服务剔除

**什么是自我保护模式？**

默认情况下，如果Eureka Server在定时间内没有接收到某个微服务实例的心跳，Eureka Server将会注销该实例（默认90秒）。

但是当网络分区故障发生（延时、卡顿、拥挤）时，微服务与Eureka Server之间无法正常通信，以上行为可能变得非常危险了一因为微服务本身其实是健康的，**此时本不应该注销这个微服务**。

Eureka通过“自我保护模式”来解决这个问题——当Eureka Server节点**在短时间内丢失过多**客户端时（可能发生了网络分区故障），那么这个节点就会讲入自我保护模式。

![image-20230503184926704](https://yumoimgbed.oss-cn-shenzhen.aliyuncs.com/img/image-20230503184926704.png)

**在自我保护模式中，Eureka Server会保护服务注册表中的信息，不再注销任何服务实例。**

它的设计哲学就是宁可保留错误的服务注册信息，也不盲目注销任何可能健康的服务实例。一句话讲解：**好死不如赖活着**

综上，自我保护模式是一种应对网络异常的安全保护措施。它的架构哲学是宁可同时保留所有微服务（健康的微服务和不健康的微服务都会保留)也不盲目注销任何健康的微服务。

使用自我保护模式，可以让Eureka集群更加的健壮、稳定。

### 关闭自我保护

eureka server

```yml
eureka:
  instance:
    hostname: eureka7001.com  #eureka服务端的实例名字
  client:
    register-with-eureka: false    #表示不向注册中心注册自己
    fetch-registry: false   #表示自己就是注册中心，职责是维护服务实例，并不需要去检索服务
    service-url:
      #设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
      defaultZone: http://eureka7002.com:7002/eureka/
  server:
    enableSelfPreservation: false #关闭自我保护机制，保证不可以服务被及时剔除
    evictionIntervalTimerInMs: 2000
```

eureka client

```yml
eureka:
  client:
    register-with-eureka: true #是否向注册中心注册自己
    fetchRegistry: true #是否从注册中心抓取已有的注册信息 默认true，集群必须设置为true
    service-url:
      #      设置与eureka server交互的地址查询服务和注册服务都需要依赖这个地址
#      defaultZone: http://localhost:17001/eureka #单机版
      defaultZone: http://eureka7001.com:17001/eureka,http://eureka7002.com:17002/eureka  #集群版
  instance:
    instance-id: payment8001
    prefer-ip-address: true  #访问路径可以显示IP地址

    lease-renewal-interval-in-seconds: 1 #Eureka客户端向服务端发送心跳的时间间隔，单位为秒(默认是30秒)
    lease-expiration-duration-in-seconds: 2 #Eureka服务端在收到最后一次心跳后等待时间上限，单位为秒(默认是90秒)，超时将剔除服务
```

------


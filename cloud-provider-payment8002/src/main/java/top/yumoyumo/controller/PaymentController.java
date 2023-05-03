package top.yumoyumo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;
import top.yumoyumo.entities.Payment;
import top.yumoyumo.response.Result;
import top.yumoyumo.service.PaymentService;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:27
 **/
@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;
    @Resource
    private DiscoveryClient discoveryClient;

    @PostMapping("/create")
    public String create(@RequestBody Payment payment){
        int res = paymentService.create(payment);
        if (res > 0) return "插入成功";
        else return "插入失败";
    }
    @GetMapping("/getPaymentById/{id}")
    public Result<Payment> getPaymentById(@PathVariable("id") Long id){
        return Result.success(paymentService.getPaymentById(id));
    }
    @GetMapping("/ping")
    public String ping(){
        return "pong:"+serverPort;
    }

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
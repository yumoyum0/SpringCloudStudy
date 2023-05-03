package top.yumoyumo.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import top.yumoyumo.entities.Payment;
import top.yumoyumo.response.Result;

import javax.annotation.Resource;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/3 13:39
 **/
@RestController
public class OrderController {
    public static final String PAYMENT_URL = "http://localhost:8001";
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

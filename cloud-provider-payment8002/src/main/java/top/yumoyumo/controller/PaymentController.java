package top.yumoyumo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import top.yumoyumo.entities.Payment;
import top.yumoyumo.response.Result;
import top.yumoyumo.service.PaymentService;

import javax.annotation.Resource;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:27
 **/
@RestController
@RequestMapping("/payment")
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Value("${server.port}")
    private String serverPort;

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
}
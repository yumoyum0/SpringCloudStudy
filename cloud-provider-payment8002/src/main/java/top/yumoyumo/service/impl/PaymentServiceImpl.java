package top.yumoyumo.service.impl;

import org.springframework.stereotype.Service;
import top.yumoyumo.dao.PaymentMapper;
import top.yumoyumo.entities.Payment;
import top.yumoyumo.service.PaymentService;

import javax.annotation.Resource;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:23
 **/
@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentMapper paymentMapper;


    @Override
    public int create(Payment payment) {
        return paymentMapper.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentMapper.getPaymentById(id);
    }
}

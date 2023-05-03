package top.yumoyumo.service;

import top.yumoyumo.entities.Payment;

/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:23
 **/
public interface PaymentService {
    int create(Payment payment);

    Payment getPaymentById(Long id);
}

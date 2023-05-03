package top.yumoyumo.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.yumoyumo.entities.Payment;


/**
 * @Author: yumo
 * @Description: TODO
 * @DateTime: 2023/5/2 20:06
 **/
@Mapper
public interface PaymentMapper {
    int create(@Param("payment") Payment payment);

    Payment getPaymentById(@Param("id") Long id);
}

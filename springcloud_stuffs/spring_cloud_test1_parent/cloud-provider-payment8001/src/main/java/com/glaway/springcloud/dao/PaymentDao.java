package com.glaway.springcloud.dao;

import com.glaway.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单服务数据接口
 */
@Mapper
public interface PaymentDao {

    public Integer create(Payment payment);

    public Payment getPaymentById(@Param("id") Long id);
}

package com.glaway.springcloud.service;

import com.glaway.springcloud.entities.Payment;
import org.apache.ibatis.annotations.Param;

public interface PaymentService {
    public Integer create(Payment payment);

    public Payment getPaymentById(@Param("id") Long id);
}

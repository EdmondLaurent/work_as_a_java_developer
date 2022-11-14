package com.glaway.springcloud.service.impl;

import com.glaway.springcloud.dao.PaymentDao;
import com.glaway.springcloud.entities.Payment;
import com.glaway.springcloud.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单接口实现类
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    @Resource
    private PaymentDao paymentDao;

    @Override
    public Integer create(Payment payment) {
        return paymentDao.create(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.getPaymentById(id);
    }
}

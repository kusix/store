package com.loganjia.store.service.order.impl;

import com.loganjia.store.service.order.OrderService;
import com.loganjia.store.service.payment.PaymentService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    private final PaymentService paymentService;


    @Autowired
    public OrderServiceImpl(@Qualifier("stripe") PaymentService stripe, @Qualifier("paypal") PaymentService paypal, @Value("${payment.gateway}") String paymentGateway) {
        this.paymentService = paymentGateway.equals("stripe") ? stripe : paypal;
        System.out.println("paymentGateway:" + paymentGateway);
    }

    @PostConstruct
    public void init() {
        System.out.println("OrderService init");
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("OrderService cleanup");
    }

    @Override
    public void placeOrder() {
        paymentService.processPayment(10.5);
    }
}

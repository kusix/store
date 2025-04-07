package com.loganjia.store.service.payment.impl;

import com.loganjia.store.service.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service("paypal")
public class PayPalPaymentService implements PaymentService {


    @Override
    public void processPayment(double amount) {
        System.out.println("PayPal");
        System.out.println("Amount: " + amount);
    }

}

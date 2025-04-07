package com.loganjia.store.service.payment.impl;

import com.loganjia.store.service.payment.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("stripe")
@Primary
public class StripePaymentService implements PaymentService {

    @Value("${stripe.apiUrl}")
    private String apiUrl;

    @Value("${stripe.enabled}")
    private boolean enabled;

    @Value("${stripe.timeout:3000}")
    private int timeout;

    @Value("${stripe.supported-currencies}")
    private List<String> supportedCurrencies;

    @Override
    public void processPayment(double amount) {
        System.out.println("Stripe");
        System.out.println("Stripe API URL: " + apiUrl);
        System.out.println("Stripe enabled: " + enabled);
        System.out.println("Stripe timeout: " + timeout);
        System.out.println("Stripe supported-currencies: " + supportedCurrencies);
        System.out.println("Amount: " + amount);
    }

}

package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Payment;

import java.util.List;

public interface IPaymentService {
    Payment AddPayment(Payment payment);
    List<Payment> retrivePayments();
    Payment updatePayment (Payment payment);
    Payment retrivePayment ( int id_payment);
    void removePayment (int id_payment);
}

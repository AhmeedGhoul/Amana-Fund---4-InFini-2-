package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Payment;
import com.ghoul.AmanaFund.repository.IPaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService implements IPaymentService{

IPaymentRepository iPaymentRepository;
    @Override
    public Payment AddPayment(Payment payment) {
        iPaymentRepository.save(payment);
        return payment;
    }

    @Override
    public List<Payment> retrivePayments() {
        return iPaymentRepository.findAll();
    }

    @Override
    public Payment updatePayment(Payment payment) {
        return iPaymentRepository.save(payment);
    }

    @Override
    public Payment retrivePayment(int id_payment) {
        return null;
    }

    @Override
    public void removePayment(int id_payment) {
        iPaymentRepository.deleteById(id_payment);

    }
}

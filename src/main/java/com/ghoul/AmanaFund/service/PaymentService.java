package com.ghoul.AmanaFund.service;

import com.ghoul.AmanaFund.entity.Payment;
import com.ghoul.AmanaFund.repository.IPaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentService implements IPaymentService{

    @Autowired
    private FraudDetectionService fraudDetectionService;
IPaymentRepository iPaymentRepository;
SmsService smsService;
    @Override
    public Payment AddPayment(Payment payment) {
        iPaymentRepository.save(payment);

        String message = "Paiement de " + payment.getAmount() + " effectué avec succès.";
        smsService.sendSms("+21693528878", message);
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

    @Override
    public Payment processPayment(Payment payment, String transactionDetails) {
        try {
            // Vérifier si le paiement est frauduleux
            boolean isFraud = fraudDetectionService.isFraudulent(payment);


            // Enregistrer le paiement dans la base de données
            if (!isFraud) {
                payment.setStatus(true); // Marquer comme payé si ce n'est pas une fraude
            } else {
                payment.setStatus(false); // Marquer comme non payé si c'est une fraude
            }

            return iPaymentRepository.save(payment);
        } catch (FraudDetectionException e) {
            // Gérer l'exception (par exemple, logger l'erreur et retourner un paiement par défaut)
            System.err.println("Erreur lors de la détection de fraude : " + e.getMessage());
            payment.setStatus(false); // Marquer comme non payé en cas d'erreur
            return iPaymentRepository.save(payment);
        }
    }}


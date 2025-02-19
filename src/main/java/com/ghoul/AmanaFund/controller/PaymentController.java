package com.ghoul.AmanaFund.controller;


import com.ghoul.AmanaFund.entity.Payment;
import com.ghoul.AmanaFund.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@AllArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // Endpoint to add a new payment
    @PostMapping("/add")
    public ResponseEntity<Payment> addPayment(@RequestBody Payment payment) {
        Payment addedPayment = paymentService.AddPayment(payment);
        return new ResponseEntity<>(addedPayment, HttpStatus.CREATED);
    }

    // Endpoint to retrieve all payments
    @GetMapping("/all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.retrivePayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    // Endpoint to retrieve a single payment by ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPayment(@PathVariable int id) {
        Payment payment = paymentService.retrivePayment(id);
        if (payment == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    // Endpoint to update an existing payment
    @PutMapping("/update/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable int id, @RequestBody Payment payment) {
        // You may want to add checks here to ensure payment with the given ID exists
        payment.setId_payment(id); // Ensures the ID from URL is used for the update
        Payment updatedPayment = paymentService.updatePayment(payment);
        return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
    }

    // Endpoint to remove a payment by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable int id) {
        paymentService.removePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

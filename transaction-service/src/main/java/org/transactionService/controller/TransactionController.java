package org.transactionService.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.transactionService.dto.CreateTransactionRequest;
import org.transactionService.service.TransactionService;
import org.transactionService.utils.InputValidation;
import org.transactionService.utils.ValidationException;

import javax.validation.Valid;

/**
 * This class is used as a controller for Transaction API.
 *
 * @author safwanmohammed907@gmal.com
 */
@RestController
public class TransactionController {
    @Autowired
    TransactionService transactionService;
    private final InputValidation inputValidation;
    public TransactionController(InputValidation inputValidation) {
        this.inputValidation = inputValidation;
    }

    /**
     * Method to create new transaction.
     *
     * @param request
     * @param bindingResult
     * @return
     * @throws JsonProcessingException
     * @throws ValidationException
     */
    @PostMapping("/transaction")
    public ResponseEntity<String> createTransaction(@RequestBody @Valid CreateTransactionRequest request, BindingResult bindingResult) throws JsonProcessingException, ValidationException {
        inputValidation.validateInputDetails(bindingResult);
        boolean transaction = transactionService.transact(request);
        if (transaction) {
            return ResponseEntity.ok("Transaction is created successfully.");
        } else {
            return ResponseEntity.badRequest().body("Transaction failed.");
        }

    }
}

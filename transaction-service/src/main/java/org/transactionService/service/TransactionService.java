package org.transactionService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.transactionService.dto.CreateTransactionRequest;
import org.transactionService.model.Transaction;
import org.transactionService.model.TransactionStatus;
import org.transactionService.repository.TransactionRepository;

import java.util.UUID;

/**
 * This class is used as a service for Transaction API.
 *
 * @author safwanmohammed907@gmal.com
 */
@Service
public class TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String TRANSACTION_CREATED_TOPIC = "transaction-created";
    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction-completed";
    private static final String WALLET_UPDATED_TOPIC = "wallet-updated";
    private static final String WALLET_UPDATE_FAILED_STATUS = "FAILED";

    /**
     * Method to initiate transaction and should listen to messages from the TRANSACTION_CREATED_TOPIC Kafka topic.
     *
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    public boolean transact(CreateTransactionRequest request) throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .senderId(request.getSender())
                .receiverId(request.getReceiver())
                .amount(request.getAmount())
                .reason(request.getReason())
                .externalId(UUID.randomUUID().toString())
                .transactionStatus(TransactionStatus.PENDING)
                .build();
        transactionRepository.save(transaction);
        JSONObject obj = new JSONObject();
        //Following fields needed to listen to Wallet Service for updating wallet based on transaction status
        obj.put("senderId", request.getSender());
        obj.put("receiverId", request.getReceiver());
        obj.put("transactionId", transaction.getExternalId());
        obj.put("amount", request.getAmount());
        kafkaTemplate.send(TRANSACTION_CREATED_TOPIC, objectMapper.writeValueAsString(obj));
        return true;
    }

    /**
     * This method is for updating transaction when transaction is initiated and should listen to messages from the WALLET_UPDATED_TOPIC Kafka topic.
     *
     * @param message
     * @throws ParseException
     * @throws JsonProcessingException
     */
    @KafkaListener(topics = {WALLET_UPDATED_TOPIC}, groupId = "Wallet")
    public void updateTransaction(String message) throws ParseException, JsonProcessingException {
        JSONObject obj = (JSONObject) new JSONParser().parse(message);
        //Following fields will be received from  Wallet Service after wallet details are updated when the transaction is initiated.
        String externalTransactionId = (String) obj.get("transactionId");
        String receiverPhoneNumber = (String) obj.get("receiverWalletId");
        String senderPhoneNumber = (String) obj.get("senderWalletId");
        String walletUpdateStatus = (String) obj.get("status");
        Long amount = (Long) obj.get("amount");
        TransactionStatus transactionStatus;
        //Check if the transaction is failed or success based on which the transaction status will be updated.
        if (walletUpdateStatus.equals(WALLET_UPDATE_FAILED_STATUS)) {
            transactionStatus = TransactionStatus.FAILED;
            transactionRepository.updateTransaction(externalTransactionId, transactionStatus);
        } else {
            transactionStatus = TransactionStatus.SUCCESSFUL;
            transactionRepository.updateTransaction(externalTransactionId, transactionStatus);
        }
        //Calling below api from user controller using rest template to get its object and send/receive mail from this application.
        JSONObject senderObj = this.restTemplate.getForObject("http://localhost:9000/user/phone/" + senderPhoneNumber, JSONObject.class);
        JSONObject receiverObj = this.restTemplate.getForObject("http://localhost:9000/user/phone/" + receiverPhoneNumber, JSONObject.class);
        String senderEmail = senderObj == null ? null : (String) senderObj.get("email");
        String receiverEmail = receiverObj == null ? null : (String) receiverObj.get("email");
        obj = new JSONObject();
        //Following fields needed to listen to Notification Service for sending/receiving notification mail to user.
        obj.put("transactionId", externalTransactionId);
        obj.put("transactionStatus", transactionStatus.toString());
        obj.put("amount", amount);
        obj.put("senderEmail", senderEmail);
        obj.put("receiverEmail", receiverEmail);
        obj.put("senderPhone", senderPhoneNumber);
        obj.put("receiverPhone", receiverPhoneNumber);
        kafkaTemplate.send(TRANSACTION_COMPLETED_TOPIC, this.objectMapper.writeValueAsString(obj));


    }
}

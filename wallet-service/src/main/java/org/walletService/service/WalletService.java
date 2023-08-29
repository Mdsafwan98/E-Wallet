package org.walletService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.walletService.model.Wallet;
import org.walletService.repository.WalletRepository;

import java.util.Optional;

/**
 * This class is used as a service for Wallet API.
 *
 * @author safwanmohammed907@gmal.com
 */
@Service
public class WalletService {
    @Autowired
    WalletRepository walletRepository;
    private static final String USER_CREATED_TOPIC = "user-created";
    public static final String USER_UPDATED_TOPIC = "user-updated";
    private static final String TRANSACTION_CREATED_TOPIC = "transaction-created";
    private static final String WALLET_UPDATED_TOPIC = "wallet-updated";
    // ObjectMapper is used to convert between Java objects and JSON representation, providing serialization and deserialization capabilities.
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Value("${wallet-initial-balance}")
    Long balance;

    /**
     * This method is for creating wallet for onboarded user and should listen to messages from the USER_CREATED_TOPIC Kafka topic.
     *
     * @param message
     * @throws ParseException
     */
    @KafkaListener(topics = {USER_CREATED_TOPIC}, groupId = "Wallet")
    public void createWallet(String message) throws ParseException {
        //To convert from requested in json to string
        JSONObject obj = (JSONObject) new JSONParser().parse(message);
        String walletId = (String) obj.get("phone");
        Wallet wallet = Wallet.builder()
                .walletId(walletId)
                .currency("INR")
                .balance(balance)
                .build();
        walletRepository.save(wallet);
    }

    /**
     * This method is for updating wallet when user update the self info details and should listen to messages from the USER_UPDATED_TOPIC Kafka topic.
     *
     * @param message
     * @throws ParseException
     */
    @KafkaListener(topics = {USER_UPDATED_TOPIC}, groupId = "Wallet")
    @Transactional
    public void updateWalletAfterUserDetailsUpdated(String message) throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(message);
        //Following fields will be received from  User Service after updating existing user details
        String phone = (String) obj.get("phone");
        Long id = (Long) obj.get("id");
        Optional<Wallet> wallet = walletRepository.findById(Math.toIntExact(id));
        if (wallet.isPresent()) {
            Wallet walletDetails = wallet.get();
            //set phone number in wallet with the updated phone number requested by user.
            walletDetails.setWalletId(phone);
            walletRepository.save(walletDetails);
        }
    }

    /**
     * This method is for updating wallet when transaction is successfully completed and should listen to messages from the TRANSACTION_CREATED_TOPIC Kafka topic.
     *
     * @param message
     * @throws ParseException
     * @throws JsonProcessingException
     */
    @KafkaListener(topics = {TRANSACTION_CREATED_TOPIC}, groupId = "Wallet")
    public void updateWallets(String message) throws ParseException, JsonProcessingException {
        JSONObject obj = (JSONObject) new JSONParser().parse(message);
        //Following fields will be received from  User Service after user details are created.
        String senderWalletId = (String) obj.get("senderId");
        String receiverWalletId = (String) obj.get("receiverId");
        Long amount = (Long) obj.get("amount");
        String transactionId = (String) obj.get("transactionId");
        try {
            Wallet senderWallet = walletRepository.findByWalletId(senderWalletId);
            Wallet receiverWallet = walletRepository.findByWalletId(receiverWalletId);
            if (senderWallet == null || receiverWallet == null || senderWallet.getBalance() < amount) {
                obj = this.init(senderWalletId, receiverWalletId, transactionId, "FAILED", amount);
                obj.put("senderWalletBalance", senderWallet == null ? 0 : senderWallet.getBalance());
                kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(obj));
                return;
            }
            //Decrement the wallet balance for sender's wallet when transaction is successful
            walletRepository.updateWallet(senderWalletId, -amount);
            //Increment the wallet balance for receiver's wallet when transaction is successful
            walletRepository.updateWallet(receiverWalletId, amount);
            obj = this.init(senderWalletId, receiverWalletId, transactionId, "SUCCESS", amount);
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(obj));
        } catch (Exception e) {
            obj = this.init(senderWalletId, receiverWalletId, transactionId, "FAILED", amount);
            obj.put("error message", e.getMessage());
            kafkaTemplate.send(WALLET_UPDATED_TOPIC, objectMapper.writeValueAsString(obj));
        }


    }

    /**
     * This is a common method to remove duplication of code.
     *
     * @param senderId
     * @param recieverId
     * @param transactionId
     * @param status
     * @param amount
     * @return
     */
    private JSONObject init(String senderId, String recieverId, String transactionId, String status, Long amount) {
        JSONObject obj = new JSONObject();
        obj.put("transactionId", transactionId);
        obj.put("status", status);
        obj.put("senderWalletId", senderId);
        obj.put("receiverWalletId", recieverId);
        obj.put("amount", amount);
        return obj;
    }
}

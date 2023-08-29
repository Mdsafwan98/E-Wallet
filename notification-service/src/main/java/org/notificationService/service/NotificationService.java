package org.notificationService.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * This class is used as a service for Notification API.
 *
 * @author safwanmohammed907@gmal.com
 */
@Service
public class NotificationService {
    private static final String TRANSACTION_COMPLETED_TOPIC = "transaction-completed";
    @Autowired
    SimpleMailMessage simpleMailMessage;
    @Autowired
    JavaMailSender javaMailSender;

    /**
     * This method is for notifying user when transaction is successfully completed and should listen to messages from the TRANSACTION_CREATED_TOPIC Kafka topic.
     *
     * @param msg
     * @throws ParseException
     */
    @KafkaListener(topics = {TRANSACTION_COMPLETED_TOPIC}, groupId = "Wallet")
    public void notify(String msg) throws ParseException {
        JSONObject obj = (JSONObject) new JSONParser().parse(msg);
        //Following fields will be received from  Transaction Service after transaction is successfully completed.
        String transactionStatus = (String) obj.get("transactionStatus");
        String transactionId = (String) obj.get("transactionId");
        Long amount = (Long) obj.get("amount") / 100;  // convert paisa to rupees
        String senderEmail = (String) obj.get("senderEmail");
        String receiverEmail = (String) obj.get("receiverEmail");
        String senderMsg = getSenderMessage(transactionStatus, amount, transactionId);
        String receiverMsg = getReceiverMessage(transactionStatus, amount, senderEmail);
        //Setting email config for sender
        if (!senderMsg.isEmpty()) {
            simpleMailMessage.setTo(senderEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("ewallet.testing79@gmail.com");
            simpleMailMessage.setText(senderMsg);
            javaMailSender.send(simpleMailMessage);
        }
        //Setting email config for receiver
        if (!receiverMsg.isEmpty()) {
            simpleMailMessage.setTo(receiverEmail);
            simpleMailMessage.setSubject("E-Wallet Transaction Updates");
            simpleMailMessage.setFrom("ewallet.testing79@gmail.com");
            simpleMailMessage.setText(receiverMsg);
            javaMailSender.send(simpleMailMessage);
        }
    }

    /**
     * This method contains messages for sender to send based on transaction status.
     *
     * @param transactionStatus
     * @param amount
     * @param transactionId
     * @return
     */
    private String getSenderMessage(String transactionStatus, Long amount, String transactionId) {
        String msg = "";
        if (transactionStatus.equals("FAILED")) {
            msg = "Hi!! Your transaction of amount " + amount + " , transaction id = " + transactionId + " has failed";
        } else {
            msg = "Hi!! Your account has been debited with amount " + amount + " , transaction id = " + transactionId;
        }
        return msg;
    }

    /**
     * This method contains message for receiver to receive when transaction is successful and amount is credited to receiver.
     *
     * @param transactionStatus
     * @param amount
     * @param senderEmail
     * @return
     */
    private String getReceiverMessage(String transactionStatus, Long amount, String senderEmail) {
        String msg = "";
        if (transactionStatus.equals("SUCCESSFUL")) {
            msg = "Hi!! Your account has been credit with amount " + amount + " for the transaction done by user " + senderEmail;
        }
        return msg;
    }
}

package org.notificationService.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * This class is used to set up and configure email sending capabilities in a Java application
 *
 * @author safwanmohammed907@gmail.com
 */
@Configuration
public class EmailConfig {
    @Bean
    SimpleMailMessage getMailMessage() {
        return new SimpleMailMessage();
    }

    //Sending mail from java application
    @Bean
    JavaMailSenderImpl getMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.gmail.com");
        javaMailSender.setPort(587);
        javaMailSender.setUsername("ewallet.testing79@gmail.com");
        //password=Testing@123 ,below value generated from GMAIL security
        javaMailSender.setPassword("naqodwnxyxyazapn");

        Properties properties = javaMailSender.getJavaMailProperties();
        //To enable debug logs for mail
        properties.put("mail.debug", true);
        //Transport layer security(tls)- used to send email from this application and it should be enabled
        properties.put("mail.smtp.starttls.enable", true);
        return javaMailSender;
    }
}

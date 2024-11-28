package com.mss.checkin.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailManager {

    @Autowired
    private JavaMailSender mailSender;


    public void sendEmail(String to, String subject, String body) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("skola2@miraclesoft.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }


    }
}

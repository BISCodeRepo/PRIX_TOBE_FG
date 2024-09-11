package com.prix.homepage.backend.basic.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Date;

@Service
public class Mailer {

    private static final String owner = "Eunok Paek";
    private static final String ownerSite = "PRIX";

//    private static final String ownerEmail = "prix@hanyang.ac.kr";
    private static final String ownerEmail = "kfje979@gmail.com";

    private static final String ccEmail = "kfje979@gmail.com";

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmailToUser(String user, String to, String software, String sendmsg, String sig, String path) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(ownerEmail, owner);
            helper.setTo(to);
            helper.setBcc(ccEmail);
            helper.setSubject(software + " software");
            helper.setSentDate(new Date());

            // 이메일 본문
            StringBuilder greeting = new StringBuilder("Dear " + user + ",\n\n");
            greeting.append(sendmsg + "\n\n").append(sig);
            helper.setText(greeting.toString());

            // 첨부 파일이 있을 경우 추가
//            if (path != null && !path.isEmpty()) {
////                helper.addAttachment("Attachment", new File(path));
//            }

            mailSender.send(message);
            System.out.println("Email sent successfully to user");

        } catch (Exception e) {
            System.out.println("Error in sendEmailToUser");
            e.printStackTrace();
        }
    }

    public boolean sendEmailToMe(String subject, String name, String affiliation, String title, String email, String instrument) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(ownerEmail, ownerSite);
            helper.setTo(ownerEmail);
            helper.setSubject(subject);
            helper.setSentDate(new Date());

            // 사용자 정보
            StringBuilder userInfo = new StringBuilder();
            userInfo.append("Name: ").append(name).append("\r\n");
            userInfo.append("Affiliation: ").append(affiliation).append("\r\n");
            userInfo.append("Title: ").append(title).append("\r\n");
            userInfo.append("Email: ").append(email).append("\r\n");
            userInfo.append("Instrument Type: ").append(instrument).append("\r\n");
            userInfo.append("\r\n");
            userInfo.append("https://prix.hanyang.ac.kr/admin/requestlog");

            helper.setText(userInfo.toString());

            mailSender.send(message);
            System.out.println("Email sent successfully to me");

            return true;

        } catch (Exception e) {
            System.out.println("Error in sendEmailToMe");
            e.printStackTrace();
            return false;
        }
    }
}

package com.neuedu.utils.killUtils;

import com.neuedu.info.MailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

/**
 * @BelongsProject: kill
 * @BelongsPackage: com.sxu.kill.server.service
 * @Author: 郝毓才
 * @CreateTime: 2019-12-23 17:45
 * @Description: 发送邮件的服务
 */
@Service
@EnableAsync
public class MailService {
    public static final Logger log = LoggerFactory.getLogger(RabbitReceiverService.class);

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Environment env;
    @Async
    public void sendSimpleEmail(final MailDto dto){
        try {
            SimpleMailMessage message=new SimpleMailMessage();
            message.setFrom(env.getProperty("mail.send.from"));
            message.setTo(dto.getTos());
            message.setSubject(dto.getSubject());
            message.setText(dto.getContent());
            System.out.println(message);
            mailSender.send(message);
            System.out.println("发送简单文本文件-发送成功!");
        }catch (Exception e){
            System.out.println("发送邮件的服务-发生异常");
            e.printStackTrace();

        }
    }

    //发送带有h5标签的邮件（好看）
    @Async
    public void sendHTMLMail(final MailDto dto){
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setFrom(env.getProperty("mail.send.from"));
            message.setTo(dto.getTos());
            message.setSubject(dto.getSubject());
            message.setText(dto.getContent(),true);
            mailSender.send(mimeMessage);
            System.out.println("发送简单文本文件-发送成功!");

        }catch (Exception e){
            System.out.println("发送邮件的服务-发生异常");
            e.printStackTrace();
        }
    }
}

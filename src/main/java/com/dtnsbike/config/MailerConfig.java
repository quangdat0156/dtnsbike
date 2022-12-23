package com.dtnsbike.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.dtnsbike.model.MailInfo;
import com.dtnsbike.service.MailerService;

@Service
public class MailerConfig implements MailerService {

	@Autowired
	JavaMailSender sender;

	List<MimeMessage> queue = new ArrayList<>();

	@Override
	public MimeMessage MyGmail(MailInfo mail) throws MessagingException {
		// Tạo message
		MimeMessage message = sender.createMimeMessage();
		// Sử dụng Helper để thiết lập các thông tin cần thiết cho message
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom(mail.getFrom());
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getBody(), true);
		helper.setReplyTo(mail.getFrom());
		for (String email : mail.getCc()) {
			helper.addCc(email);
		}
		for (String email : mail.getBcc()) {
			helper.addBcc(email);
		}
		for (File file : mail.getFiles()) {
			helper.addAttachment(file.getName(), file);
		}
		return message;
	}

	@Override
	public void send(MailInfo mail) throws MessagingException {
		// Gửi message đến SMTP server
		sender.send(this.MyGmail(mail));
	}

	@Override
	public void send(String to, String subject, String body) throws MessagingException {
		MailInfo mail = new MailInfo(to, subject, body);
		this.send(mail);
	}

	List<MailInfo> list = new ArrayList<>();

	@Override
	public void queue(MailInfo mail) throws MessagingException {
		// Thêm message vào hàng đợi
		list.add(mail);
	}

	@Override
	public void queue(String to, String subject, String body) throws MessagingException {
		queue(new MailInfo(to, subject, body));
	}

	// Số giây chạy phương thức run()
	public static final long secondRun = 3;

	@Scheduled(fixedDelay = 3000)
	public void run() {
		while (!list.isEmpty()) {
			MailInfo mail = list.remove(0);
			try {
				this.send(mail);
				System.out.println("Đang gửi " + list.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

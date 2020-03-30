package ec.com.dinersclub.chatbot.flowalexa.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import ec.com.dinersclub.chatbot.flowalexa.config.Constants;
import io.github.jhipster.config.JHipsterProperties;
import lombok.extern.log4j.Log4j2;

/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Log4j2
@Service
public class MailService {

	private static final String BASE_URL = "baseUrl";

	@Autowired
	private JHipsterProperties jHipsterProperties;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private SpringTemplateEngine templateEngine;

	@Async
	public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", isMultipart,
				isHtml, to, subject, content);

		// Prepare message using a Spring helper
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setFrom(jHipsterProperties.getMail().getFrom());
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.warn("Email could not be sent to user '{}'", to, e);
			} else {
				log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
			}
		}
	}

	@Async
	public void sendEmailFromTemplate(String userEmail, String templateName, String titleKey) {
		Locale locale = Locale.forLanguageTag(Constants.DEFAULT_LANGUAGE);
		Context context = new Context(locale);
		context.setVariable(BASE_URL, jHipsterProperties.getMail().getBaseUrl());
		String content = templateEngine.process(templateName, context);
		String subject = messageSource.getMessage(titleKey, null, locale);
		sendEmail(userEmail, subject, content, false, true);
	}

	@Async
	public void sendActivationEmail(String userEmail) {
		log.debug("Sending activation email to '{}'", userEmail);
		sendEmailFromTemplate(userEmail, "mail/activationEmail", "email.activation.title");
	}

	@Async
	public void sendCreationEmail(String userEmail) {
		log.debug("Sending creation email to '{}'", userEmail);
		sendEmailFromTemplate(userEmail, "mail/creationEmail", "email.activation.title");
	}

	@Async
	public void sendPasswordResetMail(String userEmail) {
		log.debug("Sending password reset email to '{}'", userEmail);
		sendEmailFromTemplate(userEmail, "mail/passwordResetEmail", "email.reset.title");
	}

	@Async
	public void sendPromocionModoTasty(String userEmail) {
		log.debug("Sending promocion modo tasty to '{}'", userEmail);
		sendEmailFromTemplate(userEmail, "mail/modoTasty", "email.modotasty.title");
	}
}

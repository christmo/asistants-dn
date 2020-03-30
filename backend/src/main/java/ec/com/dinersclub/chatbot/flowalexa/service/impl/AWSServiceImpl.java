package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.dinersclub.chatbot.flowalexa.service.AWSService;
import lombok.extern.log4j.Log4j2;

/**
 * AWS Services impl.
 *
 * @author Frank Rosales
 */
@Log4j2
@Service
@Transactional
public class AWSServiceImpl implements AWSService {

	@Value("${application.aws-sqs-interactions-fifo-name}")
	private String sqsInteractionsFifoName;

	@Value("${application.flow-type-id}")
	private String flowTypeId;

	@Value("${application.time-zone}")
	private String timeZone;

	@Autowired
	private AmazonSQS sqs;

	/**
	 * Method to send a sqs message to a fifo queue
	 *
	 * @param message A Json message as String
	 */
	@Override
	public void sendSQSMessageRequest(MessageResponse message) {
		// adding external values required in db
		final Map<String, Object> context = message.getContext();
		context.put("flowType", flowTypeId);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZZZZ");
		df.setTimeZone(TimeZone.getTimeZone(timeZone));
		context.put("currentInteractionDate", df.format(new Date()));
		// get queue info
		log.info("Attempting to get queue data");
		GetQueueUrlResult queueUrlResult = sqs.getQueueUrl(sqsInteractionsFifoName);
		log.info("Queue data: {}", queueUrlResult.toString());
		// get queue url
		String queueUrl = queueUrlResult.getQueueUrl();
		log.info("Attempting to send message to url: {} with data: {}", queueUrl, new Gson().toJson(message));
		// Send a message.
		final SendMessageRequest sendMessageRequest = new SendMessageRequest(queueUrl, new Gson().toJson(message));
		/*
		 * When you send messages to a FIFO queue, you must provide a non-empty
		 * MessageGroupId.
		 */
		sendMessageRequest.setMessageGroupId("chatbotInteractionsGroup");
		final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
		log.info("Message sent successfully with data: {}", sendMessageResult.toString());
	}
}

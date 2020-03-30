package ec.com.dinersclub.chatbot.flowalexa.service;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;

/**
 * Interface to aws operations
 *
 * @author Frank Rosales
 */
public interface AWSService {

    void sendSQSMessageRequest(MessageResponse message);
}

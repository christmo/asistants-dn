package ec.com.dinersclub.chatbot.flowalexa.service;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.diners.chatbot.nlu.service.NaturalLanguageProcessorService;

public interface NaturalLanguageService extends NaturalLanguageProcessorService {
    @Override
    MessageResponse requestAPI(MessageResponse messageResponse) throws ChatbotNluException;
}

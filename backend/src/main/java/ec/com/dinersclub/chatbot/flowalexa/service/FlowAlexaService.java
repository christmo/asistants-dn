package ec.com.dinersclub.chatbot.flowalexa.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.exception.RegisterException;
import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;
import ec.com.dinersclub.chatbot.flowalexa.exception.ServiceNoDataException;
import ec.com.dinersclub.chatbot.flowalexa.exception.StateNotFoundException;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaPagingDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.RegisterDTO;

/**
 * Service
 * 
 * @author Javier Coronel
 */
public interface FlowAlexaService {

	FlowAlexaDTO getFlowByClientIdentification(String identification);

	FlowAlexaDTO getFlowByToken(String token);

	FlowAlexaDTO save(FlowAlexaDTO flowAlexaDTO);

	FlowAlexaDTO saveById(String tokenId);

	void deleteDataWithJdbcTemplate();

	Page<FlowAlexaDTO> getAllClients(Pageable pageable);

	FlowAlexaPagingDTO getClientsPaged(Pageable pageable);

	Map<String, Object> getUserData(String identification) throws ServiceNoDataException;

	Map<String, Object> getCardData(String identification) throws ServiceNoDataException;

	void registerStep1(RegisterDTO registerDTO) throws OtpException, RegisterException, ServiceNoDataException;

	void registerStep2(RegisterDTO registerDTO) throws OtpException, RegisterException;

	MessageResponse getWatsonResponse(MessageResponse messageResponse)
			throws ChatbotNluException, StateNotFoundException, ServiceNoDataException;

	MessageResponse unlockUser(RegisterDTO registerDTO) throws OtpException, RegisterException, ServiceNoDataException;

}

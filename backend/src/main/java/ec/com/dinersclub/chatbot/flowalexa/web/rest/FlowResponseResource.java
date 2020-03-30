package ec.com.dinersclub.chatbot.flowalexa.web.rest;

import java.io.IOException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.domain.Output;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;
import ec.com.dinersclub.chatbot.flowalexa.exception.RegisterException;
import ec.com.dinersclub.chatbot.flowalexa.exception.ServiceNoDataException;
import ec.com.dinersclub.chatbot.flowalexa.exception.StateNotFoundException;
import ec.com.dinersclub.chatbot.flowalexa.service.AWSService;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowAlexaService;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.RegisterDTO;

@RestController
@RequestMapping("/api/watson")
public class FlowResponseResource {

	@Autowired
	private FlowAlexaService flowAlexaService;

	@Autowired
	private AWSService awsService;

	@Value("${application.backend-url}")
	private String serviceURL;

	@Value("${application.flow-name}")
	private String flowName;

	@PostMapping("/init")
	public ResponseEntity<MessageResponse> init(@Valid @RequestBody MessageResponse messageResponse)
			throws ChatbotNluException, IOException, StateNotFoundException, ServiceNoDataException {
		MessageResponse message = this.flowAlexaService.getWatsonResponse(messageResponse);
		this.awsService.sendSQSMessageRequest(message);
		return new ResponseEntity<MessageResponse>(message, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterDTO registerDTO)
			throws OtpException, RegisterException, ServiceNoDataException {
		this.flowAlexaService.registerStep1(registerDTO);
		MessageResponse message = new MessageResponse();
		message.setOutput(new Output());
		message.getOutput().setText(new ArrayList<>());
		message.getOutput().getText().add("Se ha enviado un código a tu teléfono celular.");
		this.awsService.sendSQSMessageRequest(message);
		return new ResponseEntity<MessageResponse>(message, HttpStatus.OK);
	}

	@PostMapping("/validate")
	public ResponseEntity<MessageResponse> validate(@Valid @RequestBody RegisterDTO registerDTO)
			throws OtpException, RegisterException {
		this.flowAlexaService.registerStep2(registerDTO);
		MessageResponse message = new MessageResponse();
		message.setOutput(new Output());
		message.getOutput().setText(new ArrayList<>());
		message.getOutput().getText().add("Te has registrado correctamente.");
		this.awsService.sendSQSMessageRequest(message);
		return new ResponseEntity<MessageResponse>(message, HttpStatus.OK);
	}

	@PostMapping("/resend")
	public ResponseEntity<MessageResponse> reSend(@Valid @RequestBody RegisterDTO registerDTO)
			throws OtpException, RegisterException, ServiceNoDataException {
		this.flowAlexaService.registerStep1(registerDTO);
		MessageResponse message = new MessageResponse();
		message.setOutput(new Output());
		message.getOutput().setText(new ArrayList<>());
		message.getOutput().getText().add("Se ha enviado nuevamente el código a tu teléfono celular.");
		this.awsService.sendSQSMessageRequest(message);
		return new ResponseEntity<MessageResponse>(message, HttpStatus.OK);
	}

	@PostMapping("/unlock")
	public ResponseEntity<MessageResponse> unlockUser(@Valid @RequestBody RegisterDTO registerDTO)
			throws OtpException, RegisterException, ServiceNoDataException {
		MessageResponse message = this.flowAlexaService.unlockUser(registerDTO);
		this.awsService.sendSQSMessageRequest(message);
		return new ResponseEntity<MessageResponse>(message, HttpStatus.OK);
	}

}

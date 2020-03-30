package ec.com.dinersclub.chatbot.flowalexa.web.rest.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.domain.Output;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;
import ec.com.dinersclub.chatbot.flowalexa.service.AWSService;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
@ControllerAdvice
@RestController
public class AlexaFlowResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	@Autowired
	private AWSService awsService;

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<MessageResponse> handleAllExceptions(Exception ex, WebRequest request) {
		log.debug("Entra en exception handler");
		MessageResponse response = new MessageResponse();
		response.setOutput(new Output());
		Map<String, Object> context = new HashMap<>();
		if (ex instanceof HttpClientErrorException || ex instanceof HttpServerErrorException) {
			HttpClientErrorException clientEx = null;
			HttpServerErrorException serverEx = null;
			int statusCode = 0;
			if (ex instanceof HttpClientErrorException) {
				clientEx = CastUtils.convertInstanceOfObject(ex);
				statusCode = clientEx.getStatusCode().value();
			} else if (ex instanceof HttpServerErrorException) {
				serverEx = CastUtils.convertInstanceOfObject(ex);
				statusCode = serverEx.getStatusCode().value();
			}
			switch (statusCode) {
			case 500:
				context.put("componente", "error");
				context.put("linkDialog", "");
				context.put("header", "Error Interno.");
				context.put("tipoDialog", "error");
				response.getOutput().setText(Arrays.asList("Existe un error interno con el servicio."));
				break;
			case 401:
				context.put("componente", "error");
				context.put("linkDialog", "");
				context.put("header", "Error de Sesión.");
				context.put("tipoDialog", "error");
				response.getOutput().setText(Arrays.asList("Por favor ingresar sus credenciales."));
				break;
			case 404:
				context.put("casoIniPeopleSoft", "false");
				break;
			}
		} else if (ex instanceof EntityNotFoundException) {
			context.put("clientNotFound", "true");
		} else if (ex instanceof ChatbotNluException) {
			context.put("componente", "error");
			context.put("linkDialog", "");
			context.put("header", "Error interno");
			context.put("tipoDialog", "error");
			response.getOutput()
					.setText(Arrays.asList("Existe un error de conectividad con Watson. Por favor intente más tarde."));
		} else {
			context.put("componente", "error");
			context.put("linkDialog", "");
			context.put("header", "Error interno");
			context.put("tipoDialog", "error");
			response.getOutput().setText(Arrays.asList("Se ha presentado un error. Por favor intente más tarde."));
		}
		context.put("errorMessage", ex.getMessage());
		response.setContext(context);
		response.getOutput().setText(new ArrayList<String>());
		response.getOutput().getText().add(ex.getMessage());
		log.error("Error handler: " + ex.getMessage() + ": {}", ex);
		this.awsService.sendSQSMessageRequest(response);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public final ResponseEntity<MessageResponse> handleEntityNotFounException(EntityNotFoundException ex) {
		log.debug("Entra en exception handler");
		MessageResponse response = new MessageResponse();
		response.setOutput(new Output());
		Map<String, Object> context = new HashMap<>();
		context.put("clientNotFound", "true");
		response.setContext(context);
		response.getOutput().setText(new ArrayList<String>());
		response.getOutput().getText().add(ex.getMessage());
		log.error("Error handler: " + ex.getMessage() + ": {}", ex);
		this.awsService.sendSQSMessageRequest(response);
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OtpException.class)
	public final ResponseEntity<MessageResponse> handleOtpException(OtpException ex) {
		log.debug("Entra en exception handler");
		MessageResponse response = new MessageResponse();
		response.setOutput(new Output());
		Map<String, Object> context = new HashMap<>();
		context.put("clientNotFound", "true");
		response.setContext(context);
		response.getOutput().setText(new ArrayList<String>());
		response.getOutput().getText().add(ex.getMessage());
		log.error("Error handler: " + ex.getMessage() + ": {}", ex);
		this.awsService.sendSQSMessageRequest(response);
		return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	}

}

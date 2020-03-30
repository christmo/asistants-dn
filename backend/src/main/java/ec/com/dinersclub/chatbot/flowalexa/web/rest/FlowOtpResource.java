package ec.com.dinersclub.chatbot.flowalexa.web.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.domain.Output;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.service.impl.RestTemplateService;
import ec.com.dinersclub.chatbot.flowalexa.service.impl.WatsonNLUImpl;

@RestController
@RequestMapping("/api/watson")
public class FlowOtpResource {

	private final Logger log = LoggerFactory.getLogger(FlowOtpResource.class);
	private final WatsonNLUImpl watsonImpl;

	private final RestTemplateService restTemplateService;

	@Autowired
	RestTemplate restTemplate;

	@Value("${application.otp-service-backend}")
	private String optServiceURL;

	public FlowOtpResource(WatsonNLUImpl watsonImpl, RestTemplateService restTemplateService) {
		this.watsonImpl = watsonImpl;
		this.restTemplateService = restTemplateService;
	}

	@PostMapping("/enrollment-otp")
	public ResponseEntity<String> enrollmentOtp(@Valid @RequestBody MessageResponse messageResponse) {

		ResponseEntity<String> response;
		HttpHeaders httpHeaders = new HttpHeaders();
		MessageResponse messageIn = new MessageResponse();
		try {
			// body.add("username", (String)messageResponse.getInput().get("id_data"));
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

			HttpEntity<?> httpEntity = new HttpEntity<Object>(body, this.getHeaders());
			response = restTemplate.exchange(optServiceURL + ":8001/vuserver/connector/tokens/sms/registration.php",
					HttpMethod.POST, httpEntity, String.class);

			String responseOtp = response.getBody();
			if (responseOtp.startsWith("201")) {
				messageResponse.getContext().put("otpEnrollment", "true");
				log.info("Se agrega exitosamente el usuario");
			}
			messageIn = watsonImpl.requestAPI(messageResponse);
		} catch (Exception exc) {
			log.error("Error de Enrolamiento de OTP:{}", exc);
		}
		return new ResponseEntity<>(validateSpecialCharacters(new Gson().toJson(messageIn)), httpHeaders,
				HttpStatus.OK);
	}

	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtp(@Valid @RequestBody MessageResponse messageResponse) {
		ResponseEntity<String> response;
		HttpHeaders httpHeaders = new HttpHeaders();
		MessageResponse messageIn = new MessageResponse();
		try {
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();

			body.add("username", (String) messageResponse.getInput().get("username"));
			body.add("phoneNumber", (String) messageResponse.getInput().get("phoneNumber"));
			HttpEntity<?> httpEntity = new HttpEntity<Object>(body, this.getHeaders());
			response = restTemplate.exchange(optServiceURL + ":8002/vuserver/connector/tokens/sms/send.php",
					HttpMethod.POST, httpEntity, String.class);

			String responseOtp = response.getBody();

			if (messageResponse.getContext() == null) {
				messageResponse.setContext(new HashMap<String, Object>());
			}
			if (responseOtp.equals("201: Operación realizada exitosamente")) {
				messageResponse.getContext().put("otpSend", "true");
				messageResponse.getContext().put("showFooter", "false");
				messageIn.setOutput(new Output());
				messageIn.getOutput().setText(Arrays.asList("Se ha enviado con éxito la clave temporal a su celular"));
				messageIn.setContext(messageResponse.getContext());
				messageIn.setInput(new HashMap<String, Object>());
				messageIn.getInput().put("text", "exito");
			} else {
				messageResponse.getContext().put("componente", "error");
				messageResponse.getContext().put("otpSend", "false");
				messageIn.setOutput(new Output());
				messageResponse.getContext().put("resultado", "OTP Fallido");
				messageResponse.getContext().put("descripcion", "Existió un error en envío de OTP");
				if (responseOtp.contains("302")) {
					messageResponse.getContext().put("observacion",
							"Usuario bloqueado por exceder la cantidad de intentos");
					messageIn.getOutput().setText(Arrays.asList(
							"Error al enviar el código de validación, usuario bloqueado por exceder la cantidad de intentos."));
				} else {
					messageResponse.getContext().put("observacion",
							"Error al enviar la OTP (Error # " + responseOtp + ") ");
					messageIn.getOutput().setText(Arrays.asList(
							"Error al enviar la OTP (Error # " + responseOtp + ") por favor reinicie el proceso"));
				}
				messageIn.setContext(messageResponse.getContext());
				restTemplateService.saveInteraction(messageResponse);

			}
			messageIn = watsonImpl.requestAPI(messageIn);
		} catch (Exception exc) {
			log.info("error {}", exc.getCause());
			messageIn = this.setErrorComponent(messageIn, messageResponse,
					"No se ha podido enviar la OTP, por favor volver a intentar el proceso.");
		}
		return new ResponseEntity<>(validateSpecialCharacters(new Gson().toJson(messageIn)), httpHeaders,
				HttpStatus.OK);
	}

	@PostMapping("/validate-otp")
	public ResponseEntity<String> validateOtp(@Valid @RequestBody MessageResponse messageResponse)
			throws ChatbotNluException {

		ResponseEntity<String> response;
		HttpHeaders httpHeaders = new HttpHeaders();
		MessageResponse messageIn = new MessageResponse();
		try {
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
			body.add("username", (String) messageResponse.getInput().get("username"));
			body.add("otp", (String) messageResponse.getInput().get("otp"));
			HttpEntity<?> httpEntity = new HttpEntity<Object>(body, this.getHeaders());
			response = restTemplate.exchange(optServiceURL + ":8003/vuserver/connector/tokens/sms/login.php",
					HttpMethod.POST, httpEntity, String.class);
			String responseOtp = response.getBody();
			Map<String, Object> data = new HashMap<>();
			if (responseOtp.startsWith("201")) {
				messageResponse.getContext().put("otpValidate", "true");
				messageResponse.getContext().put("otpBlocked", "false");
				messageResponse.getContext().put("resultado", "OTP Exitoso");
				messageResponse.getContext().put("descripcion", "Usuario valida OTP");
				messageResponse.getContext().put("observacion", "Ingreso exitoso a consulta AUTOSERVICIO");
				data.put("text", "Validación Exitosa");
				messageResponse.setInput(data);
				messageIn = watsonImpl.requestAPI(messageResponse);
			} else if (responseOtp.startsWith("302")) {
				messageResponse.getContext().put("componente", "error");
				messageResponse.getContext().put("otpValidate", "false");
				messageResponse.getContext().put("otpBlocked", "true");
				messageResponse.getContext().put("resultado", "OTP Fallido");
				messageResponse.getContext().put("descripcion", "Usuario no valida la OTP");
				messageResponse.getContext().put("observacion", "Validación Fallida AUTOSERVICIO");
				messageIn.setOutput(new Output());
				messageIn.getOutput()
						.setText(Arrays.asList("Exedió número de intentos permitidos. | Por favor presione aceptar."));
				messageIn.setContext(messageResponse.getContext());
			} else {
				messageResponse.getContext().put("componente", "error");
				messageResponse.getContext().put("otpValidate", "false");
				messageResponse.getContext().put("otpBlocked", "false");
				messageResponse.getContext().put("resultado", "OTP Fallido");
				messageResponse.getContext().put("descripcion", "Usuario no valida la OTP");
				messageResponse.getContext().put("observacion", "Validación Fallida AUTOSERVICIO");
				if (messageResponse.getContext().get("otpValidateCount") == null) {
					messageResponse.getContext().put("otpValidateCount", 3);
				}
				messageResponse.getContext().put("otpValidateCount",
						(Integer) messageResponse.getContext().get("otpValidateCount") - 1);
				data.put("text", "validación fallida");
				messageResponse.setInput(data);
				messageIn = watsonImpl.requestAPI(messageResponse);

			}
		} catch (Exception exc) {
			messageIn = this.setErrorComponent(messageIn, messageResponse,
					"No se ha podido validar la OTP, por favor volver a intentar el proceso.");
		} finally {
			restTemplateService.saveInteraction(messageResponse);
		}
		return new ResponseEntity<>(validateSpecialCharacters(new Gson().toJson(messageIn)), httpHeaders,
				HttpStatus.OK);
	}

	@PostMapping("/unlock-otp")
	public ResponseEntity<String> unlockOtp(@Valid @RequestBody MessageResponse messageResponse) {

		ResponseEntity<String> response;
		HttpHeaders httpHeaders = new HttpHeaders();
		MessageResponse messageIn = new MessageResponse();
		try {
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
			body.add("username", (String) messageResponse.getInput().get("id_data"));
			HttpEntity<?> httpEntity = new HttpEntity<Object>(body, this.getHeaders());
			response = restTemplate.exchange(optServiceURL + ":8004/vuserver/connector/tokens/sms/unlock.php",
					HttpMethod.POST, httpEntity, String.class);

			String responseOtp = response.getBody();
			if (responseOtp.startsWith("201")) {
				messageResponse.getContext().put("otpUnlocked", "true");
				log.info("Se ha desbloqueado exitosamente el usuario");
			}
			messageIn = watsonImpl.requestAPI(messageResponse);
		} catch (Exception exc) {
			log.error("Error de desbloqueado de OTP:{}", exc);
		}
		return new ResponseEntity<>(validateSpecialCharacters(new Gson().toJson(messageIn)), httpHeaders,
				HttpStatus.OK);
	}

	private MessageResponse setErrorComponent(MessageResponse messageIn, MessageResponse messageResponse,
			String errorText) {
		messageIn.getContext().put("componente", "error");
		messageIn.setOutput(new Output());
		messageIn.getOutput().setText(Arrays.asList(errorText));
		messageIn.setContext(messageResponse.getContext());
		return messageIn;
	}

	private HttpHeaders getHeaders() {
		HttpHeaders httpHeaders = new HttpHeaders();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		return httpHeaders;
	}

	/**
	 * Se valida que no existan caracteres especiales.
	 *
	 * @param input Cadena a validar.
	 * @return Cadena sin caracteres especiales.
	 */
	private String validateSpecialCharacters(String input) {
		String REGEX = "^[a-zA-Z0-9\\\" \\\"\\\\\\\\s\\\\d�-��-�.@,\\\\\\\\'-]{0,200}$";
		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(input);
		return m.replaceAll("");
	}
}

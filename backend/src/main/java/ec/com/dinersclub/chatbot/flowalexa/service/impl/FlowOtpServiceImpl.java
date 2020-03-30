package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import ec.com.dinersclub.chatbot.flowalexa.exception.OtpException;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowOtpService;
import ec.com.dinersclub.chatbot.flowalexa.service.HttpService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class FlowOtpServiceImpl extends HttpService implements FlowOtpService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${application.otp-service-backend}")
	private String otpServiceUrl;

	@Override
	public void enrollmentOtp(String identification) throws OtpException {
		ResponseEntity<String> response;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", identification);
		super.setBody(body);
		response = restTemplate.exchange(otpServiceUrl + ":8001/vuserver/connector/tokens/sms/registration.php",
				HttpMethod.POST, getHttpEntity(), String.class);
		String responseOtp = response.getBody();
		if (responseOtp.startsWith("201") || responseOtp.startsWith("301")) {
			log.info(responseOtp);
			return;
		}
		log.info("No se pudo registrar el usuario. {}", responseOtp);
		throw new OtpException("No se pudo registrar el usuario.");
	}

	@Override
	public void sendOtp(String identification, String phoneNumber) throws OtpException {
		ResponseEntity<String> response;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", identification);
		body.add("phoneNumber", phoneNumber);
		super.setBody(body);
		response = restTemplate.exchange(otpServiceUrl + ":8002/vuserver/connector/tokens/sms/send.php",
				HttpMethod.POST, getHttpEntity(), String.class);
		String responseOtp = response.getBody();
		if (responseOtp.equals("201: Operación realizada exitosamente")) {
			log.info("Se envia el codigo otp correctamente");
			return;
		}
		log.info("No se pudo enviar el codigo otp. {}", responseOtp);
		throw new OtpException("No se pudo enviar el codigo otp.");
	}

	@Override
	public void validateOtp(String identification, String otpCode) throws OtpException {
		ResponseEntity<String> response;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", identification);
		body.add("otp", otpCode);
		super.setBody(body);
		log.info("username: " + identification);
		log.info("otp: " + otpCode);
		response = restTemplate.exchange(otpServiceUrl + ":8003/vuserver/connector/tokens/sms/login.php",
				HttpMethod.POST, getHttpEntity(), String.class);
		String responseOtp = response.getBody();
		log.info("responseOTP: " + responseOtp);
		if (responseOtp.startsWith("201")) {
			log.info("Se valida el codigo otp correctamente.");
			return;
		}
		log.error("Codigo otp incorrecto. {}", responseOtp);
		throw new OtpException("Codigo O T P incorrecto.");
	}

	@Override
	public void unlockOtp(String identification) throws OtpException {
		ResponseEntity<String> response;
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("username", identification);
		super.setBody(body);
		response = restTemplate.exchange(otpServiceUrl + ":8004/vuserver/connector/tokens/sms/unlock.php",
				HttpMethod.POST, getHttpEntity(), String.class);
		String responseOtp = response.getBody();
		if (responseOtp.startsWith("201")) {
			log.info("Se ha desbloqueado exitosamente el usuario");
			return;
		}
		log.info("No se ha desbloqueado el usuario. {}", responseOtp);
		throw new OtpException("No se ha desbloqueado el usuario.");
	}

}

package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @author Javier Coronel
 * 
 *         Clase que captura los datos de registro.
 *
 */
@Data
public class RegisterDTO {

	@NotBlank(message = "deviceId is required")
	private String deviceId;

	private String identification;

	private String otp;

	private String tipo;

}

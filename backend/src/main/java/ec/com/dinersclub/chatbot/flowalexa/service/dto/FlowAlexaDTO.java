package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

/**
 * DTO
 *
 * @author Frank Rosales Includes lombok annotations to avoid getters and
 *         setters methods
 */
@Data
public class FlowAlexaDTO {

	private String id;

	private String identification;

	private String documentType;

	private String name;

	private String lastName;

	private String product;

	private String cardNumber;

	private String cellphone;

	private String email;

	private String campaign;

	private String status;

	private BigDecimal saldoActual;

	private LocalDate fechaCorte;

}

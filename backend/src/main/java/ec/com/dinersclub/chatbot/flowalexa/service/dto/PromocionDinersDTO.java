package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PromocionDinersDTO {

	private Long id;

	private String promocion;

	private String establecimiento;

	private String direccionEstablecimiento;

	private String contactoEstablecimiento;

	private LocalDate fechaDesde;

	private LocalDate fechaHasta;

	private String tipoPromocion;

	private String ciudad;

}

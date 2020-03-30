package ec.com.dinersclub.chatbot.flowalexa.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import ec.com.dinersclub.chatbot.flowalexa.enums.TipoPromocionEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "promocion_diners")
public class PromocionDiners implements Serializable {

	/**
	 * Serial generado.
	 */
	private static final long serialVersionUID = -9198058402951509544L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "promocion")
	private String promocion;

	@Column(name = "establecimiento")
	private String establecimiento;

	@Column(name = "direccion_establecimiento")
	private String direccionEstablecimiento;

	@Column(name = "contacto_establecimiento")
	private String contactoEstablecimiento;

	@Column(name = "fecha_desde")
	private LocalDate fechaDesde;

	@Column(name = "fecha_hasta")
	private LocalDate fechaHasta;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_promocion")
	private TipoPromocionEnum tipoPromocion;

	@Column(name = "ciudad")
	private String ciudad;

}

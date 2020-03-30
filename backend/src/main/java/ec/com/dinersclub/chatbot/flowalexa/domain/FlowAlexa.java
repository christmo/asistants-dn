package ec.com.dinersclub.chatbot.flowalexa.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity class
 *
 * @author Frank Rosales Includes lombok annotations to avoid getters and
 *         setters methods
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "flow_alexa")
public class FlowAlexa implements Serializable {

	/**
	 * Serial generado.
	 */
	private static final long serialVersionUID = 6399500552621256145L;

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "identification")
	private String identification;

	@Column(name = "document_type")
	private String documentType;

	@Column(name = "name")
	private String name;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "product")
	private String product;

	@Column(name = "card_number")
	private String cardNumber;

	@Column(name = "cellphone")
	private String cellphone;

	@Column(name = "email")
	private String email;

	@Column(name = "campaign")
	private String campaign;

	@Column(name = "status")
	private String status;

	@Column(name = "saldo_actual")
	private BigDecimal saldoActual;

	@Column(name = "fecha_corte")
	private LocalDate fechaCorte;

}

package ec.com.dinersclub.chatbot.flowalexa.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import ec.com.dinersclub.chatbot.flowalexa.enums.DeviceRegisterStateEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "device_mapping")
public class FlowDeviceMappingId implements Serializable {

	/**
	 * Serial generado.
	 */
	private static final long serialVersionUID = -6793709031556911384L;

	/**
	 * Usuario alexa.
	 */
	@Id
	@Column(name = "user_id_device")
	private String userIdDevice;

	/**
	 * Identificación usuario.
	 */
	@Column(name = "identification")
	private String identification;

	/**
	 * Estado del usuario.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	private DeviceRegisterStateEnum state;

	/**
	 * Intentos restantes para el ingreso de codigo de verificacion.
	 */
	@Column(name = "remaining_attempts")
	private int remainingAttempts;

	/**
	 * Fecha de bloqueo del usuario.
	 */
	@Column(name = "fecha_bloqueo")
	private LocalDate fechaBloqueo;

	/**
	 * Tipo dispositivo.
	 */
	@Column(name = "tipo")
	private String tipo;

	/**
	 * Default constructor.
	 */
	public FlowDeviceMappingId() {
	}

	/**
	 * 
	 * Constructor con parametros.
	 * 
	 * @param userIdDevice
	 * @param identification
	 * @param state
	 * @param tipo
	 */
	public FlowDeviceMappingId(String userIdDevice, String identification, DeviceRegisterStateEnum state, String tipo) {
		this.userIdDevice = userIdDevice;
		this.identification = identification;
		this.state = state;
		this.tipo = tipo;
	}

}

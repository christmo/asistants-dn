package ec.com.dinersclub.chatbot.flowalexa.enums;

import ec.com.dinersclub.chatbot.flowalexa.exception.StateNotFoundException;
import lombok.Getter;

@Getter
public enum DeviceRegisterStateEnum {

	NO_VALIDADO("noValidado"), PENDIENTE_VALIDACION("pendienteValidacion"), VALIDADO("validado"),
	BLOQUEADO("bloqueado");

	private String etiqueta;

	private DeviceRegisterStateEnum(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public static DeviceRegisterStateEnum getDeviceRegisterState(String state) throws StateNotFoundException {
		for (DeviceRegisterStateEnum drs : DeviceRegisterStateEnum.values()) {
			if (state.equals(drs.getEtiqueta())) {
				return drs;
			}
		}
		throw new StateNotFoundException("El estado " + state + " no existe");
	}

	public boolean isNoValidado() {
		if (this.equals(NO_VALIDADO)) {
			return true;
		}
		return false;
	}

	public boolean isPendienteValidacion() {
		if (this.equals(PENDIENTE_VALIDACION)) {
			return true;
		}
		return false;
	}

	public boolean isValidado() {
		if (this.equals(VALIDADO)) {
			return true;
		}
		return false;
	}

	public boolean isBloqueado() {
		if (this.equals(BLOQUEADO)) {
			return true;
		}
		return false;
	}

}

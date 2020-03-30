package ec.com.dinersclub.chatbot.flowalexa.enums;

import ec.com.dinersclub.chatbot.flowalexa.exception.ServiceNotFoundException;
import lombok.Getter;

@Getter
public enum ContextVarServicesConsumedEnum {

	SRV_TARJETAS("SRVTarjetas"), SRV_CLIENTE("SRVCliente"), SRV_CAMPANAS("SRVCampanas");

	private String etiqueta;

	private ContextVarServicesConsumedEnum(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public ContextVarServicesConsumedEnum getContextVarServiceFromString(String service)
			throws ServiceNotFoundException {
		for (ContextVarServicesConsumedEnum cse : ContextVarServicesConsumedEnum.values()) {
			if (service.equals(cse.getEtiqueta())) {
				return cse;
			}
		}
		throw new ServiceNotFoundException("El servicio que quiere consultar " + service + " no existe.");
	}

	public boolean isSrvTarjetas() {
		if (this.equals(SRV_TARJETAS)) {
			return true;
		}
		return false;
	}

}

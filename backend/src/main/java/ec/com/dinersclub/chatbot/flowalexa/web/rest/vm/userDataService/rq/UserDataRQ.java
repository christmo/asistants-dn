package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.userDataService.rq;

import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.genericHeaderService.DinHeader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDataRQ {

	private DinHeader dinHeader;
	private SerExtConsultarDatosGPSRequest SerExtConsultarDatosGPSRequest;

	public UserDataRQ(DinHeader dinHeader, SerExtConsultarDatosGPSRequest serExtConsultarDatosGPSRequest) {
		this.dinHeader = dinHeader;
		this.SerExtConsultarDatosGPSRequest = serExtConsultarDatosGPSRequest;
	}

}

package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.consultaConsolidado.rq;

import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.genericHeaderService.DinHeader;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsultaConsolidadoRQ {

	public DinHeader dinHeader;
	public FlujoConsultaConsolidado flujoConsultaConsolidado;

	public ConsultaConsolidadoRQ(DinHeader dinHeader, FlujoConsultaConsolidado flujoConsultaConsolidado) {
		this.dinHeader = dinHeader;
		this.flujoConsultaConsolidado = flujoConsultaConsolidado;
	}

}

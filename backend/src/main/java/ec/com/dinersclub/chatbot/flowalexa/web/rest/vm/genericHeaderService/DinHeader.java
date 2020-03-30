package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.genericHeaderService;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.joda.time.DateTime;

import ec.com.dinersclub.chatbot.flowalexa.config.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class DinHeader {

	private String aplicacionID;
	private String canalID;
	private String uuid;
	private String ip;
	private String horaTransaccion;
	private String nivelTrace;
	private String nombreServicio;
	private String llaveSimetrica;
	private Paginado paginado;

	public DinHeader() {
		this.loadDefaultData();
	}

	private void loadDefaultData() {

		this.setAplicacionID(Constants.APPLICATION_ID);
		this.setCanalID(Constants.CANAL_ID);
		this.setUuid(Constants.UUID);
		try {
			this.setIp(InetAddress.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			log.info("No se puede extraer la ip");
		}
		DateTime dt = new DateTime();
		this.setHoraTransaccion(dt.toString());
		this.setNivelTrace(Constants.NIVEL_TRACE);
		Paginado page = new Paginado();
		page.setCantRegistros(Constants.NUM_TOTAL_PAGES);
		page.setNumTotalPag(Constants.NUM_TOTAL_PAGES);
		page.setNumPagActual(Constants.NUM_TOTAL_PAGES);
		this.setPaginado(page);

	}

}

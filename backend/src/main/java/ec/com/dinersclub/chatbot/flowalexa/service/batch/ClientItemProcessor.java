package ec.com.dinersclub.chatbot.flowalexa.service.batch;

import org.springframework.batch.item.ItemProcessor;

import ec.com.dinersclub.chatbot.flowalexa.service.dto.PromocionDinersDTO;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ClientItemProcessor implements ItemProcessor<PromocionDinersDTO, PromocionDinersDTO> {

	@Override
	public PromocionDinersDTO process(final PromocionDinersDTO item) throws Exception {
		final PromocionDinersDTO promocionDinersDTO = item;
		if (log.isDebugEnabled())
			log.debug("Client's transformation by Item processor done with itemId : {}", item.getId());
		return promocionDinersDTO;
	}
}

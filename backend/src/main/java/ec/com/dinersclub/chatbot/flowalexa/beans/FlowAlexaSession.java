package ec.com.dinersclub.chatbot.flowalexa.beans;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowDeviceMappingId;
import lombok.Getter;
import lombok.Setter;

@Component
@Getter
@Setter
@SessionScope
public class FlowAlexaSession {

	private Map<String, Object> context;

	private FlowDeviceMappingId deviceMappingId;

	/**
	 * @return the context
	 */
	public Map<String, Object> getContext() {
		if (context == null) {
			context = new HashMap<String, Object>();
		}
		return context;
	}

}

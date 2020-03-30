package ec.com.dinersclub.chatbot.flowalexa.service.mapper;

import com.ibm.watson.developer_cloud.assistant.v1.model.Context;
import com.ibm.watson.developer_cloud.assistant.v1.model.DialogRuntimeResponseGeneric;
import com.ibm.watson.developer_cloud.assistant.v1.model.OutputData;
import ec.com.diners.chatbot.nlu.domain.Generic;
import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.domain.Option;
import ec.com.diners.chatbot.nlu.domain.Output;
import ec.com.dinersclub.chatbot.flowalexa.domain.FlowAlexa;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Mapper for Diners ChatBot MesasageResponse from NLU Watson MessageResponse
 */
@Service
public class MessageResponseMapper {
	public MessageResponse nluResponseToChatbotResponse(
			com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse request) {
		MessageResponse response = new MessageResponse();
		HashMap<String, Object> outputRequest = request.getOutput();
		// Mapping the output Object
		Output responseOutput = new Output();
		responseOutput.setText(new ArrayList<>(
				CastUtils.convertInstanceOfObject(outputRequest, OutputData.class).getText()));
		// Mapping the Context Object
		Context contextRequest = request.getContext();
		response.setContext(new HashMap<>());
		response.getContext().putAll(contextRequest);

		List<DialogRuntimeResponseGeneric> generic = (CastUtils.convertInstanceOfObject(outputRequest,
				OutputData.class)).getGeneric();
		if (Objects.nonNull(generic)) {
			List<Generic> generics = new LinkedList<>();
			generic.forEach(dialogRuntimeResponseGeneric -> {
				Generic genericObj = new Generic();
				genericObj.setTitle(dialogRuntimeResponseGeneric.getTitle());
				genericObj.setOptions(new LinkedList<>());
				dialogRuntimeResponseGeneric.getOptions().forEach(dialogNodeOutputOptionsElement -> {
					Option opt = new Option();
					opt.setId(dialogNodeOutputOptionsElement.getLabel());
					opt.setValue(dialogNodeOutputOptionsElement.getValue().getInput().text());
					genericObj.getOptions().add(opt);
				});
				generics.add(genericObj);
			});
			responseOutput.setGeneric(generics);
		}

		response.setOutput(responseOutput);
		return response;
	}

	public MessageResponse flowDataToMessageResponse() {
		Map context = new HashMap();
		Field[] flowFields = FlowAlexa.class.getFields();
		MessageResponse response = new MessageResponse();
		response.setContext(context);
		return response;
	}
}

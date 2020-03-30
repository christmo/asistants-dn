package ec.com.dinersclub.chatbot.flowalexa.service;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowDeviceMappingId;

public interface FlowDeviceMappingService {

	FlowDeviceMappingId findRegister(String userIdDevice, String identification);

	FlowDeviceMappingId save(FlowDeviceMappingId deviceMapping);

}

package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowDeviceMappingId;
import ec.com.dinersclub.chatbot.flowalexa.repository.FlowDeviceMappingIdRepository;
import ec.com.dinersclub.chatbot.flowalexa.service.FlowDeviceMappingService;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
public class FlowDeviceMappingServiceImpl implements FlowDeviceMappingService {

	@Autowired
	private FlowDeviceMappingIdRepository flowDeviceMappingRepository;

	@Override
	public FlowDeviceMappingId findRegister(String userIdDevice, String identification) {
		FlowDeviceMappingId deviceMapping = this.flowDeviceMappingRepository
				.findByUserIdDeviceAndIdentification(userIdDevice, identification);
		if (deviceMapping == null) {
			throw new EntityNotFoundException("No existe registro");
		}
		log.info("Se encontro registro con deviceId: " + userIdDevice + " e identificacion: " + identification);
		return deviceMapping;
	}

	@Override
	public FlowDeviceMappingId save(FlowDeviceMappingId deviceMapping) {
		return this.flowDeviceMappingRepository.save(deviceMapping);
	}

}

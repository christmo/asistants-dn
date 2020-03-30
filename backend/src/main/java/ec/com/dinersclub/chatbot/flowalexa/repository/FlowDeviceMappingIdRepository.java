package ec.com.dinersclub.chatbot.flowalexa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowDeviceMappingId;

@Repository
public interface FlowDeviceMappingIdRepository extends JpaRepository<FlowDeviceMappingId, String> {

	FlowDeviceMappingId findByUserIdDeviceAndIdentification(String userIdDevice, String identification);

}

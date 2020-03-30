package ec.com.dinersclub.chatbot.flowalexa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowAlexa;

@Repository
public interface FlowAlexaRepository extends JpaRepository<FlowAlexa, String> {
	FlowAlexa findByIdentification(String identification);
}

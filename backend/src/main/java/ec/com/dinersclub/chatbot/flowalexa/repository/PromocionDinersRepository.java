package ec.com.dinersclub.chatbot.flowalexa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ec.com.dinersclub.chatbot.flowalexa.domain.PromocionDiners;
import ec.com.dinersclub.chatbot.flowalexa.enums.TipoPromocionEnum;

@Repository
public interface PromocionDinersRepository extends JpaRepository<PromocionDiners, String> {

	List<PromocionDiners> findByTipoPromocion(TipoPromocionEnum tipoPromocion);

}

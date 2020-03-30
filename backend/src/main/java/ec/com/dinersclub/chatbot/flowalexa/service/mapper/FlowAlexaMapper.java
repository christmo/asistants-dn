package ec.com.dinersclub.chatbot.flowalexa.service.mapper;

import org.mapstruct.Mapper;

import ec.com.dinersclub.chatbot.flowalexa.domain.FlowAlexa;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.FlowAlexaDTO;


@Mapper(componentModel = "spring", uses = {})
public interface FlowAlexaMapper extends EntityMapper<FlowAlexaDTO, FlowAlexa> {

    FlowAlexa toEntity(FlowAlexaDTO dto);

    FlowAlexaDTO toDto(FlowAlexa entity);

}

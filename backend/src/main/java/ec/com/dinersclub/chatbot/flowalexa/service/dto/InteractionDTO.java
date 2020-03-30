package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class InteractionDTO {

    private Long id;

    private Long flowType;

    private String userEstablishment;

    private String input;

    private String output;

    private String userEstablishmentInfo;

    private LocalDateTime currentDate;

    private LocalDate currentShortDate;

    private String status;

    private String token;

    private Set<InteractionDataDTO> data = new HashSet<>();

}

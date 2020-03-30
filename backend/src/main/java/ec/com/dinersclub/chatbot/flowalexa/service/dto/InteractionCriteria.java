package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class InteractionCriteria implements Serializable {

    private String token;
    private String currentDate;

}

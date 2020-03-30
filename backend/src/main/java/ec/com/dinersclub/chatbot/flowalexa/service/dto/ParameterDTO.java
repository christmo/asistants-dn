package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ParameterDTO implements Serializable {

    /**
     * Serial generado.
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String value;

    private Boolean enabled;

    private Long flowType;

}

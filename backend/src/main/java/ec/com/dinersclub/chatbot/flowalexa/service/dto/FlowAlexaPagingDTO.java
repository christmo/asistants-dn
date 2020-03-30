package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FlowAlexaPagingDTO implements Serializable {
    private Set<FlowAlexaDTO> rows = new HashSet<>();
    private long total;
    private int page;
    private int nextPage;
    private int prevPage;
}

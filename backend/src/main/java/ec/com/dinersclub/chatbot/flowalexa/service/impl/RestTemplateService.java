package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.InteractionDTO;
import ec.com.dinersclub.chatbot.flowalexa.service.dto.LoginDTO;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;

@Service
public class RestTemplateService {

    @Value("${application.backend-url}")
    private String serviceURL;

    @Value("${application.flow-type-id}")
    private String flowTypeId;

    @Value("${application.flow-name}")
    private String flowName;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${application.backend-user}")
    private String userName;

    @Value("${application.backend-password}")
    private String password;

    @Value("${application.intent-backend-url}")
    private String intentBackend;

    @Value("${application.mailer-backend-url}")
    private String mailerBackend;

    @Cacheable("parameters")
    public List<LinkedHashMap> getParametersByFlowType(MessageResponse messageResponse) throws ChatbotNluException {
        PolicyFactory policy = Sanitizers.FORMATTING.and(Sanitizers.LINKS);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        String accessToken = CastUtils.convertInstanceOfObject(messageResponse.getContext().get("id_token"));
        httpHeaders.set("Authorization", policy.sanitize("Bearer " + accessToken));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", httpHeaders);
        ResponseEntity<List> exchange;
        try {
            exchange = restTemplate.exchange(serviceURL + "parameters/flow/" + flowTypeId, HttpMethod.GET, entity,
                    List.class);
        } catch (Exception exc) {
            throw new ChatbotNluException(exc.getMessage());
        }
        List<LinkedHashMap> parameters = CastUtils.convertInstanceOfObject(exchange.getBody());
        return parameters;
    }

    public String getJWTTokenAuthenticate() throws ChatbotNluException {
        LoginDTO loginDTO = new LoginDTO(userName, password, flowName);
        ResponseEntity<Object> response;
        try {
            response = restTemplate.postForEntity(serviceURL + "authenticate", loginDTO, Object.class);
        } catch (Exception exc) {
            throw new ChatbotNluException(exc.getMessage());
        }
        LinkedHashMap<String, Object> jwtToken = CastUtils.convertInstanceOfObject(response.getBody());
        return jwtToken.get("id_token").toString();
    }

    @Async
    public void saveInteraction(MessageResponse messageResponse) throws ChatbotNluException {
        ResponseEntity<InteractionDTO> response;
        try {
            messageResponse.getContext().put("flowTypeId", flowTypeId);
            response = restTemplate.postForEntity(intentBackend + "interaction", messageResponse, InteractionDTO.class);
        } catch (Exception exc) {
            throw new ChatbotNluException(exc.getMessage());
        }

    }
}

package ec.com.dinersclub.chatbot.flowalexa.service.impl;

import com.ibm.watson.developer_cloud.assistant.v1.Assistant;
import com.ibm.watson.developer_cloud.assistant.v1.model.Context;
import com.ibm.watson.developer_cloud.assistant.v1.model.InputData;
import com.ibm.watson.developer_cloud.assistant.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.service.security.IamOptions;
import ec.com.diners.chatbot.nlu.domain.MessageResponse;
import ec.com.diners.chatbot.nlu.exception.ChatbotNluException;
import ec.com.dinersclub.chatbot.flowalexa.config.Constants;
import ec.com.dinersclub.chatbot.flowalexa.service.NaturalLanguageService;
import ec.com.dinersclub.chatbot.flowalexa.service.mapper.MessageResponseMapper;
import ec.com.dinersclub.chatbot.flowalexa.web.rest.vm.CastUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;

@Log4j2
@Service
public class WatsonNLUImpl implements NaturalLanguageService {
    private Assistant service;
    @Autowired
    private MessageResponseMapper messageResponseMapper;

    @Value("${application.proxy-enabled}")
    private boolean isProxyEnabled;

    @Value("${application.proxy-address}")
    private String proxyAddress;

    @Value("${application.proxy-port}")
    private String proxyPort;

    @Value("${application.watson_workspace}")
    private String watsonWorkspace;

    @Value("${application.watson_api_key}")
    private String watsonApiKey;

    @Value("${application.watson_version}")
    private String watsonVersion;

    @Override
    public MessageResponse requestAPI(MessageResponse messageResponse) throws ChatbotNluException {
        com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse responseWatson = new com.ibm.watson.developer_cloud.assistant.v1.model.MessageResponse();
        try {

            if (Objects.isNull(messageResponse.getInput().get(Constants.NLU_TEXT_INPUT))) {
                messageResponse.getInput().put(Constants.NLU_TEXT_INPUT, "");
            }
            InputData input = new InputData.Builder(CastUtils
                    .convertInstanceOfObject(messageResponse.getInput().get(Constants.NLU_TEXT_INPUT), String.class))
                    .build();
            Context context = new Context();
            //TODO pasar al mapping
            context.putAll(messageResponse.getContext());
            MessageOptions options = new MessageOptions.Builder(watsonWorkspace)
                    .input(input).context(context)
                    .build();
            responseWatson = service.message(options).execute();
        } catch (Exception exc) {
            log.info("Error->{}", exc);
            throw new ChatbotNluException(exc.getMessage());
        }
        return messageResponseMapper.nluResponseToChatbotResponse(responseWatson);
    }

    @PostConstruct
    public void setupAPIServiceSDK() {
        if (isProxyEnabled) {
            log.info("Postconstruct WatsonService invoke with proxy enabled");

            this.service = new Assistant("2018-06-06") {
                @Override
                protected OkHttpClient configureHttpClient() {
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyAddress, Integer.valueOf(proxyPort)));
                    return super.configureHttpClient().newBuilder().proxy(proxy).build();
                }
            };
        } else {
            IamOptions iamOptions = new IamOptions.Builder().apiKey(watsonApiKey).build();
            this.service = new Assistant(watsonVersion, iamOptions);
        }
    }


}

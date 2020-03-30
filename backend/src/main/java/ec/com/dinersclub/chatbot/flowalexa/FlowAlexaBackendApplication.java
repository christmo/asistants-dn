package ec.com.dinersclub.chatbot.flowalexa;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;

import ec.com.dinersclub.chatbot.flowalexa.config.ApplicationProperties;
import ec.com.dinersclub.chatbot.flowalexa.config.DefaultProfileUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Log4j2
@EnableCaching
@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class FlowAlexaBackendApplication {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication app = new SpringApplication(FlowAlexaBackendApplication.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\t{}://localhost:{}\n\t" +
                        "External: \t{}://{}:{}\n\t" +
                        "Profile(s): \t{}\n----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                protocol,
                env.getProperty("server.port"),
                protocol,
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getActiveProfiles());
    }
}

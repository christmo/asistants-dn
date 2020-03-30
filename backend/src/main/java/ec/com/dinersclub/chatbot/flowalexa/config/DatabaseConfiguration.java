package ec.com.dinersclub.chatbot.flowalexa.config;


import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Log4j2
@Configuration
@EnableJpaRepositories("ec.com.dinersclub.chatbot.flowalexa.repository")
@EnableTransactionManagement
public class DatabaseConfiguration {
    private final Environment env;

    public DatabaseConfiguration(Environment env) {
        this.env = env;
    }

}

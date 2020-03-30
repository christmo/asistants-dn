package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm;

import java.io.Serializable;

public class MailVM implements Serializable {

    private String mail;
    private String host;

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}

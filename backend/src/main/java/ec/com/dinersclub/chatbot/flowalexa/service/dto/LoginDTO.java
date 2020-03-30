package ec.com.dinersclub.chatbot.flowalexa.service.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class LoginDTO implements Serializable {
    /**
     * Serial generado.
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Size(min = 4, max = 100)
    private String password;

    private Boolean rememberMe;

    @NotNull
    @Size(min = 1, max = 20)
    private String flowName;

    public LoginDTO(@NotNull @Size(min = 1, max = 50) String username, @NotNull @Size(min = 4, max = 100) String password,
                    @NotNull @Size(min = 1, max = 20) String flowName) {
        this.username = username;
        this.password = password;
        this.flowName = flowName;
    }

    @Override
    public String toString() {
        return "LoginVM{" +
                "username='" + username + '\'' +
                ", password=" + password +
                '}';
    }
}

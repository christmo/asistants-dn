package ec.com.dinersclub.chatbot.flowalexa.service.dto;

public enum FlowStatus {
    PENDING("NO INICIADO"),
    STARTED("INICIADO"),
    FINISH("FINALIZADO");

    private final String status;

    FlowStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }
}

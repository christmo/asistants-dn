package ec.com.dinersclub.chatbot.flowalexa.web.rest.vm;

import java.io.Serializable;

public class FileData implements Serializable {
    private Long flowTypeId;
    private byte[] file;

    public FileData() {
    }

    public Long getFlowTypeId() {
        return flowTypeId;
    }

    public void setFlowTypeId(Long flowTypeId) {
        this.flowTypeId = flowTypeId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}

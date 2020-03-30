package ec.com.dinersclub.chatbot.flowalexa.enums;

public enum ContextVarServices {

	SRV0001, SRV0002;

	public boolean isSRV0001() {
		if (this.equals(SRV0001)) {
			return true;
		}
		return false;
	}

	public boolean isSRV0002() {
		if (this.equals(SRV0002)) {
			return true;
		}
		return false;
	}

}

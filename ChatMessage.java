import java.io.*;

class ChatMessage implements Serializable {

	protected static final long serialVersionUID = 1112122200L;

	static final int MESSAGE = 1, LOGOUT = 2;
	private int type;
	private String message;

	ChatMessage(int type, String message) {
		this.type = type;
		this.message = message;
	}

	//getters
	int getType() {
		return type;
	}

	String getMessage() {
		return message;
	}
}
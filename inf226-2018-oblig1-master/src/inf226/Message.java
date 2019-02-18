package inf226;

public class Message {
	public final Username sender;
	public final Username recipient;
	public final String message;

	public Message(final Username username, final Username recipient, final String message) throws Invalid {
		this.sender = username;
		this.recipient = recipient;
		if (!valid(message))
			throw new Invalid(message);
		this.message = message;
	}

	public static boolean valid(String message) {
		// TODO: Implement message string validation.
		if (message.isEmpty() || message.equals(".")) {
			return false;
		}
		for (char c : message.toCharArray()) {
			if (Character.isISOControl(c)) {
				return false;
			}
		}
		return true;
	}

	public static class Invalid extends Exception {
		private static final long serialVersionUID = -3451435075806445718L;

		public Invalid(String msg) {
			super("Invalid string: " + msg);
		}
	}
}

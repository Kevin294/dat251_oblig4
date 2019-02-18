package inf226;

import java.util.regex.Pattern;

public class Username {
	public final String username;

	public Username(final String username) throws Invalid {
		if (!valid(username))
			throw new Invalid(username);
		this.username = username;
	}

	public static boolean valid(String username) {
		// TODO: Implement message string validation.
		final Pattern pattern = Pattern.compile("[A-Za-z0-9]+");
		if (!username.isEmpty() && pattern.matcher(username).matches()) {
			return true;
		}
		return false;
	}

	public static class Invalid extends Exception {
		private static final long serialVersionUID = -3451435075806445718L;

		public Invalid(String username) {
			super("Invalid string: " + username);
		}
	}
}

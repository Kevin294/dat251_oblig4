package inf226;

public class Password {
	public final String password;

	Password(final String password) throws Invalid {
		if (!valid(password))
			throw new Invalid(password);
		this.password = password;
	}

	// a digit must occur at least once
	// a lower case letter must occur at least once
	// an upper case letter must occur at least once
	// a special character must occur at least once
	// no whitespace allowed in the entire string
	// at least 8 characters
	public static boolean valid(String password) {
		// TODO: Implement message string validation.
		final String pattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
		if (password.matches(pattern)) {
			return true;
		}
		return false;
	}

	public static class Invalid extends Exception {
		private static final long serialVersionUID = -3451435075806445718L;

		public Invalid(String password) {
			super("Invalid string: " + password);
		}
	}
}
package inf226;

import java.security.SecureRandom;

public final class Token {

	// TODO: This should be an immutable class representing a token.
	final private byte[] bytes;

	/**
	 * The constructor should generate a random 128 bit token
	 */
	public Token() {
		// TODO: generate a random 128 bit token
		SecureRandom random = new SecureRandom();
		bytes = new byte[16];
		random.nextBytes(bytes);
	}

	/**
	 * This method should return the Base64 encoding of the token
	 * 
	 * @return A Base64 encoding of the token
	 */
	public String stringRepresentation() {
		return null;
	}
}

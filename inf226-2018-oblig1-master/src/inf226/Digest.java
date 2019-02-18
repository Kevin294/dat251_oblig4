package inf226;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class Digest {
	public final String digested;

	public Digest(String digested) {
		this.digested = digested;
	}

	public Digest(String password, byte[] salt) throws NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("SHA-512");
		md.update(salt);
		byte[] bytes = md.digest(password.getBytes());
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		digested = sb.toString();

	}

	public static byte[] createSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");
		byte[] salt = new byte[16];
		sr.nextBytes(salt);
		return salt;
	}
}

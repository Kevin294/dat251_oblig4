package inf226;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.util.regex.Pattern;

import inf226.Maybe.NothingException;
import inf226.Message.Invalid;
import inf226.Storage.DatabaseUserStorage;
import inf226.Storage.KeyedStorage;
import inf226.Storage.Storage.ObjectDeletedException;
import inf226.Storage.Storage.ObjectModifiedException;
import inf226.Storage.Stored;

/**
 * 
 * The Server main class. This implements all critical server functions.
 * 
 * @author INF226
 *
 */
public class Server {
	private static final int portNumber = 1337;
	private static final KeyedStorage<String, User> storage = new DatabaseUserStorage();

	public static Maybe<Stored<User>> authenticate(Username username, Password password) {
		// TODO: Implement user authentication
		Maybe<Stored<User>> user = storage.lookup(username.username);
		if (user.isNothing()) {
			return Maybe.nothing();
		}
		try {
			Digest digest = new Digest(password.password, user.force().getValue().getSalt());
			if (user.force().getValue().getName().username.equals(username.username)
					&& user.force().getValue().getPassword().digested.equals(digest.digested)) {
				return user;
			}
		} catch (NothingException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return Maybe.nothing();
	}

	public static Maybe<Stored<User>> register(Username username, Password password) {
		// TODO: Implement user registration
		try {
			if (storage.lookup(username.username).isNothing()) {
				byte[] salt = Digest.createSalt();
				Digest digest = new Digest(password.password, salt);
				final Maybe<User> user = new Maybe<User>(new User(username, digest, salt));
				return new Maybe<Stored<User>>(storage.save(user.force()));
			}
		} catch (NothingException | SQLException | NoSuchAlgorithmException | NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return Maybe.nothing();
	}

	public static Maybe<Token> createToken(Stored<User> user) {
		// TODO: Implement token creation
		return Maybe.nothing();
	}

	public static Maybe<Stored<User>> authenticate(String username, Token token) {
		// TODO: Implement user authentication
		return Maybe.nothing();
	}

	public static Maybe<String> validateUsername(String username) {
		// TODO: Validate username before returning
		final Pattern pattern = Pattern.compile("[A-Za-z0-9]+");
		if (!username.isEmpty() && pattern.matcher(username).matches()) {
			return Maybe.just(username);
		}
		return Maybe.nothing();
	}

	public static Maybe<String> validatePassword(String pass) {
		// TODO: Validate pass before returning
		// This method only checks that the password contains a safe string.
		final Pattern pattern = Pattern.compile("[a-zA-Z0-9!@#$&()\\-`.+,/\"]+");
		if (!pass.isEmpty() && pattern.matcher(pass).matches()) {
			return Maybe.just(pass);
		}
		return Maybe.nothing();
	}

	public static boolean sendMessage(Stored<User> sender, Username recipient, String content) {
		// TODO: Implement the message sending.

		Maybe<Message> msg;
		try {
			msg = new Maybe<Message>(new Message(sender.getValue().getName(), recipient, content));
			final Maybe<Stored<User>> rec = storage.lookup(recipient.username);
			if (!rec.isNothing()) {
				storage.update(rec.force(), rec.force().getValue().addMessage(msg.force()));
				return true;
			}
		} catch (Invalid | ObjectModifiedException | ObjectDeletedException | SQLException | NothingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}

	/**
	 * Refresh the stored user object from the storage.
	 * 
	 * @param user
	 * @return Refreshed value. Nothing if the object was deleted.
	 */
	public static Maybe<Stored<User>> refresh(Stored<User> user) {
		try {
			return Maybe.just(storage.refresh(user));
		} catch (ObjectDeletedException e) {
		} catch (SQLException e) {
		}
		return Maybe.nothing();
	}

	/**
	 * @param args TODO: Parse args to get port number
	 */
	public static void main(String[] args) {
		final RequestProcessor processor = new RequestProcessor();
		System.out.println("Staring authentication server");
		processor.start();
		try (final ServerSocket socket = new ServerSocket(portNumber)) {
			while (!socket.isClosed()) {
				System.err.println("Waiting for client to connect…");
				Socket client = socket.accept();
				System.err.println("Client connected.");
				processor.addRequest(new RequestProcessor.Request(client));
			}
		} catch (IOException e) {
			System.out.println("Could not listen on port " + portNumber);
			e.printStackTrace();
		}
	}

}

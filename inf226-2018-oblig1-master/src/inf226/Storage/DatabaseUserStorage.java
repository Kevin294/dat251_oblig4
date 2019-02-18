package inf226.Storage;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import inf226.Digest;
import inf226.Maybe;
import inf226.Maybe.NothingException;
import inf226.Message;
import inf226.User;
import inf226.Username;
import inf226.Username.Invalid;

public class DatabaseUserStorage implements KeyedStorage<String, User> {
	private final static String path = "C:/temp/storage.db";
	private final Id.Generator idgen;
	private Connection connection;

	public DatabaseUserStorage() {
		idgen = new Id.Generator();
		File dir = new File("C:/temp/");

		if (!dir.exists()) {
			dir.mkdir();
		}
		try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + path);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		createDatabase();
	}

	private void createDatabase() {
		String tableUsers = "CREATE TABLE IF NOT EXISTS users (\n" + "	username TEXT PRIMARY KEY,\n"
				+ "	password TEXT NOT NULL,\n" + " salt TEXT NOT NULL)";
		String tableMessages = "CREATE TABLE IF NOT EXISTS messages (\n" + " id INT PRIMARY KEY,\n"
				+ " recipient TEXT,\n" + " sender TEXT,\n" + " message TEXT NOT NULL,\n"
				+ "	FOREIGN KEY(recipient) REFERENCES users(username),\n"
				+ "	FOREIGN KEY(sender) REFERENCES users(username))";

		try {
			Statement statement = connection.createStatement();
			statement.execute(tableMessages);
			statement.execute(tableUsers);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public Stored<User> save(User value) {
		String query = "INSERT INTO users(username, password, salt) VALUES (?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, value.getName().username);
			ps.setString(2, value.getPassword().digested);
			ps.setString(3, new String(value.getSalt(), "ISO-8859-1"));
			ps.executeUpdate();
		} catch (SQLException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (Message message : value.getMessages()) {
			addMessage(message);
		}
		return new Stored<>(idgen, value);
	}

	@Override
	public Maybe<Stored<User>> lookup(String key) {
		User user;
		String query = "SELECT username, password, salt FROM users WHERE username = ?";

		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, key);
			ResultSet result = ps.executeQuery();

			if (!result.next()) {
				return Maybe.nothing();
			}
			String username = result.getString("username");
			String password = result.getString("password");
			byte[] salt = result.getString("salt").getBytes("ISO-8859-1");
			user = new User(new Username(username), new Digest(password), salt);
		} catch (SQLException | UnsupportedEncodingException | Invalid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Maybe.nothing();
		}

		String query2 = "SELECT sender, message FROM messages WHERE recipient = ? ORDER BY id DESC";
		try (PreparedStatement ps = connection.prepareStatement(query2)) {
			ps.setString(1, key.toString());
			ResultSet result = ps.executeQuery();

			while (result.next()) {
				String sender = result.getString("sender");
				String message = result.getString("message");
				user = user.addMessage(new Message(new Username(sender), user.getName(), message));
			}
		} catch (SQLException | inf226.Message.Invalid | Invalid e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Maybe.nothing();
		}

		return Maybe.just(new Stored<>(idgen, user));
	}

	@Override
	public Stored<User> refresh(Stored<User> old) throws ObjectDeletedException {

		try {
			return lookup(old.getValue().getName().username).force();
		} catch (NothingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ObjectDeletedException(old.id());
		}

	}

	@Override
	public Stored<User> update(Stored<User> old, User newValue) throws ObjectModifiedException, ObjectDeletedException {

		Iterator<Message> iteratorOld;
		Iterator<Message> iteratorNew = newValue.getMessages().iterator();

		Message temp;
		boolean status;
		while (iteratorNew.hasNext()) {
			iteratorOld = old.getValue().getMessages().iterator();
			temp = iteratorNew.next();
			status = false;
			while (iteratorOld.hasNext()) {
				if (temp.equals(iteratorOld.next())) {
					status = true;
					break;
				}
			}
			if (!status) {
				addMessage(temp);
			}
		}
		return new Stored<User>(idgen, newValue);
	}

	private void addMessage(Message message) {
		String query = "INSERT INTO messages(recipient, sender, message) VALUES (?, ?, ?)";
		try (PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setString(1, message.recipient.username);
			ps.setString(2, message.sender.username);
			ps.setString(3, message.message);
			ps.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void delete(Stored<User> old) throws ObjectModifiedException, ObjectDeletedException, SQLException {
		// TODO Auto-generated method stub

	}
}
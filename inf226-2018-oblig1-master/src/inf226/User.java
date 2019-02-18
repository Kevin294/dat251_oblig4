package inf226;

/**
 * Immutable class for users.
 * 
 * @author INF226
 *
 */

public final class User {

	private final Username name;
	private final ImmutableLinkedList<Message> log;
	private final Digest password;
	private final byte[] salt;

	public User(final Username name, final Digest digested, final byte[] salt) {
		this.name = name;
		this.log = new ImmutableLinkedList<Message>();
		this.password = digested;
		this.salt = salt;
	}

	private User(final Username name, final ImmutableLinkedList<Message> log, final Digest password,
			final byte[] salt) {
		this.name = name;
		this.log = log;
		this.password = password;
		this.salt = salt;
	}

	/**
	 * 
	 * @return User name
	 */
	public Username getName() {
		return name;
	}

	public Digest getPassword() {
		return password;
	}

	public byte[] getSalt() {
		return salt;
	}

	/**
	 * @return Messages sent to this user.
	 */
	public Iterable<Message> getMessages() {
		return log;
	}

	/**
	 * Add a message to this user’s log.
	 * 
	 * @param m Message
	 * @return Updated user object.
	 */
	public User addMessage(Message m) {
		return new User(name, new ImmutableLinkedList<Message>(m, log), password, salt);
	}

}

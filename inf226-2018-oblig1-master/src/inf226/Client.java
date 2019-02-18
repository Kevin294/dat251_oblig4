package inf226;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TreeMap;

import inf226.Maybe.NothingException;

/**
 * This class is the client which connect to the server. To run the client,
 * execute it on the console.
 * 
 * @author INF226
 *
 */
public class Client {
	private static final int portNumber = 1337;
	static final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) {
		final String hostname = (args.length < 1) ? "localhost" : args[0];
		System.out.println("Welcome to assignment 1.");
		System.out.println("This is the client program which will allow you to register users,");
		System.out.println("request and validate session IDs.");
		System.out.println();
		try (final Socket socket = new Socket(hostname, portNumber);
				final BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				final BufferedWriter serverOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			System.out.println("Connected to server. What do you want to do?");
			mainMenu(serverIn, serverOut);
		} catch (IOException e) {
			System.err.println("Connection error");
			e.printStackTrace();
		}
	}

	/**
	 * Diplay the main menu on the console.
	 * 
	 * @param serverIn  Server input
	 * @param serverOut Server output
	 */
	private static void mainMenu(final BufferedReader serverIn, final BufferedWriter serverOut) {
		System.out.println("[1] Login");
		System.out.println("[2] Register");
		System.out.println("[3] Quit");
		int option = 0;
		try {
			while (option == 0) {
				System.out.print("Enter option: ");
				final String line = Util.getLine(stdin);
				System.out.println(line);
				if (line.equals("1") || line.equals("[1]") || line.toLowerCase().equals("one")
						|| line.toLowerCase().equals("login"))
					option = 1;
				if (line.equals("2") || line.equals("[2]") || line.toLowerCase().equals("two")
						|| line.toLowerCase().equals("register"))
					option = 2;
				if (line.equals("3") || line.equals("[3]") || line.toLowerCase().equals("three")
						|| line.toLowerCase().equals("quit"))
					option = 3;
				if (line.toLowerCase().equals("help") || line.equals("?"))
					System.out.println("Ask you TA.");
			}
			if (option == 1) { // LOGIN
				System.out.print("Username: ");
				final String username = Util.getLine(stdin);
				System.out.print("Password: ");
				final String password = Util.getLine(stdin);
				login(serverOut, serverIn, username, password);
			}
			if (option == 2) { // REGISTER
				System.out.print("Username: ");
				final String username = Util.getLine(stdin);
				System.out.print("Password: ");
				final String password = Util.getLine(stdin);
				register(serverOut, serverIn, username, password);
			}
			if (option == 3) // QUIT
				return;
		} catch (IOException e) {
			System.err.println("Bye-bye!");
		}
	}

	/**
	 * This method implement the menu for when the user is logged in.
	 * 
	 * @param serverOut
	 * @param serverIn
	 */
	private static void userMenu(BufferedWriter serverOut, BufferedReader serverIn) {
		while (true) {
			System.out.println("Chose an action:");
			System.out.println("[1] Read messages");
			System.out.println("[2] Send message");
			System.out.println("[3] Quit");
			int option = 0;
			try {
				while (option == 0) {
					System.out.print("Enter option > ");
					final String line = Util.getLine(stdin);
					System.out.println(line);
					if (line.equals("1") || line.equals("[1]") || line.toLowerCase().equals("one")
							|| line.toLowerCase().equals("read"))
						option = 1;
					if (line.equals("2") || line.equals("[2]") || line.toLowerCase().equals("two")
							|| line.toLowerCase().equals("send"))
						option = 2;
					if (line.equals("3") || line.equals("[3]") || line.toLowerCase().equals("three")
							|| line.toLowerCase().equals("quit"))
						option = 3;
				}
				if (option == 1) { // READ
					readMessages(serverOut, serverIn);
				}
				if (option == 2) { // SEND
					// TODO: Implement message sending
					System.out.print("Recipient: ");
					final String recipient = Util.getLine(stdin);
					System.out.print("Message: ");
					final String msg = Util.getLine(stdin);
					sendMessages(serverOut, serverIn, recipient, msg);
				}
				if (option == 3) // QUIT
					return;
			} catch (IOException e) {
				System.err.println("Bye-bye!");
			}
		}
	}

	/**
	 * Register the user with the server.
	 * 
	 * @param serverOut
	 * @param serverIn
	 * @param username
	 * @param password
	 * @throws IOException If the server hangs-up.
	 */
	private static void register(final BufferedWriter serverOut, final BufferedReader serverIn, final String username,
			final String password) throws IOException {
		serverOut.write("REGISTER");
		serverOut.newLine();
		serverOut.write("USER " + username);
		serverOut.newLine();
		serverOut.write("PASS " + password);
		serverOut.newLine();
		serverOut.flush();
		final String response = Util.getLine(serverIn);
		System.out.println(response);
		if (response.startsWith("REGISTERED ")) {
			userMenu(serverOut, serverIn);
		}
	}

	/**
	 * Negotiate the login authentication with the server
	 * 
	 * @param serverOut
	 * @param serverIn
	 * @param username
	 * @param password
	 * @throws IOException If the server hangs-up
	 */
	private static void login(final BufferedWriter serverOut, final BufferedReader serverIn, final String username,
			final String password) throws IOException {
		serverOut.write("LOGIN");
		serverOut.newLine();
		serverOut.write("USER " + username);
		serverOut.newLine();
		serverOut.write("PASS " + password);
		serverOut.newLine();
		serverOut.flush();
		final String response = Util.getLine(serverIn);
		System.out.println(response);
		if (response.startsWith("LOGGED IN ")) {
			userMenu(serverOut, serverIn);
		}
	}

	private static void sendMessages(BufferedWriter serverOut, BufferedReader serverIn, String recipient, String msg)
			throws IOException {
		// TODO Auto-generated method stub
		serverOut.write("SEND MESSAGE");
		serverOut.newLine();
		serverOut.write("REC " + recipient);
		serverOut.newLine();
		serverOut.write("MSG " + msg);
		serverOut.newLine();
		serverOut.flush();
		final String response = Util.getLine(serverIn);
		System.out.println(response);

	}

	/**
	 * Read messages from the server and display one of them to the user.
	 * 
	 * @param serverOut
	 * @param serverIn
	 * @throws IOException
	 */
	private static void readMessages(BufferedWriter serverOut, BufferedReader serverIn) throws IOException {
		serverOut.write("READ MESSAGES");
		serverOut.newLine();
		serverOut.flush();
		final String initialResponse = Util.getLine(serverIn);
		if (initialResponse.equals("FAILED")) {
			System.err.println("Failed to retrieve messages");
			return;
		}

		// Read messages from server, one by one
		final TreeMap<String, ArrayList<String>> messages = new TreeMap<String, ArrayList<String>>();
		for (String response = initialResponse; response
				.startsWith("MESSAGE FROM "); response = Util.getLine(serverIn)) {
			String message = "";
			// Concatenate the message lines.
			for (String messageLine = Util.getLine(serverIn); !messageLine.equals("."); messageLine = Util
					.getLine(serverIn)) {
				message = message + "\n" + unescape(messageLine);
			}
			final String sender = response.substring("MESSAGE FROM ".length());
			final Maybe<ArrayList<String>> previously = new Maybe<ArrayList<String>>(messages.get(sender));
			try {
				previously.force().add(message);
				messages.put(sender, previously.force());
			} catch (NothingException e) {
				final ArrayList<String> fresh = new ArrayList<String>();
				fresh.add(message);
				messages.put(sender, fresh);
			}
		}

		// Display one of the messages to the user
		if (messages.size() == 0) {
			System.out.println("No messages!");
			return;
		}
		System.out.println("Got messages from:");
		Integer nsenders = 0;
		final TreeMap<Integer, String> senders = new TreeMap<Integer, String>();
		for (String sender : messages.keySet()) {
			System.out.println(nsenders + " - " + sender);
			senders.put(nsenders, sender);
			nsenders++;
		}
		Integer selection = Util.getOption("Select: ", 0, nsenders, stdin);
		final String sender = senders.get(selection);
		final ArrayList<String> senderMessages = messages.get(sender);
		final String prompt = "Select a message from " + sender + " (0–" + (senderMessages.size() - 1) + "): ";
		Integer messageSelection = Util.getOption(prompt, 0, senderMessages.size(), stdin);
		System.out.println("Message from: " + sender);
		System.out.println(senderMessages.get(messageSelection));
	}

	private static String unescape(final String messageLine) {
		if (isEscapedMessage(messageLine))
			return messageLine.substring(1);
		else
			return messageLine;
	}

	private static boolean isEscapedMessage(final String messageLine) {
		final int n = messageLine.length();
		if (n <= 1)
			return false;
		for (int i = 0; i < n - 1; ++i) {
			if (messageLine.charAt(i) != '\\')
				return false;
		}
		return messageLine.charAt(n - 1) == '.';
	}
}

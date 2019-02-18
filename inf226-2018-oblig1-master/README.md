# Abstract

This is the first of two mandatory assignments in INF226 autumn 2018.

In these exercises the goal is to write a simple, secure console based messaging system,
consisting of a client and a server. Some of the code has already been written, but
through the exercises your job will be to complete and secure the program.

The assignment is individual, and each student must hand in their own solution.
If you do cooperate, please specify at the top of NOTES.txt who you collaborated
with (max 2 collaborators).

# Updates to the code shell

It might happen that the prepared code will have to be updated after the
assignment becomes available. If so, changes will be made as additional
Git-commits and can be pulled from the repository.

# Handing in the assignment

Hand in the assignment on mitt.uib.no as a zip file of the code repository.

Since the repository is a Git-repository, you should commit your progress
as you write, or at least make a final commit before submission.

Remember to add any new files you create to git.

# Specification

The program should compile and run under JDK 1.8.

Include any external libraries you use in the zip file.

Any notes about your solutions which you want us to read
during grading can be put in a separate file called NOTES.txt
in the top directory of tile repository.

This project should consist of two programs: Client and Server.
The client and server communicate through the protocol specified
below.

The client and server offer a message passing system between the users.

 - Only an authenticated user can read messages sent to their account.
 - Only an authenticated user can send messages from their account.
 - The user displayed as sender to the recipient should be account name
   of the user who sent the message.

Assume that the OS running the server/server is secure, and that the
administrators respect the privacy of their users (no need for end-to-end
encryption of messages).

The two program should be secured against common security vulnerabilities
such as:

 - SQL injection
 - Man-in-the-middle attacks
 - Password leakage if database is compromised
 - Online and off-line brute-force of weak passwords



# Exercises

The solution of the exercises is rated on a scale from zero to fifteen,
and the weight of each individual exercise is given below.

Testing: The code contains no tests, but you are strongly encouraged
to write your own tests to help you develop your solution.

## Exercise 1: Authentication and input sanitizing (4 points)

Goal: When this exercise is done, the client should be able
to successfully register and login a user.

The authentication mechanisms of the server are located in src/inf226/Server.java
and requests are parsed in src/inf226/RequestHandler.java .

 - Fill out the missing methods bodies using what we have learned about storing authentication
   credentials in the course:
     - Implement a secure authentication mechanism
       in the methods Server.register and Server.authenticate
     - Preventing brute by implementing a attempt limit in RequestProcessor
 - Implement simple sanitizers for user names and passwords:
    - Allowed user name characters: a-z,A-Z,0-9
    - Allowed password characters: a-z,A-Z,0-9, or .,:;()[]{}<>"'#!$%&/+*?=-_|

You might need to introduce more fields in the User class to store the
keys derived from the registration process. Make sure to give the new fields
the final-keyword.

## Exercise 2: Implement message sending (3 points)

Goal: When this exercise is done the client should be able
to successfully send and receive messages.

Implement message sending in client and server:

 - In Message, implement the valid-method to sanitize messages:
    - No control characters (Character.isISOControl())
    - No single “.” lines.
 - In Client, the method userMenu should call upon a method you create, called "sendMessage",
   which allows the user to compose and send a message.
 - In RequestProcessor, the method handleMessage should make appropriate calls to
   Server in order to send the message.
 - In Server, the method sendMessage should find the recipient user add its
   message there.

Remember that the client should escape single “.” line messages!

## Exercise 3: Using the type system to detect a problem (2 points)

Goal: Increase the typing information to detect possible security issues.

Currently the registration method takes strings as inputs. This makes it possible
to be confused about weather or not these are already sanitized, or weather
these need to be sanitized by the registration method. Make separate
classes UserName and Password, and move the validation code into their
constructors throwing an exception if the user name is invalid.

Let register take UserName and Password as inputs and adapt the code
in RequestProcessor to match.

Observe: Was the previous code correct?

## Exercise 4: Database usage (3 points)

Goal: The current implementation stores the user data in memory, so that
no state is kept when the server resets. When this exercise is
done, the user data should be securely stored in a SQLite database.

Write a new class called inf226.DataBaseUserStorage which implements KeyedStorage<UserName,User>.

This class should store the values in a SQLite data base, and take care to avoid SQL injection
by using prepared statements:

 - You find the SQLite library in lib/sqlite-jdbc-3.23.1.jar
 - Use JDBC to interface with a SQLite database file which you create.
 - Tables can be made either through JDBC or the command line utility sqlite3.

Make changes so that this is the KeyedStorage used by Server.

Note: To store the messages in a database you can immitate the linked list structure
by having each message contain a *back-reference to the previous message*, and then store the
latest message id as a field in the user table.

## Exercise 5: Using TLS (2 points)

Secure the connection between client and server by using TLS.

 - Generate server keys using keytool
 - Use the SSLSocketFactory class to create the server socket.
 - Use SSLSocket to connect the client to the server

## Exercise 6: Implement tokens for session id (1 point)

In the current implementation, if the connection to the server
is lost, the client exits and the user must restart the client and
log in again.

  - Complete the implementation of the Token class, so that it
    represents a 128 bit token.
  - Extend the protocol so that a LOGIN request can be followed also by:
      - USER username
      - TOKEN token
  - Implement the extended protocol in RequestProcessor.handle
  - Change the client so that it requests a token when logging in.
  - Change the client so that if it encounters an EOFException it
    will attempt to log in again using the token.

Tokens should be transferred using a Base64 encoding.


# Client—server protocol

The client and server communicate through a simple line-based protocol:

## Requests

When the client connect it can make any number of separate requests.
Each request is identified by a keyword:

 - REGISTER
 - LOGIN
 - SEND MESSAGE
 - READ MESSAGES
 - REQUEST TOKEN

A request is made by sending its keyword on a single line, followed by lines giving
additional information pertaining to the request.

### REGISTER

A registration request is made using the REGISTER keyword followed
by two lines:

  - USER username
  - PASS password

Server responses:

 - REGISTERED username
 - FAILED
  
### LOGIN

A login request is made using the LOGIN keyword followed by
two lines

  - USER username
  - PASS password

Server responses:

 - LOGGED IN username
 - FAILED

### READ MESSAGES

The read messages request take no additional parameters, and the server responds
on the form:

 - MESSAGE FROM username
    - message (multiple lines)
 - . (a point on a single line)
 - MESSAGE FROM username
    - message (multiple lines)
 - . (a point on a single line)
 - ⋯
 - END OF MESSAGES

Or, if the user is not logged in:

 - FAILED

### SEND MESSAGES

The send message request takes an additional line

 - RECIPIENT username

…and additional lines with the message content and a
"." on a single line as termination.
 

# Structure of the code

## Client

The client is a simple console based program reading commands from stdin,
and making requests to the server on the user’s behalf.

All of the client code is located in src/inf226/Client.java

## Server

The server listens to port 1337, and responds to requests from
the users.

### Storage

User data is stored in a TransientStorage in Server. The storage
interface provides ways to save, update and delete users from
the server database.

The TransientStorage class merely stores the values in memory.
Part of this exercise will be to create a new storage class
which stores the values in a database.

### Request handling

The RequestProcessor spawns handlers for every client.

The client handlers interface with the rest of the server code through
the static methods of the Server class. The Stored<User> class acts as
a capability for authenticated users. A handler only gets access to the
Stored<User> object after the user is authenticated, but can then
use this object to interact with more of the server methods.




# Optional exercises

## Make it a social application

 - Add support for a user to administer (add,delete) a set of friends.
 - Change the code so that only friends can send you a message.
 - Add a function to send messages to all your friends.

## Create a web based client

Create a web-based client in your web-framework of choice.

 - Use OWASP AntiSamy on the server side to sanitize output for HTML


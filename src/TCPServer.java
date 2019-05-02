import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class TCPServer implements Runnable {
	String key = "ShVmYq3s6v9y$B&E"; // 128 bit key
    String initVector = "RandomInitVector"; // 16 bytes IV
	static ArrayList<String> members = new ArrayList<>();
	static ArrayList<String> Allmembers = new ArrayList<>();
	static ArrayList<Socket> sockets = new ArrayList<>();
	String serverInput = ""; // input to server from client
	String clientOutput = ""; // output to client
	DataOutputStream outToClient;
	Socket connectionSocket;
	static ServerSocket welcomeSocket;
	static String ServerMessage;
	static ArrayList<Thread> t = new ArrayList<>();
	boolean joinFlag = false;

	public TCPServer(Socket conn) {
		connectionSocket = conn;
	}

	public static void main(String argv[]) throws Exception {

		welcomeSocket = new ServerSocket(6000);
		while (true) {
			Socket connectionSocket = welcomeSocket.accept();
			Thread t1 = new Thread(new TCPServer(connectionSocket));
			sockets.add(connectionSocket);

			t.add(t1);
			t1.start();

		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		while (true) {

			try {
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				outToClient = new DataOutputStream(connectionSocket.getOutputStream());

				serverInput = inFromClient.readLine();// this line often causes an exception to be thrown so i
														// surrounded it with a try and catch
				if (serverInput != null) {

					if (serverInput.startsWith("join("))
						JoinResponse();
					else if (serverInput.startsWith("signUp("))
						SignUpResponse();
					else if (serverInput.startsWith("signIn("))
						SignInResponse();
					else if (serverInput.contains("GetMembers()")) {
						MemberResponse();
					}

					else {
						if (serverInput.startsWith("Chat:")) {
							if (joinFlag == false) {
								clientOutput = "SERVER: You are not signed in yet";
								outToClient.writeBytes(clientOutput);
							} else {
								StringTokenizer st = new StringTokenizer(serverInput, ";");
								String dest = st.nextToken();
								String destName = dest.substring(5);
								String m = st.nextToken();
								int ttl = 11;
								String x = "";
								for (int i = 0; i < sockets.size(); i++) {
									if (sockets.get(i) == connectionSocket) {
										x = members.get(i);
									}
								}
								String y = x + ";" + m;
								if (destName.equals("Lobby")) {
									for (int i = 0; i < members.size(); i++) {
										if (sockets.get(i) != connectionSocket)
											Route(y, members.get(i), ttl);
									}
								} else {
									Route(y, destName, ttl);
								}
							}
						}

						else {

						}
					}
				}
				String serverInput1 = serverInput.toUpperCase();
				if ((serverInput1.contains("QUIT") || serverInput1.contains("BYE")) && !serverInput.contains("Chat")) {
					for (Socket s : sockets) {
						DataOutputStream PendingChats = new DataOutputStream(s.getOutputStream());
						PendingChats.writeBytes(clientOutput);

					}

					clientOutput = "TERMINATED";
					String x = "";
					for (int i = 0; i < sockets.size(); i++) {
						if (connectionSocket.equals(sockets.get(i))) {
							x = members.get(i);
							members.remove(i);
							sockets.remove(i);

						}
					}
					for (int i = 0; i < Allmembers.size(); i++) {
						if (Allmembers.get(i).equals(x))
							Allmembers.remove(i);
					}

					connectionSocket = welcomeSocket.accept();
				} else if (serverInput.contains("RE:")) {
					String x = serverInput.substring(3);
					for (int i = 0; i < Allmembers.size(); i++) {
						if (Allmembers.get(i).equals(x))
							Allmembers.remove(i);
					}


				} else {

					clientOutput = "" + '\n';

				}

				outToClient.writeBytes(clientOutput);

			}

			catch (Exception e) {

			}

		}

	}

	public void MemberResponse() throws IOException {
		clientOutput = "Members: ";
		for (int i = 0; i < members.size(); i++) {
			clientOutput += members.get(i) + ", ";

		}
		outToClient.writeBytes(clientOutput + "\n");

	}

	public void JoinResponse() throws IOException {
		String x = serverInput.substring(5, serverInput.length() - 1);

		if (x.contains(",")) {
			clientOutput = "Not Joined";
			outToClient.writeBytes(clientOutput);

		}

		if (x.contains("FromServer")) {
			x = x.substring(10);
			for (int i = 0; i < Allmembers.size(); i++) {
				if (Allmembers.get(i).equals(x)) {
					clientOutput = "Not joined";
					outToClient.writeBytes(clientOutput);
					return;
				}

			}
			Allmembers.add(x);
		} else {
			for (int i = 0; i < Allmembers.size(); i++) {
				if (Allmembers.get(i).equals(x)) {
					clientOutput = "Not joined";
					outToClient.writeBytes(clientOutput);
					return;
				}
			}
			Allmembers.add(x);
			members.add(x);
			joinFlag = true;
			clientOutput = "joined";
			outToClient.writeBytes(clientOutput);

		}

	}

	// server response to sign up request
	// decrypts password and calls hashing function
	public void SignUpResponse() throws IOException {
		String x = serverInput.substring(7, serverInput.length() - 1);
		String[] vals = x.split(",");
		String username = vals[0];
		String password = EncryptDecrypt.decrypt(key, initVector, vals[1]);
		String response = Authentication.signUp(username, password);
		
		if (response.equals("joined!")) {

			joinFlag = true;
			Allmembers.add(username);
			members.add(username);
		}

		outToClient.writeBytes(response);

	}
	
	// server response to sign in request
	// decrypts password and compares hashed salt+password to entry in config file
	public void SignInResponse() throws IOException {

		String x = serverInput.substring(7, serverInput.length() - 1);
		String[] vals = x.split(",");
		String username = vals[0];
		String password = EncryptDecrypt.decrypt(key, initVector, vals[1]);

		String response = Authentication.signIn(username, password);
		if (response.equals("joined!")) {
			joinFlag = true;
			if(!Allmembers.contains(username))
				Allmembers.add(username);
			if(!members.contains(username))
				members.add(username);
		}
		outToClient.writeBytes(response);

	}

	// forwards message to intended recipient
	public void Route(String Message, String Destination, int TTL) throws IOException {


		if (members.contains(Destination)) {
			for (int i = 0; i < members.size(); i++) {
				if (members.get(i).equals(Destination)) {
					System.out.println("sending to dest");
					OutputStream os = (sockets.get(i)).getOutputStream();
					DataOutputStream outToOtherClient = new DataOutputStream(os);
					outToOtherClient.writeBytes(Message + "\n");

				}
			}
		}

	}
}
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class MITM {

	// Vector to store active clients
	static Vector<ClientHandler> ar = new Vector<>();

	// counter for clients
	static int i = 0;

	public static void main(String[] args) throws IOException {
		ServerSocket ss = new ServerSocket(6000);

		Socket s;

		// running infinite loop for getting
		// client request
		while (true) {
			// Accept the incoming request
			s = ss.accept();

			System.out.println("New client request received : " + s);

			// obtain input and output streams
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			System.out.println("Creating a new handler for this client...");

			// Create a new handler object for handling this request.
			ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos);

			// Create a new Thread with this object.
			Thread t = new Thread(mtch);

			System.out.println("Adding this client to active client list");

			// add this client to active clients list
//	        ar.add(mtch); 

			// start the thread.
			t.start();

			// increment i for new client.
			// i is used for naming only, and can be replaced
			// by any naming scheme
//			i++;
		}

	}

	static class ClientHandler implements Runnable {

		private String name;
		final DataInputStream dis;
		final DataOutputStream dos;
		Socket s;
		boolean isloggedin;

		// constructor
		public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
			this.dis = dis;
			this.dos = dos;
			this.name = name;
			this.s = s;
			this.isloggedin = true;
		}

		@Override
		public void run() {
			String received;
			// TODO Auto-generated method stub
			// receive the string
			while (true) {
				try {
					received = dis.readUTF();
					System.out.println("RECIEVED: " + received);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}
}

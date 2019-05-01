import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Base64;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;

public class GUIClient extends JFrame {
	
	String key = "ShVmYq3s6v9y$B&E"; // 128 bit key
    String initVector = "RandomInitVector"; // 16 bytes IV
	JPanel contentPane;
	JLabel label1;
	JTextArea nameArea;
	JPasswordField passwordArea;
	JButton signIn;
	JButton signUp;
	JLabel afterName;
	JTextArea chat;
	JTextArea textArea;
	JTextArea destination;
	JLabel destinationLabel;
	JLabel messageLabel;
	JTextArea message;
	JButton send;
	JButton sendAll;
	JButton quitButton;
	JButton serverMembers;
	JButton imageSelect;
	JScrollPane chatS;
	JScrollBar scrollBar;
	static Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	String serveroutput;
	boolean joinFlag;

	JFileChooser imageChooser;
	Steganography steg = new Steganography();
	BufferedImage img;
	File file;

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 */

	public static void main(String[] args) throws IOException {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIClient frame = new GUIClient();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public GUIClient() throws UnknownHostException, IOException {
		joinFlag = false;
		clientSocket = new Socket("localhost", 6000);
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String clientinp;
		Thread t1 = new Thread(new Runnable() {
			public void run() {

				String serveroutput = null;
				while (true) {
					try {
						inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
						serveroutput = inFromServer.readLine();
						// System.out.println(serveroutput);
						if (serveroutput != null) {
							System.out.println("OUTPUT: " + serveroutput);
							if(serveroutput.equals("Username does not exist.")){
								afterName.setText("Username does not exist.");
								afterName.setVisible(true);
							}
							else if(serveroutput.equals("Username exists.")){
								afterName.setText("Username exists.");
								afterName.setVisible(true);
							}
							
							else if(serveroutput.equals("Incorrect username or password. Please try again!")){
								afterName.setText("Incorrect username or password. Please try again!");
								afterName.setVisible(true);
							}
							else if (serveroutput.equals("Not joined")) {
								afterName.setText("Name already in use, choose another one");
								afterName.setVisible(true);
							} else {
								if (serveroutput.equals("joined!")) {
									signIn.setVisible(false);
									signUp.setVisible(false);
									afterName.setVisible(false);
									label1.setVisible(false);
									nameArea.setVisible(false);
									passwordArea.setVisible(false);
									chatS.setVisible(true);
									quitButton.setVisible(true);
									destinationLabel.setVisible(true);
									messageLabel.setVisible(true);
									destination.setVisible(true);
									message.setVisible(true);
									serverMembers.setVisible(true);
									send.setVisible(true);
									sendAll.setVisible(true);
									imageSelect.setVisible(true);
									contentPane.repaint();
									contentPane.revalidate();
								} else {
									if (serveroutput.contains("Members:")) {
										chat.append(serveroutput + "\n");
										chat.repaint();
										chat.revalidate();
									} else if (serveroutput.length() > 0) {
//										chat.append(serveroutput+"\n");
										String[] vals = serveroutput.split(";");
										String source = vals[0]; // TODO split from
										
										byte[] imageContent = Base64.getDecoder().decode(vals[1]);
										ByteArrayInputStream bis = new ByteArrayInputStream(imageContent);
										BufferedImage bImage2 = ImageIO.read(bis);
										ImageIO.write(bImage2, "png", new File("output.png"));
										String decodedMessage = steg.decode("output.png");
//										String decodedMessage = new String(message);
										String decryptedMessage = EncryptDecrypt.decrypt(key, initVector, decodedMessage);
										chat.append(source + ": " + decryptedMessage + "\n");
										System.out.println("DECODED MESSAGE " + decryptedMessage);
										
//										chat.append(source +  "\n");
										System.out.println("DECODED END!");

									}

								}
							}

							// System.out.println(serveroutput);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						return;
					}
				}
			}

		});

		t1.start();
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(200, 200, 500, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		label1 = new JLabel("Enter a name and a password :)");
		label1.setBounds(6, 82, 219, 31);
		contentPane.add(label1);

		nameArea = new JTextArea();
		nameArea.setBounds(9, 138, 186, 43);
		contentPane.add(nameArea);

		passwordArea = new JPasswordField();
		passwordArea.setBounds(9, 200, 186, 43);
		contentPane.add(passwordArea);

		signIn = new JButton("Sign In");
		signIn.setBounds(282, 148, 117, 29);
		signIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (nameArea.getText().contains(",")) {
					afterName.setText("Your name can not contain a','");
					afterName.setVisible(true);
				} else if (nameArea.getText().length() == 0) {
					afterName.setText("You must enter a name!");
					afterName.setVisible(true);
				} else if (passwordArea.getText().length() == 0) {
					afterName.setText("Please enter a password!");
				} else {
					try {
						// TODO: ENCRYPT AND SEND PASSWORD
						// System.out.println("INSERTED PASSWORD " +passwordArea.getText());
						String password = EncryptDecrypt.encrypt(key, initVector, passwordArea.getText());
						String output = "signIn(" + nameArea.getText() + "," + password + ")\n";
//						 output = EncryptDecrypt.encrypt("Bar12345Bar12345", "RandomInitVector",
//						 output) + "\n";
						
						System.out.println(output);
						outToServer.writeBytes(output);
						passwordArea.setText("");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		contentPane.add(signIn);

		signUp = new JButton("Sign Up");
		signUp.setBounds(282, 180, 117, 29);
		signUp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (nameArea.getText().contains(",")) {
					afterName.setText("Your name can not contain a','");
					afterName.setVisible(true);
				} else if (nameArea.getText().length() == 0) {
					afterName.setText("You must enter a name!");
					afterName.setVisible(true);
				} else if (passwordArea.getText().length() == 0) {
					afterName.setText("Please enter a password!");
				} else {
					try {
						String password = EncryptDecrypt.encrypt(key, initVector, passwordArea.getText());
						outToServer.writeBytes("signUp(" + nameArea.getText() + "," + password + ")\n");

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}
		});
		contentPane.add(signUp);

		afterName = new JLabel("Name already in use, choose another one");
		afterName.setBounds(6, 231, 338, 53);
		afterName.setForeground(Color.BLACK);
		afterName.setFont(new Font("Lucida Grande", Font.BOLD, 12));
		afterName.setVisible(false);
		contentPane.add(afterName);

		chat = new JTextArea("");
		chat.setBounds(16, 18, 308, 228);
		chat.setEditable(false);
		chat.setVisible(true);

		chatS = new JScrollPane();
		chatS.setBounds(16, 20, 320, 250);

		scrollBar = new JScrollBar();
		chatS.setViewportView(chat);
		;
		chatS.setVisible(false);
		contentPane.add(chatS);

		quitButton = new JButton("Quit");
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					outToServer.writeBytes("Quit\n");
					System.exit(0);

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		quitButton.setBounds(346, 95, 102, 29);
		quitButton.setVisible(false);
		contentPane.add(quitButton);

		destination = new JTextArea();
		destination.setBounds(16, 295, 93, 33);
		destination.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		destination.setVisible(false);
		contentPane.add(destination);

		destinationLabel = new JLabel("To:");
		destinationLabel.setBounds(16, 277, 61, 16);
		destinationLabel.setVisible(false);
		contentPane.add(destinationLabel);

		messageLabel = new JLabel("Message:");
		messageLabel.setBounds(130, 277, 61, 16);
		messageLabel.setVisible(false);
		contentPane.add(messageLabel);

		message = new JTextArea();
		message.setBounds(129, 295, 231, 33);
		message.setVisible(false);
		contentPane.add(message);

		send = new JButton("Send");
		send.setBounds(366, 290, 72, 29);
		send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});
		send.setVisible(false);
		contentPane.add(send);

		imageChooser = new JFileChooser();
		imageChooser.setAcceptAllFileFilterUsed(false);
		imageChooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG files", "jpg"));
		imageSelect = new JButton("Image");
		imageSelect.setBounds(366, 240, 72, 29);
		imageSelect.setFocusable(false);
		imageSelect.setVisible(false);
		imageSelect.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				selectImage();
				// System.out.println(""+imageChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY));
				// System.out.println(imageChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES));

			}

		});

		contentPane.add(imageSelect);

		sendAll = new JButton("Send All");
		sendAll.setBounds(366, 320, 92, 29);
		sendAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendAll();
			}
		});
		sendAll.setVisible(false);
		contentPane.add(sendAll);

		serverMembers = new JButton("Server Members");
		serverMembers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					outToServer.writeBytes("GetMembers()\n");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		serverMembers.setBounds(342, 46, 122, 37);
		serverMembers.setVisible(false);
		contentPane.add(serverMembers);

	}

	private void selectImage() {
		int returnVal = imageChooser.showDialog(this, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = imageChooser.getSelectedFile();
				// System.out.println(file.getName());
				// System.out.println(file.getParent());
				img = ImageIO.read(imageChooser.getSelectedFile());

//				format = HidePanel.getImageFormat(imageChooser.getSelectedFile());
//				int c = new StegoImage(img, StegoImage.HIDE_MODE).getMaxHideCapacity();
//				String msg = "The hide capacity for \"" + imageChooser.getSelectedFile().getName() + "\" is " + c + " bytes";
//				messageDialog.showInfo(msg);
			} catch (Exception ex) {
//				messageDialog.showError("Could not read the image");
			}
		}
	}

	private void send() {
		if (message.getText().length() == 0)
			chat.append("You must enter a message\n");
		else if (destination.getText().length() == 0)
			chat.append("You must enter a destination\n");

		else {
			try {
				// TODO: Call steganography , encrypt then send message to server
				if (file == null) {
					chat.append("Please choose an image before sending!");
					return;
				}
				String encryptedMessage = EncryptDecrypt.encrypt(key, initVector, message.getText());

				boolean success = steg.encode(file.getParent(), file.getName(), "jpg", "encoded_" + file.getName(),
						encryptedMessage);
				String newName = file.getParent() + "/encoded_" + file.getName() + ".png";

				File encFile = new File(newName);
				System.out.println(newName);
				byte[] imageContent = new byte[(int) encFile.length()];
				FileInputStream inStream = new FileInputStream(encFile);
				inStream.read(imageContent);
				System.out.println("SENDER IMAGE LENGTH " + imageContent.length);
				String encodedString = Base64.getEncoder().encodeToString(imageContent);

//			System.out.println("encoded " + encodedString);
			//outToServer.writeBytes(EncryptDecrypt.encrypt("Bar12345Bar12345", "RandomInitVector", encodedString));
			outToServer.writeBytes("Chat:" + destination.getText() + ";" + encodedString + "\n");

			String res = steg.decode(newName);
			chat.append("To " + destination.getText() + ": "+ message.getText() + "\n" );
			destination.setText(null);

				message.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private void sendAll() {

		if (message.getText().length() == 0)
			chat.append("You must enter a message\n");

		else {
			try {
				String encryptedMessage = EncryptDecrypt.encrypt(key, initVector, message.getText());
				boolean success = steg.encode(file.getParent(), file.getName(), "jpg", "encoded_" + file.getName(),
						encryptedMessage);
				String newName = file.getParent() + "/encoded_" + file.getName() + ".png";

				File encFile = new File(newName);
				byte[] imageContent = new byte[(int) encFile.length()];
				FileInputStream inStream = new FileInputStream(encFile);
				inStream.read(imageContent);
				String encodedString = Base64.getEncoder().encodeToString(imageContent);

				outToServer.writeBytes("Chat:" + "Lobby" + ";" + encodedString + "\n");
				chat.append("To: Lobby Message: " + message.getText() + "\n");
				destination.setText(null);
				message.setText("");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

}
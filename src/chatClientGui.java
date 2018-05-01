//
//Ryan Slipher
//



import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Dimension;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class chatClientGui extends JFrame {
	private JTextField messageInputTextField;
	private JTextPane textPane;
	private JButton sendButton;
	private JButton exitButton;
	private String hostname = "127.0.0.1";
	private int port;
	//connection data
	private Socket cwSocket;
    private PrintWriter netOut;
    private BufferedReader netIn;
    ListenRun serverListener;
	public chatClientGui() {
		hostname = "127.0.0.1";
		port = 5005;
		setLocation(new Point(100, 100));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ChatWars");
		setPreferredSize(new Dimension(450, 300));
		
		JPanel inputsPanel = new JPanel();
		getContentPane().add(inputsPanel, BorderLayout.SOUTH);
		inputsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel buttonPanel = new JPanel();
		inputsPanel.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new BorderLayout(0, 0));
		
		// Send button
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				netOut.println(messageInputTextField.getText() + "\n");
			}
		});
		buttonPanel.add(sendButton, BorderLayout.WEST);
		
		// Exit button
		exitButton = new JButton("Exit");
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(null,
						"Exit ChatWars",
						"Are you sure you went to leave chatwars?",
						JOptionPane.YES_NO_OPTION);
				//System.out.println(choice);
				if(choice == 0){
					System.out.println("Exit");
					cleanExit();
				}
			}
		});
		buttonPanel.add(exitButton, BorderLayout.EAST);
		
		messageInputTextField = new JTextField();
		inputsPanel.add(messageInputTextField, BorderLayout.CENTER);
		messageInputTextField.setColumns(10);
		
		JScrollPane chatScrollPane = new JScrollPane();
		chatScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		getContentPane().add(chatScrollPane, BorderLayout.CENTER);
		
		textPane = new JTextPane();
		chatScrollPane.setViewportView(textPane);
	}
	
	public boolean connect(String username, String password){
		
		try{
			cwSocket = new Socket(hostname, port);
			netOut = new PrintWriter(cwSocket.getOutputStream(), true);
            netIn = new BufferedReader(
            	new InputStreamReader(cwSocket.getInputStream()));
            String msg;
            
            //get messages
            msg = netIn.readLine();
            System.out.println(msg);
            post(msg);
            
            
            //TODO send username and password
            
            msg = netIn.readLine();
            System.out.println(msg);
            post(msg);
            msg = netIn.readLine();
            System.out.println(msg);
            post(msg);
            
            // start listener thread
            serverListener = new ListenRun(netIn);
            
			
		}catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostname);
            JOptionPane.showMessageDialog(this, "Don't know about host " + hostname);
            cleanExit();
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
            		hostname);
            JOptionPane.showMessageDialog(this,"Couldn't get I/O for the connection to " +
            		hostname);
            cleanExit();
       }
		
		return true;
	}
	
	public void cleanExit(){
		//Cleanly exit program, close socket etc.
		try {
			cwSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
	}

	public static void main(String[] args) {
		chatClientGui window = new chatClientGui();
		
		LoginPanel myPanel = new LoginPanel();
		boolean loginSuccess  = false;
		int result = JOptionPane.showConfirmDialog(window, myPanel, 
				"ChatWars Login", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			System.out.println("Username: " + myPanel.getUsername());
			System.out.println("Password: " + myPanel.getPassword());
			if(!window.connect(myPanel.getUsername(), myPanel.getPassword())){
				JOptionPane.showConfirmDialog(window, "Could not connect");
			}
			
		}else{
			// If login is cancelled, exit the program
			System.exit(1);
		}
		
		// make window visible
		window.setSize(window.getPreferredSize());
		window.setVisible(true);

	}
	
	private static class LoginPanel extends JPanel{
		// have class variables to hold the user name and password.
		private JTextField usernameField;
		private JTextField passwordField;
		
		public LoginPanel(){
			//create a panel for username.
			JPanel userPanel = new JPanel();
			userPanel.setLayout(new BorderLayout());
			usernameField = new JTextField(25);
			userPanel.add(new JLabel("Username:"), BorderLayout.WEST);
			userPanel.add(usernameField, BorderLayout.EAST);
			
			//create a panel for password.
			JPanel passPanel = new JPanel();
			passPanel.setLayout(new BorderLayout());
			passwordField = new JTextField(25);
			passPanel.add(new JLabel("Password:"), BorderLayout.WEST);
			passPanel.add(passwordField, BorderLayout.EAST);
			
			// add each panel to another panel for organization
			JPanel content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(userPanel, BorderLayout.NORTH);
			content.add(passPanel,BorderLayout.SOUTH);
			// add a border to see what was added. TESTING
			//content.setBorder(new javax.swing.border.EtchedBorder());
			
			// add all the content to the base class panel.
			this.setLayout(new BorderLayout());
			this.add(content,BorderLayout.CENTER);
		}
		
		public String getUsername(){
			return usernameField.getText();
			
		}
		
		public String getPassword(){
			return passwordField.getText();
		}
		
	}

	public JTextPane getTextPane() {
		return textPane;
	}
	public JTextField getMessageInputTextField() {
		return messageInputTextField;
	}
	public JButton getSendButton() {
		return sendButton;
	}
	public JButton getExitButton() {
		return exitButton;
	}
	
	public void post(String message){
		int end = textPane.getDocument().getLength();
		try {
			textPane.getDocument().insertString(end, message + "\n", null);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public class ListenRun extends Thread {
		BufferedReader reader;
		ListenRun(BufferedReader readerln){
			this.reader = readerln;
		}
		
		public void run() {
			// compare primes larger than midPrime
			String fromServer = null;
			try {
				while((fromServer = reader.readLine()) != null) {
					System.out.println(fromServer);
					//fromServer
					post(fromServer);
				}
			}catch(IOException e) {
				//TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}



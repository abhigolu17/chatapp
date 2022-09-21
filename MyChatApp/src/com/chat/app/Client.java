package com.chat.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class Client extends JFrame {

	Socket socket;
	BufferedReader br;
	PrintWriter out;

	// Declare Components
	private JLabel heading = new JLabel("Client Area");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto", Font.PLAIN, 20);
	private Font font1 = new Font("Roboto", Font.ITALIC, 20);

	public Client() {
		try {
			System.out.println("Sending Request to Server");
			socket = new Socket("10.10.21.151", 7777);
			System.out.println("Connection Done");

			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out = new PrintWriter(socket.getOutputStream());

			createGui();
			handleEvents();
			startReading();
			startWriting();

		} catch (Exception e) {
		}
	}

	private void handleEvents() {
		messageInput.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
//				System.out.println("key released "+e.getKeyCode());
				if (e.getKeyCode() == 10) {
//					System.out.println("you have prassed enter button");
					String contentToSend = messageInput.getText();
					messageArea.append("Client :" + contentToSend + "\n");
					out.println(contentToSend);
					out.flush();
					messageInput.setText("");
					messageInput.requestFocus();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {

			}

		});

	}

	private void createGui() {
		this.setTitle("Client Message [ONLINE]");
		this.setSize(600, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Coding for components
		heading.setFont(font);
		messageArea.setFont(font1);
		messageInput.setFont(font);

//		heading.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("msg.png")).getImage().getScaledInstance(200, 50, Image.SCALE_SMOOTH)));

		heading.setIcon(new ImageIcon(".png"));
		heading.setHorizontalTextPosition(SwingConstants.CENTER);
		heading.setVerticalTextPosition(SwingConstants.BOTTOM);
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		messageInput.setHorizontalAlignment(SwingConstants.CENTER);

		messageArea.setBackground(Color.CYAN);
		messageInput.setForeground(Color.RED);
		// messageArea.setForeground(Color.BLUE);

		// messageArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

		messageArea.setEditable(false);
		// Frame layout set
		this.setLayout(new BorderLayout());

		// adding the componets to frame
		this.add(heading, BorderLayout.NORTH);
		JScrollPane jScroll = new JScrollPane(messageArea);

		this.add(jScroll, BorderLayout.CENTER);
		this.add(messageInput, BorderLayout.SOUTH);

		this.setVisible(true);
	}

	private void startReading() {
		Runnable r1 = () -> {
			System.out.println("Reader Started ....");
			try {
				while (true) {
					
					String msg = br.readLine();

					if (msg.equals("exit")) {
						// System.out.println("Server terminated the chat");
						JOptionPane.showMessageDialog(this, "Server terminated the chat");
						messageInput.setEnabled(false);
						socket.close();
						break;
					}

					if (msg.equals("Hello")) {
						messageArea.setForeground(Color.black);
					} else{
						messageArea.setForeground(Color.blue);
					}
					// System.out.println("Server :" + msg);
					messageArea.append("Server :" + msg + "\n");

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		};
		new Thread(r1).start();
	}

	private void startWriting() {
		Runnable r2 = () -> {
			System.out.println("Writing Started ....");
			try {
				while (!socket.isClosed()) {

					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

					// messageInput.setHorizontalAlignment(JTextField.LEFT);
					String content = br1.readLine();

					if (content.equals(content)) {
						messageArea.setForeground(Color.blue);
					}

					out.println(content);
					out.flush();

					if (content.equals("exit")) {
						System.out.println("Server terminated the chat");
						socket.close();
						break;
					}
				}
			} catch (IOException e) {
				System.out.println("Connection Closed");
			}
		};
		new Thread(r2).start();
	}

	public static void main(String[] args) {
		System.out.println("This is Client.... Going to Start Client Server");
		new Client();
	}

}

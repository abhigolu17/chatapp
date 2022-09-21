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
import java.net.ServerSocket;
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

public class Server extends JFrame {

	ServerSocket server;
	Socket socket;
	BufferedReader br;
	PrintWriter out;

	private JLabel heading = new JLabel("Server Area");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto", Font.PLAIN, 20);

	public Server() {
		try {
			server = new ServerSocket(7777);
			System.out.println("Server is ready to accept connection");
			System.out.println("Waiting.......");
			socket = server.accept();

			// change to char and stored or handle with bufferedReader
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			out = new PrintWriter(socket.getOutputStream());

			createGui();
			handleEvents();
			startReading();
			startWriting();

		} catch (Exception e) {
			e.printStackTrace();
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
					messageArea.append("Server :" + contentToSend + "\n");
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
		this.setTitle("Server Message [ONLINE]");
		this.setSize(600, 600);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Coding for components
		heading.setFont(font);
		messageArea.setFont(font);
		messageInput.setFont(font);

		heading.setIcon(new ImageIcon(".png"));
		heading.setHorizontalTextPosition(SwingConstants.CENTER);
		heading.setVerticalTextPosition(SwingConstants.BOTTOM);
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		messageInput.setHorizontalAlignment(SwingConstants.CENTER);

		messageArea.setBackground(Color.PINK);
		messageInput.setForeground(Color.RED);
		messageArea.setForeground(Color.MAGENTA);

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
						// System.out.println("Client terminated the chat");
						JOptionPane.showMessageDialog(this, "Client terminated the chat");
						messageInput.setEnabled(false);
						socket.close();
						break;
					}
					messageArea.append("Client :" + msg + "\n");

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

					String content = br1.readLine();
					out.println(content);
					out.flush();

					if (content.equals("exit")) {
						System.out.println("Client terminated the chat");
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
		System.out.println("This is server.... Going to Start Server");
		new Server();
	}

}

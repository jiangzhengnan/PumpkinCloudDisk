package com.example.pumpkinonlinedisk.bean;

import java.net.Socket;

public class SocketObject {
	private Socket socket;
	int currentSocket;
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public int getCurrentSocket() {
		return currentSocket;
	}
	public void setCurrentSocket(int currentSocket) {
		this.currentSocket = currentSocket;
	}
	
}

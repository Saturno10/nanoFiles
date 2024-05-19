package es.um.redes.nanoFiles.tcp.server;


import java.net.Socket;
import java.net.SocketAddress;

public class NFServerThread extends Thread {
	private Socket socket;
	private boolean serverState;
	
	public NFServerThread(Socket socket) {
		this.socket = socket;
		
	}
	
	public void run() {
		
		NFServerComm.serveFilesToClient(socket);
		
		
	}




}
package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class NFServerSimple {

	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private static final int PORT = 10000;
	private ServerSocket serverSocket = null;
	

	public NFServerSimple() throws IOException {
		/*
		 * TODO: Crear una direción de socket a partir del puerto especificado
		 */
		//MEJORA IMPLEMENTADA: Ampliar comando fgservers: si el puerto predeterminado no está disponible prueba otros que si lo estén. Permite tener varios servidores disponibles
		boolean creado = false;
		int usedPort = PORT;
		while(!creado) {
			try {
				InetSocketAddress serverSocketAddress = new InetSocketAddress(usedPort);
				/*
				 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
				 */
				serverSocket = new ServerSocket();
				serverSocket.bind(serverSocketAddress);
				creado = true;
			} catch (IOException e) {
				usedPort++;
			}
		}
		
		
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * @throws IOException 
	 * 
	 */
	public void run() throws IOException {
		/*
		 * TODO: Comprobar que el socket servidor está creado y ligado
		 */
		boolean serverState = true;
		if(serverSocket!=null && serverSocket.isBound()) {
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
			
			System.out.println("\nServer is listening on port " + serverSocket.getLocalPort() + "\nType " + STOP_SERVER_COMMAND + " to stop the server");
			while(serverState) {
				Socket socket = serverSocket.accept();
				System.out.println("\n Client connected. Client info:\n InetAddress: "+socket.getInetAddress().toString()+"\n Port: " + socket.getPort());
				NFServerComm.serveFilesToClient(socket);
				
			}
			
		/*
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
			
		}else {
			System.err.println("Server initiation failed");
			
		}


		System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
	}
}

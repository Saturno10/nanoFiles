package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import es.um.redes.nanoFiles.logic.NFControllerLogicDir;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link NFServerThread} cada vez que se conecte un cliente.
 */
public class NFServer implements Runnable {

	private ServerSocket serverSocket = null;
	private boolean stopServer = false;
	private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private static final int PORT = 10000;
	private NFControllerLogicDir controllerDir;
	public NFServer(NFControllerLogicDir controllerDir) throws IOException {
		/*
		 * TODO: Crear un socket servidor y ligarlo a cualquier puerto disponible
		 */
		this.controllerDir = controllerDir;
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
				serverSocket.setSoTimeout(SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS);
				creado = true;
				
			} catch (IOException e) {
				usedPort++;
			}
		}


	}

	/**
	 * Método que crea un socket servidor y ejecuta el hilo principal del servidor,
	 * esperando conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	
	
	public void run(){
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		try {
			controllerDir.registerFileServer(this.serverSocket.getLocalPort());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\nServer is listening on port " + this.getServerPort() + "\nType " + STOP_SERVER_COMMAND + " to stop the server");
		BufferedReader stopper = new BufferedReader(new InputStreamReader(System.in));	
		while(!stopServer) {
			try {
				Socket socket = serverSocket.accept();
				System.out.println("\n Client connected. Client info:\n InetAddress: "+socket.getInetAddress().toString()+"\n Port: " + socket.getPort());
				
				NFServerThread st = new NFServerThread(socket);
				st.start();
			} catch (SocketTimeoutException e) {
				try {
					if (stopper.ready()) {
						String input = stopper.readLine();
						if(input.equals(STOP_SERVER_COMMAND)) {
							stopServer();
							System.out.println("Server closed");
}		}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		try {
			controllerDir.unregisterFileServer();
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
		 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
		 * hay que pasarle el socket devuelto por accept
		 */
		/*
		 * TODO: (Opcional) Crear un hilo nuevo de la clase NFServerThread, que llevará
		 * a cabo la comunicación con el cliente que se acaba de conectar, mientras este
		 * hilo vuelve a quedar a la escucha de conexiones de nuevos clientes (para
		 * soportar múltiples clientes). Si este hilo es el que se encarga de atender al
		 * cliente conectado, no podremos tener más de un cliente conectado a este
		 * servidor.
		 */



	}
	/**
	 * TODO: Añadir métodos a esta clase para: 1) Arrancar el servidor en un hilo
	 * nuevo que se ejecutará en segundo plano 2) Detener el servidor (stopserver)
	 * 3) Obtener el puerto de escucha del servidor etc.
	 */
	public int getServerPort() {
		return serverSocket.getLocalPort();
	}
	
	public void stopServer() {
		stopServer = true;
	}




}
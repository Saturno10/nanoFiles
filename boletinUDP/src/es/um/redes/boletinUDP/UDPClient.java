package es.um.redes.boletinUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class UDPClient {

	public final static int MAX_MSG_SIZE_BYTES = 32;
	private static final int SOCKET_RECV_TIMEOUT = 2000;

	private static final String[] messages = { "**one**", "++++two++++", "#######three######", "%four%", "END" };

	public static void main(String[] args) throws IOException {
		String serverName;
		System.out.println("**** UDP CLIENT ****");
		if (args.length == 1) {
			serverName = args[0];
		} else {
			serverName = "localhost";
			System.out.println("WARNING: You did not specify the server name nor IP. Default server location is '"
					+ serverName + "'");
			System.out.println("Make sure both server and client are both running on this host");
		}
		System.out.println("Attempting to reach server located in " + serverName);

		/******** SEND TO SERVER **********/
		// Creamos un socket UDP en cualquier puerto disponible
		DatagramSocket socket = new DatagramSocket();
		System.out.println("Created UDP socket at local addresss " + socket.getLocalSocketAddress());

		// Enviamos una serie de mensajes al servidor UDP mediante datagramas
		for (String messageToServer : messages) {
			System.out.println("******************************************");

			// Obtenemos los datos que vamos a enviar: messageToServer
			System.out.println("Sending message to server: \"" + messageToServer + "\"");
			// Extraemos los bytes en que se codifica el string
			byte[] dataToServer = messageToServer.getBytes();
			// Creamos la dirección de socket del servidor (IP+puerto) a la que enviar el
			// datagram
			// NOTA: Se usa InetAddress.getByName(String) para convertir un String con una
			// IP o nombre de host a un objeto de la clase InetAddress
			// PORT es el puerto destino donde escucha el servidor UDP
			InetAddress serverIp = InetAddress.getByName(serverName);
			InetSocketAddress serverSocketAddr = new InetSocketAddress(serverIp, UDPServer.PORT);
			// Creamos el datagrama a enviar a la dirección destino, con el buffer de datos
			// a enviar y la longitud de los datos (en este caso, todo el búfer)
			DatagramPacket packetToServer = new DatagramPacket(dataToServer, dataToServer.length, serverSocketAddr);

			System.out.println("Press Enter key to send the message...");
			System.in.read();
			// Enviamos el datagrama
			socket.send(packetToServer);

			/******** RECEIVE FROM SERVER **********/
			// Preparamos el búfer para recibir la respuesta
			byte[] recvBuf = new byte[MAX_MSG_SIZE_BYTES];
			// Creamos un datagrama asociado al búfer de recepción
			DatagramPacket packetFromServer = new DatagramPacket(recvBuf, recvBuf.length);
			// Establecemos un temporizador de 2 segundos para evitar que receive se
			// bloquee indefinidamente (en caso de que el servidor no responda)
			socket.setSoTimeout(SOCKET_RECV_TIMEOUT);
			// Tratamos de recibir la respuesta
			socket.receive(packetFromServer);
			// Creamos un string a partir del contenido del búfer de recepción, sabiendo el
			// tamaño de los datos recibidos
			String messageFromServer = new String(recvBuf, 0, packetFromServer.getLength());
			// Podemos obtener la dirección del servidor también del datagrama
			SocketAddress responseAddr = packetFromServer.getSocketAddress();
			System.out.println("Datagram received from server at addr " + responseAddr);
			System.out.println(
					" Size: " + packetFromServer.getLength() + " bytes - Contents: \"" + messageFromServer + "\"");
		}
		socket.close();
	}
}
package es.um.redes.boletinUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UDPServer {

	final static int PORT = 6868; // Package visibility to allow access to UDP client
	private static final int MAX_MSG_SIZE_BYTES = 32;

	public static void main(String[] args) throws IOException {
		System.out.println("**** UDP SERVER ****");

		// Creamos un socket UDP y lo ligamos al número de puerto especificado
		// (conocido por el cliente)
		DatagramSocket socket = new DatagramSocket(PORT);
		System.out.println("Server listening on socket addresss " + socket.getLocalSocketAddress());

		// Reservamos buffer para recibir mensajes
		byte[] recvBuf = new byte[MAX_MSG_SIZE_BYTES];
		System.out.println(" Reception buffer contents: " + Arrays.toString(recvBuf));
		boolean stopServer = false;
		while (!stopServer) {
			System.out.println("******************************************");
			/******** RECEIVE FROM CLIENT **********/
			// Creamos el datagrama
			DatagramPacket packetFromClient = new DatagramPacket(recvBuf, recvBuf.length);
			// Tratamos de recibir un message a través del socket: la llamada socket.receive
			// no retorna hasta haber recibido un datagrama en el puerto indicado. El
			// contenido del datagrama recibido estará en recvBuf, que es el array de bytes
			// subyacente del datagrama (referenciado por packetFromClient.getData())
			System.out.println("Waiting to receive datagram...");
			socket.receive(packetFromClient);
			String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
			System.out.println("**** Datagram received - Current time is "+currentTime);
			// Hacemos algo con los datos que hemos recibido en "recvBuf"
			System.out.println(" Reception buffer contents: " + Arrays.toString(recvBuf));
			System.out.println(" Datagram size: " + packetFromClient.getLength() + " bytes");
			// Creamos un string usando los datos de recvBuf desde 0 a packetLength-1
			String messageFromClient = new String(recvBuf, 0, packetFromClient.getLength());
			System.out.println(" Contents interpreted as "+packetFromClient.getLength()+"-byte String: \"" + messageFromClient+"\"");
			if (messageFromClient.equals("END")) {
				System.out.println("UDP server terminating due to reception of \"END\" message");
				System.out.println("No response will be sent... UDP client will get a SocketTimeoutException!");
				break;
			}
			/******** SEND TO CLIENT **********/
			String messageToClient = new String(messageFromClient+" "+currentTime);
			// Obtenemos el array de bytes en que se codifica este string
			byte[] dataToClient = messageToClient.getBytes();
			// Para enviar una respuesta al cliente, debemos extraer la dirección "origen"
			// del datagrama recibido anteriormente
			// Averiguamos la dirección de socket del cliente que envió este datagram
			// (IP+puerto origen)
			InetSocketAddress clientAddr = (InetSocketAddress) packetFromClient.getSocketAddress();
			System.out.println(
					"Sending datagram with message \"" + messageToClient + "\"");
			System.out.println("Destination is client at addr: " + clientAddr);
			DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
			socket.send(packetToClient);
		}
		socket.close();
	}
}
package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class PeerMessageTest {

	public static void main(String[] args) throws IOException {
		String nombreArchivo = "peermsg.bin";
		DataOutputStream fos = new DataOutputStream(new FileOutputStream(nombreArchivo));
		
		/*
		 * TODO: Probar a crear diferentes tipos de mensajes (con los opcodes válidos
		 * definidos en PeerMessageOps), estableciendo los atributos adecuados a cada
		 * tipo de mensaje. Luego, escribir el mensaje a un fichero con
		 * writeMessageToOutputStream para comprobar que readMessageFromInputStream
		 * construye un mensaje idéntico al original.
		 */
		
		
	
		PeerMessage msgOut = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_OK);
		msgOut.setLengthFile("hola".length());
		msgOut.setfile("hola".getBytes());
		
		
		
		msgOut.writeMessageToOutputStream(fos);

		DataInputStream fis = new DataInputStream(new FileInputStream(nombreArchivo));
		PeerMessage msgIn = PeerMessage.readMessageFromInputStream((DataInputStream) fis);
		/*
		 * TODO: Comprobar que coinciden los valores de los atributos relevantes al tipo
		 * de mensaje en ambos mensajes (msgOut y msgIn), empezando por el opcode.
		 */
		//Comprobador universal para ver que todo funciona
		if (msgOut.getOpcode() != msgIn.getOpcode()) {
			System.err.println("Opcode does not match!");
		}else {
			System.out.println("Opcode OK!");
		}
		if (msgOut.getFileList() != msgIn.getFileList()) {
			System.err.println("FileList does not match!");
		}else {
			System.out.println("FileList OK!");
		}
		if(msgOut.getfile() != msgIn.getfile()) {
			System.err.println("File does not match!");
			System.out.println(msgOut.getfile());
			System.out.println(msgIn.getfile());
		}else {
			System.out.println("File OK!");
			
		}
		if(msgOut.getHashFile() != msgIn.getHashFile()) {
			System.err.println("HashFile does not match!");
		}else {
			System.out.println("HashFile OK!");
		}
		if(msgOut.getLengthFile() != msgIn.getLengthFile()) {
			System.err.println("LengthFile does not match!");
		}else {
			System.out.println("LengthFile OK!");
		}
		if(!msgOut.getServerNick().equals(msgIn.getServerNick())) {
			System.err.println("ServerNick does not match!");
		}else {
			System.out.println("ServerNick OK!");
		}
			
	}
}




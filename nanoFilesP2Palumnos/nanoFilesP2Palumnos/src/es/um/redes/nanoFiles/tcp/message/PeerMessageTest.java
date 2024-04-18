package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

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
		PeerMessage msgOut = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FROM_OK);
		msgOut.setFileSize(45);
		String hola = "hola";
		msgOut.setHash("adios");
		byte[] holaBytes = hola.getBytes();
		msgOut.setFile(holaBytes);

		msgOut.writeMessageToOutputStream(fos);
		DataInputStream fis = new DataInputStream(new FileInputStream(nombreArchivo));
		PeerMessage msgIn = PeerMessage.readMessageFromInputStream(fis);
		/*
		 * TODO: Comprobar que coinciden los valores de los atributos relevantes al tipo
		 * de mensaje en ambos mensajes (msgOut y msgIn), empezando por el opcode.
		 */
		if(msgOut.getOpcode() != msgIn.getOpcode()) {
			System.err.println("Opcode does not match!");
		} else {
			System.out.println("Opcode OK!");
		}
		if(!msgOut.getNickname().equals(msgIn.getNickname())) {
			System.err.println("Nickname does not match!");
		} else {
			System.out.println("Nickname OK!");
		}
		if(!msgOut.getHash().equals(msgIn.getHash())) {
			System.err.println("Hash does not match!");
			
		} else {
			System.out.println("Hash OK!");
		}
		if(!msgOut.getFileName().equals(msgIn.getFileName())) {
			System.err.println("Filename does not match!");
		} else {
			System.out.println("Filename OK!");
		}
		if(msgOut.getFileSize() != msgIn.getFileSize()) {
			System.err.println("Filesize does not match!");
		} else {
			System.out.println("Filesize OK!");
		}
		if(!Arrays.equals(msgOut.getFile(), msgIn.getFile())) {
			System.err.println("File does not match!");
		} else {
			System.out.println("File OK!");
		}
	}

}
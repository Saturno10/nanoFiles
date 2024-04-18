package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.time.LocalDateTime;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: Crear dis/dos a partir del socket
		 */
		
		try {
			
			DataInputStream dis = new DataInputStream(socket.getInputStream());
			DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
			FileInfo[] ficheros = NanoFiles.db.getFiles();
			
			
			while(socket.isConnected()) {	
				PeerMessage requestMessage = PeerMessage.readMessageFromInputStream(dis);
				PeerMessage answerMessage = new PeerMessage();
				switch (requestMessage.getOpcode()) {
				case PeerMessageOps.OPCODE_DOWNLOAD_FROM:
					System.out.println("Download request recieved");
					FileInfo[] coincidencias = FileInfo.lookupHashSubstring(ficheros, requestMessage.getHash());
					switch (coincidencias.length) {
					case 0:
						System.out.println("File not found");
						answerMessage.setOpcode(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
						break;
					case 1:
						System.out.println("File found");
						answerMessage.setOpcode(PeerMessageOps.OPCODE_DOWNLOAD_FROM_OK);
						answerMessage.setFileSize((int) coincidencias[0].fileSize);
						answerMessage.setHash(coincidencias[0].fileHash);
						File fichero = new File(NanoFiles.db.lookupFilePath(coincidencias[0].fileHash));
						try(FileInputStream fis = new FileInputStream(fichero)){
							byte[] bytesFichero = fis.readAllBytes();
							answerMessage.setFile(bytesFichero);
						}
						break;

					default:
						System.out.println("Ambiguous download request");
						answerMessage.setOpcode(PeerMessageOps.OPCODE_AMBIGUOUS_DOWNLOAD);
						break;
					}
					System.out.println("Response sent");
					answerMessage.writeMessageToOutputStream(dos);
					
					break;

				default:
					System.err.println("Unknown request");
					break;
				}
				
			}	
				
			dis.close();
			dos.close();
			socket.close();
			
				
				
			
			
			
		} catch (IOException e) {
			System.out.println("Client disconnected");
			
		}
		
		/*
		 * TODO: Mientras el cliente esté conectado, leer mensajes de socket,
		 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
		 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
		 */
		/*
		 * TODO: Para servir un fichero, hay que localizarlo a partir de su hash (o
		 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
		 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
		 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
		 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
		 * devuelve la ruta al fichero a partir de su hash completo.
		 */
	}
}
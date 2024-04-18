package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileDigest;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;
	DataInputStream dis;
	DataOutputStream dos;


	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO Se crea el socket a partir de la dirección del servidor (IP, puerto). La
		 * creación exitosa del socket significa que la conexión TCP ha sido
		 * establecida.
		 */
		socket = new Socket(serverAddr.getAddress(), serverAddr.getPort());
		/*
		 * TODO Se crean los DataInputStream/DataOutputStream a partir de los streams de
		 * entrada/salida del socket creado. Se usarán para enviar (dos) y recibir (dis)
		 * datos del servidor.
		 */
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
	}

	/**
	 * Método para descargar un fichero a través del socket mediante el que estamos
	 * conectados con un peer servidor.
	 * 
	 * @param targetFileHashSubstr Subcadena del hash del fichero a descargar
	 * @param file                 El objeto File que referencia el nuevo fichero
	 *                             creado en el cual se escribirán los datos
	 *                             descargados del servidor
	 * @return Verdadero si la descarga se completa con éxito, falso en caso
	 *         contrario.
	 * @throws IOException Si se produce algún error al leer/escribir del socket.
	 */
	public boolean downloadFile(String targetFileHashSubstr, File file) throws IOException {
		boolean downloaded = false;
		/*
		 * TODO: Construir objetos PeerMessage que modelen mensajes con los valores
		 * adecuados en sus campos (atributos), según el protocolo diseñado, y enviarlos
		 * al servidor a través del "dos" del socket mediante el método
		 * writeMessageToOutputStream.
		 */
		//TEST PARA EL BOLETIN
		//EL CÓDIGO DE JUSTO ABAJO NO ES CORRECTO PARA LA PRACTICA FINAL, EL RESTO COMENTADO SI
		//Este código funciona si se manda el mensaje y luego se cierra el servidor. Falta despillar el server
		/*
		dos.writeInt(5);
		int test = dis.readInt();
		if (test == 5) {
			System.out.println("Todo bien");
			downloaded = true;
		}
		*/
		
		
		//dis.readUTF();
		PeerMessage outputMessage = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FROM);
		outputMessage.setHash(targetFileHashSubstr);
		outputMessage.setFileName(file.getName());
		outputMessage.setNickname(this.getServerAddr().getHostName());
		
		outputMessage.writeMessageToOutputStream(dos);
		System.out.println("Download request sent to server with InetAddress:" + socket.getInetAddress().toString());
		/*
		 * TODO: Recibir mensajes del servidor a través del "dis" del socket usando
		 * PeerMessage.readMessageFromInputStream, y actuar en función del tipo de
		 * mensaje recibido, extrayendo los valores necesarios de los atributos del
		 * objeto (valores de los campos del mensaje).
		 */
		
		PeerMessage inputMessage = PeerMessage.readMessageFromInputStream(dis);
		System.out.println("Response recieved:");
		byte opcodeInput =  inputMessage.getOpcode();
		int fileSizeInput = inputMessage.getFileSize();
		byte[] fileInput = inputMessage.getFile();
		String fileHashInput = inputMessage.getHash();
		/*
		 * TODO: Para escribir datos de un fichero recibidos en un mensaje, se puede
		 * crear un FileOutputStream a partir del parámetro "file" para escribir cada
		 * fragmento recibido (array de bytes) en el fichero mediante el método "write".
		 * Cerrar el FileOutputStream una vez se han escrito todos los fragmentos.
		 */
		if(opcodeInput == PeerMessageOps.OPCODE_DOWNLOAD_FROM_OK) {
			
			File f = new File("aux");
			if(!f.exists()) {
				f.createNewFile();
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(fileInput);
				fos.close();
			} else {
				FileOutputStream fos = new FileOutputStream(f, false);
				fos.write(fileInput);
				fos.close();
			}
			
			if( FileDigest.computeFileChecksumString(f.getAbsolutePath()).equals(fileHashInput)) {
				FileOutputStream fileResult = new FileOutputStream(file);
				fileResult.write(fileInput);
				fileResult.close();
				if (file.length() == fileSizeInput) downloaded = true;
				
			}
			
			
			
		} 
		
		
		
		/*
		 * NOTA: Hay que tener en cuenta que puede que la subcadena del hash pasada como
		 * parámetro no identifique unívocamente ningún fichero disponible en el
		 * servidor (porque no concuerde o porque haya más de un fichero coincidente con
		 * dicha subcadena)
		 */

		/*
		 * TODO: Finalmente, comprobar la integridad del fichero creado para comprobar
		 * que es idéntico al original, calculando el hash a partir de su contenido con
		 * FileDigest.computeFileChecksumString y comparándolo con el hash completo del
		 * fichero solicitado. Para ello, es necesario obtener del servidor el hash
		 * completo del fichero descargado, ya que quizás únicamente obtuvimos una
		 * subcadena del mismo como parámetro.
		 */
		dis.close();
		dos.close();
		socket.close();
		return downloaded;
	}





	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
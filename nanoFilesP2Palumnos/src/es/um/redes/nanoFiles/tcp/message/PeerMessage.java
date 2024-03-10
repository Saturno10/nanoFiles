package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {

//FALTA AÑADIR VALOR LONGITUD PARA PODER TRABAJAR BIEN


	private byte opcode;
	
	private byte[] file;
	private String fileList = "";
	private int hashFile;
	private int lengthFile;
	private String serverNick = "";
	

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */




	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
	

	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}
	
	public byte[] getfile() {
		return file;
	}
	public int getHashFile() {
		return hashFile;
	}
	public int getLengthFile() {
		return lengthFile;
	}
	public String getServerNick() {
		return serverNick;
	}
	public String getFileList() {
		return fileList;
	}
	public void setOpcode(byte opcode) {
		this.opcode = opcode;
	}
	
	public void setfile(byte[] file) {
		this.file = file;
	}
	public void setHashFile(int hashFile) {
		this.hashFile = hashFile;
	}
	public void setLengthFile(int lengthFile) {
		this.lengthFile = lengthFile;
	}
	public void setServerNick(String serverNick) {
		this.serverNick = serverNick;
	}
	public void setFileList(String fileList) {
		this.fileList = fileList;
	}


	


	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		switch (opcode) {
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:
			message.opcode = PeerMessageOps.OPCODE_FILE_NOT_FOUND;
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD:
			message.opcode = PeerMessageOps.OPCODE_DOWNLOAD;
			message.hashFile = dis.readInt();
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD_FROM:
			message.opcode = PeerMessageOps.OPCODE_DOWNLOAD_FROM;
			message.hashFile = dis.readInt();
			message.serverNick = new String(dis.readAllBytes());
			break;
		case PeerMessageOps.OPCODE_FILELIST:
			message.opcode = PeerMessageOps.OPCODE_FILELIST;
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD_OK:
			message.opcode = PeerMessageOps.OPCODE_DOWNLOAD_OK;
			message.lengthFile = dis.readInt();
			message.file = dis.readAllBytes();
			break;
		case PeerMessageOps.OPCODE_FILELIST_OK:
			message.opcode = PeerMessageOps.OPCODE_FILELIST_OK;
			message.fileList = new String(dis.readAllBytes());
			break;


		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
		case PeerMessageOps.OPCODE_FILE_NOT_FOUND:
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD:
			dos.writeInt(hashFile);
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD_FROM:
			dos.writeInt(hashFile);
			dos.writeBytes(serverNick);
		
			break;
		case PeerMessageOps.OPCODE_FILELIST:		
			break;
		case PeerMessageOps.OPCODE_DOWNLOAD_OK:
			dos.writeInt(lengthFile);
			dos.write(file);
			break;
		case PeerMessageOps.OPCODE_FILELIST_OK:
			
			dos.writeBytes(fileList);
			break;
			



		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}

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

	private byte opcode;

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */
	private String nickname;
	private String hash;
	private String fileName;
	private int fileSize;
	private byte[] file;

	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
		this.nickname = "";
		this.fileName = "";
		this.hash = "";
	}

	public PeerMessage(byte op) {
		opcode = op;
		this.nickname = "";
		this.fileName = "";
		this.hash = "";
	}

	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
		this.file = file;
	}

	public void setOpcode(byte opcode) {
		this.opcode = opcode;
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
		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode);
		switch (opcode) {
		case(PeerMessageOps.OPCODE_FILE_NOT_FOUND):
		case(PeerMessageOps.OPCODE_AMBIGUOUS_DOWNLOAD):
			break;
		case(PeerMessageOps.OPCODE_DOWNLOAD_FROM):
			message.nickname = dis.readUTF();
			message.hash = dis.readUTF();
			message.fileName = dis.readUTF();
			break;
		case(PeerMessageOps.OPCODE_DOWNLOAD_FROM_OK):
			message.hash = dis.readUTF();
			message.fileSize = dis.readInt();
			
			message.file = dis.readNBytes(message.fileSize);
			
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
		case(PeerMessageOps.OPCODE_FILE_NOT_FOUND):
		case(PeerMessageOps.OPCODE_AMBIGUOUS_DOWNLOAD):
			break;
		case(PeerMessageOps.OPCODE_DOWNLOAD_FROM):
			dos.writeUTF(nickname);
			dos.writeUTF(hash);
			dos.writeUTF(fileName);
			break;
		case(PeerMessageOps.OPCODE_DOWNLOAD_FROM_OK):
			dos.writeUTF(hash);
			dos.writeInt(fileSize);
			
			dos.write(file);
			
			break;
		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}
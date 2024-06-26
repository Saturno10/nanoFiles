package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.*;
import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;


public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;
	public static final String LOGIN_OK = "loginok";
	public static final String LOGIN_ERR = "login_failed";
	public static final String LOGOUT_OK = "logoutok";
	public static final String LOGOUT_ERR = "logout_failed";
	public static final String LIST_OK = "userlistok";
	public static final String LIST_ERR = "userlist_failed";
	public static final String REGISTER_SERVER_OK = "registerserverok";
	public static final String REGISTER_SERVER_ERR = "registerserver_failed";
	public static final String UNREGISTER_SERVER_OK  = "unregisterserverok";
	public static final String UNREGISTER_SERVER_ERR = "unregisterserver_failed";
	public static final String DOWNLOADFROM_OK = "downloadfromok";
	public static final String DOWNLOADFROM_ERR = "downloadfrom_failed";
	public static final String PUBLISH_OK = "publishok";
	public static final String PUBLISH_ERR = "publish_failed";
	public static final String SEARCH_OK = "searchok";
	public static final String SEARCH_ERR = "search_failed";
	public static final String FILELIST_OK = "filelistok";
	public static final String FILELIST_ERR = "filelist_failed";


	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * 
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */
	//Estructuras para guardar usuarios que son servidores <UserKey,Port> <UserKey,IP>
	private HashMap<Integer,Integer> registeredServersPorts;

	private HashMap<Integer, InetAddress> registeredServersIPs;
	//Estructura para guardar los ficheros publicados por los servidores <Userkey,<Hash,fileInfo[2]>> fileInfo[0] = fileName, fileInfo[1] = fileSize

	private HashMap<Integer, HashMap<String,String[]>> publishedFiles;


	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		this.socket = new DatagramSocket(DIRECTORY_PORT);

		/*
		 * TODO: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */
		this.nicks = new HashMap<String, Integer>();
		this.sessionKeys = new HashMap<Integer, String>();
		this.registeredServersPorts = new HashMap<Integer,Integer>();
		this.registeredServersIPs = new HashMap<Integer, InetAddress>();
		this.publishedFiles = new HashMap<Integer , HashMap<String,String[]>>();
		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = null;
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		/*
		 * TODO: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */
		receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
		DatagramPacket packetFromClient = new DatagramPacket(receptionBuffer, receptionBuffer.length);

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio

			// TODO: (Boletín UDP) Recibimos a través del socket un datagrama
			System.out.println("Waiting to receive datagram...");
			socket.receive(packetFromClient);
			// TODO: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido
			dataLength = packetFromClient.getLength();
			// TODO: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido
			clientAddr = (InetSocketAddress)packetFromClient.getSocketAddress();
			
			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {
				/*
				 * TODO: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */
				String messageFromClient = new String(receptionBuffer, 0, dataLength);

				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * TODO: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */
					
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}
					
					String login = "login";
					if(messageFromClient.equals(login)) {
						String messageToClient = new String("loginok");
						byte[] dataToClient = messageToClient.getBytes();
						System.out.println("Sending datagram with message \"" + messageToClient + "\"");
						System.out.println("Destination is client at addr: " + clientAddr);
						DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
						socket.send(packetToClient);
					} else {
						System.err.println("Login fallido.");		
					}

				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}

					/*
					 * TODO: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */
					System.out.println(messageFromClient);
					DirMessage request = DirMessage.fromString(messageFromClient);
					/*
					 * TODO: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */
					DirMessage mensajeProcesado = buildResponseFromRequest(request, clientAddr);
					/*
					 * TODO: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */
					String paraEnviar = mensajeProcesado.toString();
					byte[] dataToClient = paraEnviar.getBytes();
					System.out.println("Sending datagram with message \"" + paraEnviar + "\"");
					System.out.println("Destination is client at addr: " + clientAddr);
					DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
					socket.send(packetToClient);
				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}

		}
	}

	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * TODO: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */
		String operation = msg.getOperation();

		DirMessage response = null;


		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {
			String username = msg.getNickname();

			/*
			 * TODO: Comprobamos si tenemos dicho usuario registrado (atributo "nicks"). Si
			 * no está, generamos su sessionKey (número aleatorio entre 0 y 1000) y añadimos
			 * el nick y su sessionKey asociada. NOTA: Puedes usar random.nextInt(10000)
			 * para generar la session key
			 */
			if(!nicks.containsKey(username)) {
				Integer sessionKey = random.nextInt(1000);
				nicks.put(username, sessionKey);
				sessionKeys.put(sessionKey, username);
				response = new DirMessage(LOGIN_OK);
				response.setNickname(username);
				response.setSessionKey(sessionKey.toString());
				System.out.println("Login successful.");
			} else {
				response = new DirMessage(LOGIN_ERR);
				System.out.println("ERROR: Login failed.");
			}
			/*
			 * TODO: Construimos un mensaje de respuesta que indique el éxito/fracaso del
			 * login y contenga la sessionKey en caso de éxito, y lo devolvemos como
			 * resultado del método.
			 */
			/*
			 * TODO: Imprimimos por pantalla el resultado de procesar la petición recibida
			 * (éxito o fracaso) con los datos relevantes, a modo de depuración en el
			 * servidor
			 */
			return response;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			int sessionKey = Integer.parseInt(msg.getSessionKey());
			if(sessionKeys.containsKey(sessionKey) && !registeredServersPorts.containsKey(sessionKey)) {
				String username = sessionKeys.get(sessionKey);
				nicks.remove(username);
				sessionKeys.remove(sessionKey);
				registeredServersPorts.remove(sessionKey);
				response = new DirMessage(LOGOUT_OK);
				response.setSessionKey(msg.getSessionKey());
				response.setNickname(username);
				System.out.println("Logout successful.");
				break;
			} else {
				response = new DirMessage(LOGOUT_ERR);
				System.out.println("ERROR: Logout error. Invalid sessionKey");
				break;
			}
		}
		case DirMessageOps.OPERATION_LIST: {
			int sessionKey = Integer.parseInt(msg.getSessionKey());
			if(sessionKeys.containsKey(sessionKey)) {
				String username = "";
				for(String usr : nicks.keySet()) {
					String usrInfo = usr;
					if(registeredServersPorts.containsKey(nicks.get(usr))) {
						usrInfo+=":Server";
						usrInfo+="\t Port: " + registeredServersPorts.get(nicks.get(usr));
					}else {
						usrInfo+=":User";
					}
					username += usrInfo+",";
				}
				response = new DirMessage(LIST_OK);
				response.setUsers(username);
				response.setSessionKey(msg.getSessionKey());
				response.setNickname(sessionKeys.get(Integer.parseInt(msg.getSessionKey())));
				System.out.println("List successful.");
			} else {
				response = new DirMessage(LIST_ERR);
				System.out.println("ERROR: List failed. Invalid sessionKey");
			}
			break;
		}
		case DirMessageOps.OPERATION_REGISTER_SERVER: {
			int sessionKey = Integer.parseInt(msg.getSessionKey());
			int port = Integer.parseInt(msg.getPort());
			if(sessionKeys.containsKey(sessionKey)) {
				response = new DirMessage(REGISTER_SERVER_OK);
				registeredServersPorts.put(sessionKey, port);
				registeredServersIPs.put(sessionKey, clientAddr.getAddress());
				System.out.println("Server registered successfully");
			}else {
				response = new DirMessage(REGISTER_SERVER_ERR);
				System.out.println("ERROR: Server registration failed. User not found");
			}
			break;
		}
		case DirMessageOps.OPERATION_UNREGISTER_SERVER: {
			int sessionKey = Integer.parseInt(msg.getSessionKey());
			if (registeredServersPorts.containsKey(sessionKey)) {
				registeredServersPorts.remove(sessionKey);
				registeredServersIPs.remove(sessionKey);
				publishedFiles.remove(sessionKey);
				System.out.println("Server unregistered successfully");
				response = new DirMessage(UNREGISTER_SERVER_OK);
			} else {
				response = new DirMessage(UNREGISTER_SERVER_ERR);
				System.out.println("ERROR: Server unregistration failed. Server was not registered to begin with");
			}
			break;
		}
		case DirMessageOps.OPERATION_DOWNLOADFROM: {
			String nick = msg.getNickname();
			int sessionKey = Integer.parseInt(msg.getSessionKey());
			if(nicks.containsKey(nick) && registeredServersPorts.containsKey(nicks.get(nick)) && sessionKeys.containsKey(sessionKey)) {
				response = new DirMessage(DOWNLOADFROM_OK);

				response.setIp(registeredServersIPs.get(nicks.get(nick)).getHostAddress());
				response.setPort(registeredServersPorts.get(nicks.get(nick)).toString());
				System.out.println("Server Identified");
				
			}else {
				response = new DirMessage(DOWNLOADFROM_ERR);
				System.out.println("Server not found in registers");
			}
			break;
		}

		case DirMessageOps.OPERATION_PUBLISH: {
			int sessionKey = Integer.parseInt(msg.getSessionKey());



			if(registeredServersPorts.containsKey(sessionKey)) {
				publishedFiles.remove(sessionKey);
				String[] files = msg.getFiles().split(":");

				HashMap<String,String[]> hashesSet = new HashMap<String,String[]>();
				try{
					for(int i = 0; i < files.length; i++) {
						String[] currentFile = new String[2];
						currentFile[0] = files[i].split(",")[1];
						currentFile[1] = files[i].split(",")[2];

						hashesSet.put(files[i].split(",")[0], currentFile);
					}
					publishedFiles.put(sessionKey, hashesSet);
				} catch (ArrayIndexOutOfBoundsException e){

				}

				response = new DirMessage(PUBLISH_OK);
				System.out.println("Files published successfully");
			}else {
				response = new DirMessage(PUBLISH_ERR);
				System.out.println("ERROR: File publication failed. User not found");
			}
			break;
		}
		case DirMessageOps.OPERATION_SEARCH: {
			if (!sessionKeys.containsKey(Integer.parseInt(msg.getSessionKey()))){
				response = new DirMessage(SEARCH_ERR);
				System.out.println("ERROR: Search failed. User not found");
				break;
			}
			String hash = msg.getHash();
			String servers = "";
			for(Integer userKey : publishedFiles.keySet()) {
				if(publishedFiles.get(userKey).containsKey(hash)) {
					servers += sessionKeys.get(userKey) + ",";
				}

			}
			if(!servers.isEmpty()) {
				response = new DirMessage(SEARCH_OK);
				response.setServers(servers);
				System.out.println("Search successful");
			}else {
				response = new DirMessage(SEARCH_ERR);
				System.out.println("ERROR: Search failed. File not found");
			}
			break;
		}

			case DirMessageOps.OPERATION_FILELIST: {
				int sessionKey = Integer.parseInt(msg.getSessionKey());
				if(sessionKeys.containsKey(sessionKey)){
					response = new DirMessage(FILELIST_OK);
					StringBuilder files = new StringBuilder();
					Set<String> hashesInserted = new HashSet<>();

					HashMap<String, Set<Integer>> hashToServers = new HashMap<>();

					for (Map.Entry<Integer, HashMap<String, String[]>> server : publishedFiles.entrySet()) {
						for (Map.Entry<String, String[]> file : server.getValue().entrySet()) {
							String hash = file.getKey();
							hashToServers.computeIfAbsent(hash, k -> new HashSet<>()).add(server.getKey());

						}
					}
					for (Map.Entry<String, Set<Integer>> hashServers : hashToServers.entrySet()) {
						String hash = hashServers.getKey();
						if (!hashesInserted.contains(hash)) {
							HashMap<String, String[]> fileData = publishedFiles.get(hashServers.getValue().iterator().next());
							if (fileData != null && fileData.containsKey(hash)) {
								String fileName = fileData.get(hash)[0];
								String fileSize = fileData.get(hash)[1];
								files.append(hash).append(",").append(fileName).append(",").append(fileSize).append(",");
								hashesInserted.add(hash);
							}
						}
						Set<String> addedUsers = new HashSet<String>();
						for (Integer userKey : hashServers.getValue()) {
							String username = sessionKeys.get(userKey);
							if (!addedUsers.contains(username)) {
								addedUsers.add(username);
								files.append(username).append("/");
							}
						}
						files.deleteCharAt(files.length() - 1);
						files.append(":");
					}




					// Construir la cadena files


					response.setFiles(files.toString());
				}else {
					response = new DirMessage(FILELIST_ERR);
					System.out.println("ERROR: Filelist failed. User not found");
				}
				break;
			}


		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		return response;

	}
}
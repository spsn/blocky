package ge.demo.network;

import ge.demo.game.Blocky;

public class Server
{
	//TODO * accept players + create threads
	//TODO * serve world data
	//TODO * update player data
	//TODO - send player data - only when dirty ???

	public class ServerThread extends java.lang.Thread
	{
		private Server server;

		public ServerThread(
			final Server server)
		{
			//TODO
			this.server = server;
		}

		public void run()
		{
			//TODO
			server.handleClientConnections();
		}

	}

	public class ClientThread extends java.lang.Thread
	{
		private int id;
		private Server server;
		private java.net.Socket socket;
		private java.util.List<Message> messageList;

		public ClientThread(
			final Server server,
			final java.net.Socket socket)
		{
			//TODO
			id = hashCode();

			//TODO
			this.server = server;
			this.socket = socket;

			//TODO
			messageList = new java.util.LinkedList<Message>();
		}

		//TODO
		public void addMessage(
			final Message message)
		{

			synchronized (messageList)
			{
				//TODO
				messageList.add(message);
			}

		}

		//TODO
		public void run()
		{
			//TODO
			server.handleClientMessages(id, socket, messageList);
		}

	}

	public class Message
	{
		public byte id;
		public byte[] payload;
	}

	//TODO - ???
	private Blocky blocky;

	private java.net.ServerSocket serverSocket;
	private java.util.List<ClientThread> clientList;

	/**
	 * Constructor.
	 * @param blocky The game
	 * @param portNumber The port number
	 */
	public Server(
		final Blocky blocky,
		final int portNumber) throws java.lang.Exception
	{
		// Call super constructor
		super();

		// Local variables
		ServerThread serverThread;

		//TODO
		this.blocky = blocky;

		//TODO
		try
		{
			//TODO
			serverSocket = new java.net.ServerSocket(portNumber);

			System.out.println("Server: Server ready.");
		}
		catch (java.lang.Exception exception)
		{
			throw new java.lang.Exception("Failed to start server: " + exception.getMessage(), exception);
		}

		//TODO
		clientList = new java.util.ArrayList<ClientThread>();

		//TODO
		serverThread = new ServerThread(this);
		serverThread.setDaemon(true);
		serverThread.start();
	}

	//TODO
	protected void handleClientConnections()
	{
		// Local variables
		java.net.Socket clientSocket;
		ClientThread clientThread;

		//TODO
		while (true)
		{

			//TODO
			try
			{
				//TODO
				clientSocket = serverSocket.accept();
				clientSocket.setTcpNoDelay(true);
				clientSocket.setTrafficClass(0x10);

				//TODO
				clientThread = new ClientThread(this, clientSocket);
				clientThread.setDaemon(true);
				clientThread.start();

				//TODO - !!! client must be inactive until after reading world data to avoid corruption !!!
				addClient(clientThread);
			}
			catch (java.lang.Exception exception)
			{
				System.out.println("Failed to accept client connection: " + exception.getMessage());
				exception.printStackTrace();
			}

		}

	}

	//TODO
	protected void handleClientMessages(
		final int clientId,
		final java.net.Socket socket,
		final java.util.List<Message> messageList)
	{
		// Local variables
		java.io.BufferedOutputStream outputStream;
		java.io.BufferedInputStream inputStream;
		byte[] buffer;
		ByteArrayDataStream dataStream;
		byte messageId;
		byte[][][] space;
		int xs, ys, zs;
		Message message;

		//TODO
		try
		{
			//TODO
			outputStream = new java.io.BufferedOutputStream(socket.getOutputStream(), 1024);
			inputStream = new java.io.BufferedInputStream(socket.getInputStream(), 1024);

			//TODO
			buffer = new byte[Constants.MESSAGE_SIZE[Constants.MESSAGE_SIZE_PLAYER_ID]];

			//TODO
			dataStream = new ByteArrayDataStream(buffer);

			//TODO
			dataStream.writeInt(clientId);

			//TODO
			outputStream.write(buffer, 0, dataStream.size());

			//TODO
			outputStream.flush();

			System.out.println("Server: Client[" +  clientId + "] connected.");

			//TODO
			while (true)
			{

				//TODO
				try
				{
					//TODO - connection reset - client disconnected ???
					messageId = (byte) inputStream.read();
//System.out.println(clientId + "<--" + messageId);
				}
				catch (java.lang.Exception exception)
				{
					//TODO
					removeClient(clientId);

					//TODO
					buffer = new byte[Constants.MESSAGE_SIZE[Constants.MESSAGE_ID_PLAYER_REMOVE]];

					//TODO
					dataStream = new ByteArrayDataStream(buffer);

					//TODO
					dataStream.writeInt(clientId);

					//TODO
					message = new Message();
					message.id = Constants.MESSAGE_ID_PLAYER_REMOVE;
					message.payload = buffer;

					//TODO
					distributeClientMessage(clientId, message);

					System.out.println("Server: Client[" +  clientId + "] disconnected.");
					//TODO
					break;
				}

				//TODO
				if (messageId == Constants.MESSAGE_ID_WORLD_DATA)
				{
					//TODO
					space = blocky.getSpace();
					xs = space.length;
					ys = space[0].length;
					zs = space[0][0].length;

					//TODO
					outputStream.write(xs >> 4);
					outputStream.write(ys >> 4);
					outputStream.write(zs >> 4);

					for (int x = 0; x < xs; x++)
					{

						for (int y = 0; y < ys; y++)
						{
							outputStream.write(space[x][y]);
						}

					}

					outputStream.flush();
				}
				else if ((messageId == Constants.MESSAGE_ID_PLAYER_POSITION)
					|| (messageId == Constants.MESSAGE_ID_PLAYER_REMOVE)
					|| (messageId == Constants.MESSAGE_ID_CHANGED_BLOCK))
				{
					//TODO
					buffer = new byte[Constants.MESSAGE_SIZE[messageId]];

					//TODO
					readPacket(inputStream, buffer, buffer.length);

//System.out.println("pac=" + messagePayload.length);

					//TODO
					message = new Message();
					message.id = messageId;
					message.payload = buffer;

					//TODO
					distributeClientMessage(clientId, message);

					//TODO
					if (messageList.size() > 0)
					{
						//TODO
						if (messageList.size() > 5)
						{
							System.out.println("S:Messages[" + clientId + "] --> " + messageList.size());
						}

						//TODO - send out messages ???
						synchronized (messageList)
						{

							if (messageList.size() > 0)
							{
								message = messageList.remove(0);
							}
							else
							{
								message = null;
							}

						}

						while (message != null)
						{
//System.out.println(clientId + "-->" + message.id);
							//TODO
							outputStream.write(message.id);
							outputStream.write(message.payload);

							synchronized (messageList)
							{

								if (messageList.size() > 0)
								{
									message = messageList.remove(0);
								}
								else
								{
									message = null;
								}

							}

						}

						//TODO
//						outputStream.write(Constants.MESSAGE_ID_END_OF_MESSAGES);
//						outputStream.flush();
					}

					//TODO
					outputStream.write(Constants.MESSAGE_ID_END_OF_MESSAGES);
					outputStream.flush();
				}
				else
				{
					System.out.println("S: Invalid message: " + clientId + "<--" + messageId);
				}

			}

		}
		catch (java.lang.Exception exception)
		{
			System.out.println("Failed to process client message: " + exception.getMessage());
			exception.printStackTrace();
		}

	}

	//TODO
	private void addClient(
		final ClientThread client)
	{

		synchronized (clientList)
		{
			clientList.add(client);
		}

	}

	//TODO
	private void removeClient(
		final int clientId)
	{
		// Local variables
		ClientThread client;

		synchronized (clientList)
		{

			//TODO
			for (int i = 0; i < clientList.size(); i++)
			{
				client = clientList.get(i);

				//TODO
				if (client.id == clientId)
				{
					//TODO
					clientList.remove(client);

					break;
				}

			}

		}

	}

	//TODO
	private void distributeClientMessage(
		final int clientId,
		final Message message)
	{
		// Local variables
		ClientThread client;

		synchronized (clientList)
		{

			//TODO
			for (int i = 0; i < clientList.size(); i++)
			{
				client = clientList.get(i);

				//TODO
				if (client.id != clientId)
				{
	//System.out.println(clientId + "=>" + message.id + "=>" + client.id);
					//TODO
					client.addMessage(message);
				}

			}

		}

	}

	//TODO
	private void readPacket(
		final java.io.InputStream inputStream,
		final byte[] buffer,
		final int size) throws java.lang.Exception
	{
		// Local variables
		int index;

		//TODO
		index = 0;

		while (index < size)
		{
			buffer[index] = (byte) inputStream.read();

			index++;
		}

	}

}

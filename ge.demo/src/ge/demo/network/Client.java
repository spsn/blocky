package ge.demo.network;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import ge.demo.actor.Player;
import ge.demo.game.Blocky;
import ge.framework.overlay.ProgressBarOverlay;
import ge.framework.render.Renderer;

//TODO - messages
//1 - get world data
//2 - add/update player data
//3 - remove player data
//4 - add/update actor data
//5 - remove actor data
//6 - send/receive block change
public class Client
{
	//TODO * connect to server
	//TODO - get world data
	//TODO - get player data
	//TODO - track local player data - send to server
	//TODO - get remote player data from server - update players

	private int id;
	private Blocky blocky;
	private java.net.Socket socket;
	private java.io.BufferedOutputStream outputStream;
	private java.io.BufferedInputStream inputStream;
	private byte[] buffer;
	private ByteArrayDataStream dataStream;
	private boolean connected;

	/**
	 * Constructor.
	 * @param blocky The game
	 * @param portNumber The port number
	 */
	public Client(
		final Blocky blocky,
		final String hostName,
		final int portNumber) throws java.lang.Exception
	{
		// Call super constructor
		super();

		//TODO
		this.blocky = blocky;

		//TODO
		try
		{
			//TODO
			socket = new java.net.Socket(hostName, portNumber);
			socket.setTcpNoDelay(true);
			socket.setTrafficClass(0x10);
			outputStream = new java.io.BufferedOutputStream(socket.getOutputStream(), 1024);
			inputStream = new java.io.BufferedInputStream(socket.getInputStream(), 1024);

			//TODO
			buffer = new byte[1024];

			//TODO
			dataStream = new ByteArrayDataStream(buffer);

			//TODO
			readPacket(inputStream, buffer, Constants.MESSAGE_SIZE[Constants.MESSAGE_SIZE_PLAYER_ID]);

			//TODO
			dataStream.reset();
			id = dataStream.readInt();

			//TODO
			connected = true;

			System.out.println("Client[" + id + "]: Connected to server.");
		}
		catch (java.lang.Exception exception)
		{
			throw new java.lang.Exception("Failed to connect to server: " + exception.getMessage(), exception);
		}

	}

	//TODO
	public byte[][][] getWorldData(
		final Renderer renderer,
		final ProgressBarOverlay s2) throws java.lang.Exception
	{
		// Local variables
		int xs, ys, zs;
		byte[][][] space;

		try
		{
			//TODO
			outputStream.write(Constants.MESSAGE_ID_WORLD_DATA);
			outputStream.flush();

			//TODO
			xs = ((byte) inputStream.read() << 4);
			ys = ((byte) inputStream.read() << 4);
			zs = ((byte) inputStream.read() << 4);

//			System.out.println("x = " + xs + ", y = " + ys + ", z = " + zs);

			space = new byte[xs][ys][zs];

			for (int x = 0; x < xs; x++)
			{

				for (int y = 0; y < ys; y++)
				{
					readPacket(inputStream, space[x][y], zs);
				}

				if ((x & 7) == 0)
				{
					s2.setValue((x * 100) / xs, 100);
					renderer.renderOverlays();
				}

			}

		}
		catch (java.lang.Exception exception)
		{
			throw new java.lang.Exception("Failed to get world data from server: " + exception.getMessage(), exception);
		}

		return space;
	}

	public void sendPlayerData(
		final Player player,
		final java.util.List<Vector4f> changedBlockList) throws java.lang.Exception
	{
		// Local variables
		Vector3f position;
		Vector3f rotation;
		Vector4f block;
		byte messageId;

		try
		{
			//TODO
			dataStream.reset();
			dataStream.writeByte(Constants.MESSAGE_ID_PLAYER_POSITION);
			dataStream.writeInt(id);

			//TODO
			position = player.getPosition();
			dataStream.writeFloat(position.x);
			dataStream.writeFloat(position.y);
			dataStream.writeFloat(position.z);

			//TODO
			rotation = player.getRotation();
			dataStream.writeFloat(rotation.x);
			dataStream.writeFloat(rotation.y);
			dataStream.writeFloat(rotation.z);

			//TODO - server disconnected
			//TODO
			outputStream.write(buffer, 0, dataStream.size());
//System.out.println("stream[2]-->" + dataStream.size());

			//TODO
			if (changedBlockList.size() > 0)
			{
//System.out.println("block changed");
				//TODO
				dataStream.reset();

				while (changedBlockList.size() > 0)
				{
					//TODO
					block = changedBlockList.remove(0);

					//TODO
					dataStream.writeByte(Constants.MESSAGE_ID_CHANGED_BLOCK);
					dataStream.writeInt(id);
					dataStream.writeFloat(block.x);
					dataStream.writeFloat(block.y);
					dataStream.writeFloat(block.z);
					dataStream.writeByte((byte) block.w);
				}

				//TODO
				outputStream.write(buffer, 0, dataStream.size());
//if (dataStream.size() > 0)
//System.out.println("stream[6]-->" + dataStream.size());
			}

			//TODO
			outputStream.flush();
//System.out.println(player.getId() + "-->" + 2);

			//TODO
//			if (inputStream.available() > 0)
			{
				//TODO - connection reset - server disconnected ???
				messageId = (byte) inputStream.read();
//System.out.println(player.getId() + "[c]<--" + messageId);

				//TODO
				while (messageId > Constants.MESSAGE_ID_END_OF_MESSAGES)
				{

					//TODO
					if (messageId == Constants.MESSAGE_ID_PLAYER_POSITION)
					{
						//TODO
						readPacket(inputStream, buffer, Constants.MESSAGE_SIZE[Constants.MESSAGE_ID_PLAYER_POSITION]);

						//TODO
						dataStream.reset();
						blocky.updatePlayerData(dataStream.readInt(),
							new Vector3f(dataStream.readFloat(), dataStream.readFloat(), dataStream.readFloat()),
							new Vector3f(dataStream.readFloat(), dataStream.readFloat(), dataStream.readFloat()));
					}
					else if (messageId == Constants.MESSAGE_ID_PLAYER_REMOVE)
					{
						//TODO
						readPacket(inputStream, buffer, Constants.MESSAGE_SIZE[Constants.MESSAGE_ID_PLAYER_REMOVE]);

						//TODO
						dataStream.reset();
						blocky.removePlayer(dataStream.readInt());
					}
					else if (messageId == Constants.MESSAGE_ID_CHANGED_BLOCK)
					{
						//TODO
						readPacket(inputStream, buffer, Constants.MESSAGE_SIZE[Constants.MESSAGE_ID_CHANGED_BLOCK]);

						//TODO
						dataStream.reset();
						dataStream.readInt();
						blocky.updateBlock(new Vector3f(dataStream.readFloat(), dataStream.readFloat(), dataStream.readFloat()), dataStream.readByte());
					}
					else
					{
						System.out.println("C: Invalid message: " + player.getId() + "<--" + messageId);
					}

					//TODO
//					if (inputStream.available() == 0)
//					{
//						break;
//					}

					//TODO - connection reset - server disconnected ???
					messageId = (byte) inputStream.read();
				}

			}

		}
		catch (java.lang.Exception exception)
		{
			//TODO
			connected = false;

			throw new java.lang.Exception("Failed to send player data to server: " + exception.getMessage(), exception);
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

	public boolean isConnected()
	{
		return connected;
	}

}

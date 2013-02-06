package ge.demo.network;

//TODO
public class ByteArrayDataStream
{
	private int position;
	private byte[] buffer;

	public ByteArrayDataStream(
		final byte[] buffer)
	{
		// Call super constructor
		super();

		//TODO
		this.buffer = buffer;
	}

	public int size()
	{
		//TODO
		return position;
	}

	public void reset()
	{
		//TODO
		position = 0;
	}

	public void writeByte(
		final byte value)
	{
		buffer[position++] = value;
	}

	public byte readByte()
	{
		return buffer[position++];
	}

	public void writeInt(
		final int value)
	{
		buffer[position++] = (byte)((value >> 24) & 0xff);
		buffer[position++] = (byte)((value >> 16) & 0xff);
		buffer[position++] = (byte)((value >> 8) & 0xff);
		buffer[position++] = (byte)((value >> 0) & 0xff);
	}

	public int readInt()
	{
		return (((buffer[position++] & 0xff) << 24) | ((buffer[position++] & 0xff) << 16)
			| ((buffer[position++] & 0xff) << 8 | ((buffer[position++] & 0xff) << 0)));
	}

	public void writeFloat(
		final float value)
	{
		int value1 = Float.floatToIntBits(value);

		buffer[position++] = (byte)((value1 >> 24) & 0xff);
		buffer[position++] = (byte)((value1 >> 16) & 0xff);
		buffer[position++] = (byte)((value1 >> 8) & 0xff);
		buffer[position++] = (byte)((value1 >> 0) & 0xff);
	}

	public float readFloat()
	{
		return Float.intBitsToFloat((((buffer[position++] & 0xff) << 24) | ((buffer[position++] & 0xff) << 16)
			| ((buffer[position++] & 0xff) << 8 | ((buffer[position++] & 0xff) << 0))));
	}

}

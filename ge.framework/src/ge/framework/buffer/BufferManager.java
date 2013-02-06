package ge.framework.buffer;

/**
 * Buffer manager.
 * @author GEF
 */
public class BufferManager
{
	// Buffer list
	private java.util.List<Buffer> bufferList;

	/**
	 * Constructor.
	 */
	public BufferManager()
	{
		// Call super constructor
		super();

		// Create buffer list
		bufferList = new java.util.ArrayList<Buffer>();
	}

	/**
	 * Allocate buffer.
	 * @param type The buffer type
	 * @param capacity The buffer capacity
	 * @return The allocated buffer
	 * @throws java.lang.Exception when unable to allocate a buffer
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Buffer getBuffer(
		final Class type,
		final int capacity) throws java.lang.Exception
	{
		// Local variables
		boolean found;
		java.util.Iterator<Buffer> iterator;
		Buffer buffer;

		synchronized (bufferList)
		{
			// Find free buffer in buffer list
			found = false;
			buffer = null;

			for (iterator = bufferList.listIterator(); iterator.hasNext() == true;)
			{
				buffer = iterator.next();

				// Buffer found?
				if ((buffer.isInUse() == false)
					&& (buffer.getClass() == type))
				{
					found = true;
					break;
				}

			}

			// Free buffer not found?
			if (found == false)
			{
				// Create new buffer
				buffer = (Buffer) type.getConstructor(int.class).newInstance(capacity);

				// Add buffer to buffer list
				bufferList.add(buffer);
			}

			// Allocate buffer
			buffer.setInUse(true);
		}

		return buffer;
	}

	/**
	 * Release buffer.
	 * @param buffer The buffer
	 */
	public void releaseBuffer(
		final Buffer buffer)
	{

		synchronized (bufferList)
		{
			// Release buffer
			buffer.setInUse(false);
		}

	}

}

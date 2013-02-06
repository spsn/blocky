package ge.framework.buffer;

/**
 * Represents a Short buffer.
 */
public class ShortBuffer extends Buffer
{
	// Capacity
	private int capacity;

	// Size
	private int size;

	// Content
	private short[] content;

	/**
	 * Constructor.
	 * @param capacity The capacity
	 */
	public ShortBuffer(
		final int capacity)
	{
		// Call super constructor
		super();

		// Set capacity
		this.capacity = capacity;

		// Set size
		size = 0; 

		// Create content
		content = new short[capacity];
	}

	/**
	 * Constructor.
	 * @param floatBuffer The int buffer
	 */
	public ShortBuffer(
		final ShortBuffer shortBuffer)
	{
		// Call super constructor
		super();

		// Set capacity
		capacity = shortBuffer.getSize();

		// Set size
		size = capacity; 

		// Create content
		content = new short[size];
		System.arraycopy(shortBuffer.content, 0, content, 0, size);
	}

	/**
	 * Add value.
	 * @param value The value
	 */
	public void add(
		final short value)
	{
		// Add value
		content[size] = value;

		// Increment size
		size++;
	}

	/**
	 * Clear buffer.
	 */
	public void clear()
	{
		// Reset size
		size = 0; 
	}

	/**
	 * Get capacity.
	 * @return The capacity
	 */
	public int getCapacity()
	{
		return capacity;
	}

	/**
	 * Get size.
	 * @return The size
	 */
	public int getSize()
	{
		return size;
	}

	/**
	 * Get content.
	 * @return The content
	 */
	public short[] getContent()
	{
		return content;
	}

}

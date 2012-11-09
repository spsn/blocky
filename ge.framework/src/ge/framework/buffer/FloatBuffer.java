package ge.framework.buffer;

/**
 * Float buffer.
 * @author GEF
 */
public class FloatBuffer
{
	// Capacity
	private int capacity;

	// Size
	private int size;

	// Content
	private float[] content;

	/**
	 * Constructor.
	 * @param capacity The capacity
	 */
	public FloatBuffer(
		final int capacity)
	{
		// Call super constructor
		super();

		// Set capacity
		this.capacity = capacity;

		// Set size
		size = 0; 

		// Create content
		content = new float[capacity];
	}

	/**
	 * Constructor.
	 * @param floatBuffer The float buffer
	 */
	public FloatBuffer(
		final FloatBuffer floatBuffer)
	{
		// Call super constructor
		super();

		// Set capacity
		capacity = floatBuffer.getSize();

		// Set size
		size = capacity; 

		// Create content
		content = new float[size];
		System.arraycopy(floatBuffer.content, 0, content, 0, size);
	}

	/**
	 * Add value.
	 * @param value The value
	 */
	public void add(
		final float value)
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
	public float[] getContent()
	{
		return content;
	}

}

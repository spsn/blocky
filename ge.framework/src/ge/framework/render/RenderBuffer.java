package ge.framework.render;

/**
 * Represents a render buffer.
 */
public abstract class RenderBuffer
{
	// Vertex buffer identifier
	private int vertexBufferId;

	// Index buffer identifier
	private int indexBufferId;

	/**
	 * Constructor.
	 */
	public RenderBuffer()
	{
		// Call super constructor
		super();
	}

	/**
	 * Get vertex buffer identifier.
	 * @return The vertex buffer identifier
	 */
	public int getVertexBufferId()
	{
		return vertexBufferId;
	}

	/**
	 * Set vertex buffer identifier.
	 * @param vertexBufferId The vertex buffer identifier
	 */
	public void setVertexBufferId(
		final int vertexBufferId)
	{
		// Set vertex buffer identifier
		this.vertexBufferId = vertexBufferId;
	}

	/**
	 * Get index buffer identifier.
	 * @return The index buffer identifier
	 */
	public int getIndexBufferId()
	{
		return indexBufferId;
	}

	/**
	 * Set index buffer identifier.
	 * @param indexBufferId The index buffer identifier
	 */
	public void setIndexBufferId(
		final int indexBufferId)
	{
		// Set index buffer identifier
		this.indexBufferId = indexBufferId;
	}

}

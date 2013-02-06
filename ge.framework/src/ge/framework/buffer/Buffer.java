package ge.framework.buffer;

/**
 * Buffer.
 * @author GEF
 */
public abstract class Buffer
{
	// In use indicator
	private boolean inUse;

	/**
	 * Constructor.
	 */
	public Buffer()
	{
		// Call super constructor
		super();

		// Not in use
		inUse = false;
	}

	/**
	 * Get in use indicator.
	 * @return The in use indicator
	 */
	public boolean isInUse()
	{
		return inUse;
	}

	/**
	 * Set in use indicator.
	 * @param inUse The in use indicator
	 */
	public void setInUse(
		final boolean inUse)
	{
		// Set in use indicator
		this.inUse = inUse;
	}

}

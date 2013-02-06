package ge.framework.util;

/**
 * Implements a semaphore construct.  A semaphore may be initialized with an initial post count
 * and a maximum post count.  The default value for the initial post count is 0.  The default
 * value for the maximum post count is Integer.MAX_VALUE.  When a thread waits on the semaphore
 * while the post count is greater than 0, the post count is decremented and the thread does not
 * actually wait.  When the post count is 0, the thread waits on the semaphore object until another
 * thread posts to the semaphore.  The semaphore ensures that posts to the semaphore do not get lost
 * when threads are not actively waiting on the semaphore.
 */
public class Semaphore
{
	// Count
	private int count;

	// Maximum count
	private int maximumCount;

	/**
	 * Constructor.  Sets initial post count to 0 and
	 * maximum post count for semaphore to Integer.MAX_VALUE.
	 */
	public Semaphore()
	{
		this(0, Integer.MAX_VALUE);
	}

	/**
	 * Constructor.  Sets initial post count
	 * and maximum post count for semaphore.
	 * @param initialCount The initial post count
	 * @param maximumCount The maximum post count
	 */
	public Semaphore(
		final int initialCount,
		final int maximumCount)
	{
		// Call super constructor
		super();

		// Set initial count
		this.count = initialCount;

		// Set maximum count
		this.maximumCount = maximumCount;
	}

	/**
	 * Get maximum count.
	 * @return The maximum count
	 */
	public int getMaximumCount()
	{
		return maximumCount;
	}

	/**
	 * Set maximum count.
	 * @param maximumCount The maximum count
	 */
	public void setMaximumCount(int maximumCount)
	{
		// Set maximum count
		this.maximumCount = maximumCount;
	}

	/**
	 * Wait on semaphore indefinitely.  Decrements post count by 1.
	 * @throws java.lang.InterruptedException when the thread is interrupted
	 */
	public final void waitFor() throws java.lang.InterruptedException
	{
		waitFor(0);
	}

	/**
	 * Wait on semaphore indefinitely.  Sets post count to 0.
	 * @throws java.lang.InterruptedException when the thread is interrupted
	 */
	public final void waitForAndPurge() throws java.lang.InterruptedException
	{
		waitForAndPurge(0);
	}

	/**
	 * Wait on semaphore for specified number of milliseconds. Decrements post count by 1.
	 * @param timeOut The time to wait in milliseconds
	 * @throws java.lang.InterruptedException when the thread is interrupted
	 */
	public final void waitFor(
		final long timeOut) throws java.lang.InterruptedException
	{

		synchronized (this)
		{

			// Wait for notify when post count is 0
			if (count == 0)
			{

				try
				{
					// Wait for notify
					wait(timeOut);
				}
				catch (java.lang.InterruptedException exception)
				{
					Thread.currentThread().interrupt();
					throw exception;
				}

			}

			// Decrement post count
			if (count > 0)
			{
				count--;
			}

		}

	}

	/**
	 * Wait on semaphore for specified number of milliseconds. Sets post count to 0.
	 * @param timeOut The time to wait in milliseconds
	 * @throws java.lang.InterruptedException when the thread is interrupted
	 */
	public final void waitForAndPurge(
		final long timeOut) throws java.lang.InterruptedException
	{

		synchronized (this)
		{

			// Wait for notify when post count is 0
			if (count == 0)
			{

				try
				{
					// Wait for notify
					wait(timeOut);
				}
				catch (java.lang.InterruptedException exception)
				{
					Thread.currentThread().interrupt();
					throw exception;
				}

			}

			// Set post count to 0
			if (count > 0)
			{
				count = 0;
			}

		}

	}

	/**
	 * Post count of 1 to semaphore.
	 * Count will not increase past maximum value set for semaphore.
	 */
	public final void post()
	{
		// Post count of 1
		post(1);
	}

	/**
	 * Post specified count to semaphore.
	 * Count will not increase past maximum value set for semaphore.
	 * @param count1 The count to post
	 */
	public final void post(
		final int count1)
	{
		// Local variables
		int actualCount;

		synchronized (this)
		{

 			// Calculate actual count.  Post count may not pass maximum
			if ((count + count1) <= maximumCount)
			{
				actualCount = count1;
			}
			else
			{
				actualCount = maximumCount - count;
			}

			// Increment post count
			count += actualCount;

			// Notify waiting threads
			for (int i = 0; i < actualCount; i++)
			{
				notify();
			}

		}

	}

}

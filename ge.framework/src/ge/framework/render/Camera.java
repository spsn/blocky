package ge.framework.render;

/**
 * Represents a camera.
 */
public class Camera
{
	// Field of view
	private float fieldOfView;

	// Viewing distance
	private float viewingDistance;

	// Position X component
	private float positionX;

	// Position Y component
	private float positionY;

	// Position Z component
	private float positionZ;

	// Pitch
	private float pitch;

	// Yaw
	private float yaw;

	// Roll
	private float roll;

	// View changed?
	private boolean viewChanged;

	/**
	 * Constructor.
	 */
	public Camera()
	{
		// Call super constructor
		super();

		//TODO
		viewChanged = false;
	}

	public float getFieldOfView()
	{
		return fieldOfView;
	}

	public void setFieldOfView(
		final float fieldOfView)
	{
		this.fieldOfView = fieldOfView;

		viewChanged = true;
	}

	public float getViewingDistance()
	{
		return viewingDistance;
	}

	public void setViewingDistance(
		final float viewingDistance)
	{
		this.viewingDistance = viewingDistance;

		viewChanged = true;
	}

	public float getPositionX()
	{
		return positionX;
	}

	public void setPositionX(
		final float positionX)
	{
		this.positionX = positionX;
	}

	public void updatePositionX(
		final float positionXDelta)
	{
		this.positionX += positionXDelta;
	}

	public float getPositionY()
	{
		return positionY;
	}

	public void setPositionY(
		final float positionY)
	{
		this.positionY = positionY;
	}

	public void updatePositionY(
		final float positionYDelta)
	{
		this.positionY += positionYDelta;
	}

	public float getPositionZ()
	{
		return positionZ;
	}

	public void setPositionZ(
		final float positionZ)
	{
		this.positionZ = positionZ;
	}

	public void updatePositionZ(
		final float positionZDelta)
	{
		this.positionZ += positionZDelta;
	}

	public float getPitch()
	{
		return pitch;
	}

	public void setPitch(
		final float pitch)
	{
		this.pitch = pitch;
	}

	public void updatePitch(
		final float pitchDelta)
	{
		this.pitch += pitchDelta;
	}

	public float getYaw()
	{
		return yaw;
	}

	public void setYaw(
		final float yaw)
	{
		this.yaw = yaw;
	}

	public void updateYaw(
		final float yawDelta)
	{
		this.yaw += yawDelta;
	}

	public float getRoll()
	{
		return roll;
	}

	public void setRoll(
		final float roll)
	{
		this.roll = roll;
	}

	public void updateRoll(
		final float rollDelta)
	{
		this.roll += rollDelta;
	}

	public boolean hasViewChanged()
	{
		// Local variables
		boolean viewChanged1;

		//TODO
		viewChanged1 = viewChanged;

		//TODO
		viewChanged = false;

		return viewChanged1;
	}

}

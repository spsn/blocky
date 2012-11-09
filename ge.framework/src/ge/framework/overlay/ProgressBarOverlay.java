package ge.framework.overlay;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import org.newdawn.slick.opengl.Texture;

/**
 * Represents a counter overlay on the screen.
 */
public class ProgressBarOverlay implements Overlay
{
	private Mesh mesh;
	private int percentage;

	/**
	 * Constructor.
	 */
	public ProgressBarOverlay(
		final float positionX,
		final float positionY,
		final float width,
		final float height,
		final Texture texture)
	{
		// Call super constructor
		super();

		//TODO
		float hwidth = width / 2;
		float hheight = height / 2;
		float pwidth = width / 100f;

		//TODO - make mesh - z = 0.1f
		mesh = new Mesh(Mesh.MeshType.OVERLAY, false, new FloatBuffer(4 * 12 * 101), new ShortBuffer(4 * 101));

		for (int i = 0; i < 101; i++)
		{
			// Top right
			mesh.addVertex(
					(positionX - hwidth + (pwidth * i)), (positionY + hheight), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					clamp(0.0f + 0.001f + (i / 100f), (1.0f - 0.001f)), 0.0f + 0.001f);
			// Top left
			mesh.addVertex(
					(positionX - hwidth), (positionY + hheight), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					0.0f + 0.001f, 0.0f + 0.001f);
			// Bottom left
			mesh.addVertex(
					(positionX - hwidth), (positionY - hheight), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					0.0f + 0.001f, 1.0f - 0.001f);
			// Bottom right
			mesh.addVertex(
					(positionX - hwidth + (pwidth * i)), (positionY - hheight), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					clamp(0.0f + 0.001f + (i / 100f), (1.0f - 0.001f)), 1.0f - 0.001f);
		}

		//TODO
		mesh.setTexture(texture);
	}

	//TODO
	private float clamp(
		final float value,
		final float max)
	{
		return (value > max) ? max : value;
	}

	//TODO
	public void setValue(
		final int value,
		final int max)
	{
		percentage = (value * 100) / max;
		mesh.setIndexOffset((short) (percentage * 12));
		mesh.setIndexCount((short) 6);
	}

	//TODO
	public int getPercentage() {
		return percentage;
	}

	/**
	 * Render overlay to screen.
	 */
	public void render()
	{
	}

	public Mesh getMesh()
	{
		return mesh;
	}

}

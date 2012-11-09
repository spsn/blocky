package ge.framework.overlay;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import org.newdawn.slick.opengl.Texture;

/**
 * Represents a sprite overlay on the screen.
 */
public class SpriteOverlay implements Overlay
{
	private Mesh mesh;

	/**
	 * Constructor.
	 */
	public SpriteOverlay(
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

		//TODO - make mesh - z = 0.1f
		mesh = new Mesh(Mesh.MeshType.OVERLAY, false, new FloatBuffer(4 * 12), new ShortBuffer(4));

		// Top right
		mesh.addVertex(
				(positionX + hwidth), (positionY + hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f - 0.001f, 0.0f + 0.001f);
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
				(positionX + hwidth), (positionY - hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f - 0.001f, 1.0f - 0.001f);

		//TODO
		mesh.setTexture(texture);
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

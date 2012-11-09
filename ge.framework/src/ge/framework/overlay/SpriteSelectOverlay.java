package ge.framework.overlay;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import org.newdawn.slick.opengl.Texture;

/**
 * Represents a sprite select overlay on the screen.
 */
public class SpriteSelectOverlay implements Overlay
{
	private float positionX;
	private float positionY;
	private float hwidth;
	private float hheight;

	private Mesh mesh;

	private short count;
	private short index;

	/**
	 * Constructor.
	 */
	public SpriteSelectOverlay(
		final float positionX,
		final float positionY,
		final float width,
		final float height,
		final Texture texture)
	{
		// Call super constructor
		super();

		//TODO
		this.positionX = positionX;
		this.positionY = positionY;

		//TODO
		hwidth = width / 2;
		hheight = height / 2;

		//TODO - make mesh - z = 0.1f
		mesh = new Mesh(Mesh.MeshType.OVERLAY, false, new FloatBuffer(4 * 12 * 100), new ShortBuffer(4 * 100));

		//TODO
		mesh.setTexture(texture);
	}

	//TODO
	public void addSprite(
		final float textureX1,
		final float textureX2,
		final float textureY1,
		final float textureY2)
	{
		// Top right
		mesh.addVertex(
				(positionX + hwidth), (positionY + hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				textureX2 - 0.001f, textureY1 + 0.001f);
		// Top left
		mesh.addVertex(
				(positionX - hwidth), (positionY + hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				textureX1 + 0.001f, textureY1 + 0.001f);
		// Bottom left
		mesh.addVertex(
				(positionX - hwidth), (positionY - hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				textureX1 + 0.001f, textureY2 - 0.001f);
		// Bottom right
		mesh.addVertex(
				(positionX + hwidth), (positionY - hheight), 0.0f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				textureX2 - 0.001f, textureY2 - 0.001f);

		//TODO
		count++;
	}

	//TODO
	public void start()
	{
		//TODO
		mesh.setIndexCount((short) 6);
	}

	//TODO
	public void shiftUp()
	{
		index += 1;

		index = (index >= count) ? 0 : index;

		mesh.setIndexOffset((short) (index * 12));
	}

	//TODO
	public void shiftDown()
	{
		index -= 1;

		index = (index < 0) ? (short) (count - 1) : index;

		mesh.setIndexOffset((short) (index * 12));
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

	public short getIndex()
	{
		return index;
	}

}

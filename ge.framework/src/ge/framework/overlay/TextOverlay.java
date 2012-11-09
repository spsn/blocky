package ge.framework.overlay;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.BufferedImageUtil;

/**
 * Represents a text overlay on the screen.
 */
public class TextOverlay implements Overlay
{
	private int positionX;
	private int positionY;
	private int width;
	private int height;
	private BufferedImage bufferedImage;
	private Graphics2D graphics;
	private Mesh mesh;
	private Texture texture;

	/**
	 * Constructor.
	 */
	public TextOverlay(
		final int positionX,
		final int positionY,
		final int width,
		final int height,
		final Font font)
	{
		// Call super constructor
		super();

		//TODO
		this.positionX = positionX;
		this.positionY = positionY;
		this.width = width;
		this.height = height;

		//TODO
		bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		graphics = bufferedImage.createGraphics();
		graphics.setFont(font);
		graphics.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));

		//TODO - make mesh - z = 0.1f
		mesh = new Mesh(Mesh.MeshType.OVERLAY, false, new FloatBuffer(4 * 12), new ShortBuffer(4));
		// Top right
		mesh.addVertex(
				0.10f, 0.95f, -0.2f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f);
		// Top left
		mesh.addVertex(
				0.0f, 0.95f, -0.2f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				0.0f, 1.0f);
		// Bottom left
		mesh.addVertex(
				0.0f, 0.90f, -0.2f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				0.0f, 0.0f);
		// Bottom right
		mesh.addVertex(
				0.10f, 0.90f, -0.2f,
				0.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 0.0f);
	}

	/**
	 * Set text for overlay.
	 * @param text The text
	 */
	public void setText(
		final String text) throws java.lang.Exception
	{
		//TODO
		graphics.scale(1, 1);
		graphics.clearRect(0, 0, width, height);
		graphics.scale(1, -1);
		graphics.setColor(Color.WHITE);
		graphics.drawString(text, 0, -20);

		if (texture != null)
		{
			texture.release();
		}

		texture = BufferedImageUtil.getTexture(String.valueOf(this.hashCode()), bufferedImage);

		//TODO - set texture on mesh
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

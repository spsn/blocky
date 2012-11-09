package ge.framework.overlay;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import org.newdawn.slick.opengl.Texture;

/**
 * Represents a counter overlay on the screen.
 */
public class CounterOverlay implements Overlay
{
	private Mesh mesh;

	/**
	 * Constructor.
	 */
	public CounterOverlay(
		final float positionX,
		final float positionY,
		final float width,
		final float height,
		final Texture texture)
	{
		// Call super constructor
		super();

		//TODO
		float digitWidth = width / 3;
		float digitRatio = 1 / 12.8f;

		//TODO - make mesh - z = 0.1f
		mesh = new Mesh(Mesh.MeshType.OVERLAY, false, new FloatBuffer(4 * 12 * 30), new ShortBuffer(4 * 30));

		for (int i = 0; i < 10; i++)
		{
			int digit1 = i;

			// Digit #1
			// Top right
			mesh.addVertex(
					(positionX + digitWidth), positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit1 + 1) - 0.001f, 0.0f + 0.001f);
			// Top left
			mesh.addVertex(
					positionX, positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit1 + 0.001f, 0.0f + 0.001f);
			// Bottom left
			mesh.addVertex(
					positionX, (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit1 + 0.001f, 1.0f - 0.001f);
			// Bottom right
			mesh.addVertex(
					(positionX + digitWidth), (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit1 + 1) - 0.001f, 1.0f - 0.001f);
		}

		for (int i = 0; i < 10; i++)
		{
			int digit2 = i;

			// Digit #2
			// Top right
			mesh.addVertex(
					(positionX + digitWidth + digitWidth), positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit2 + 1), 0.0f);
			// Top left
			mesh.addVertex(
					(positionX + digitWidth), positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit2, 0.0f);
			// Bottom left
			mesh.addVertex(
					(positionX + digitWidth), (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit2, 1.0f);
			// Bottom right
			mesh.addVertex(
					(positionX + digitWidth + digitWidth), (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit2 + 1), 1.0f);
		}

		for (int i = 0; i < 10; i++)
		{
			int digit3 = i;

			// Digit #3
			// Top right
			mesh.addVertex(
					(positionX + digitWidth + digitWidth + digitWidth), positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit3 + 1), 0.0f);
			// Top left
			mesh.addVertex(
					(positionX + digitWidth + digitWidth), positionY, 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit3, 0.0f);
			// Bottom left
			mesh.addVertex(
					(positionX + digitWidth + digitWidth), (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * digit3, 1.0f);
			// Bottom right
			mesh.addVertex(
					(positionX + digitWidth + digitWidth + digitWidth), (positionY - height), 0.0f,
					0.0f, 0.0f, 1.0f,
					1.0f, 1.0f, 1.0f, 1.0f,
					digitRatio * (digit3 + 1), 1.0f);
		}

		//TODO
		mesh.setTexture(texture);
	}

	//TODO
	public void setValue(
		final int value)
	{
		int value1 = (value < 0) ? 0 : value;

		//(180 * pos) + (18 * num)
		short val1 = (short) (value1 / 100);
		short val2 = (short) ((value1 % 100) / 10);
		short val3 = (short) ((value1 % 100) % 10);
		short digit1 = (short) (val1 * 12);
		short digit2 = (short) ((val2 * 12) + 120);
		short digit3 = (short) ((val3 * 12) + 240);

		mesh.setIndexOffsets(new short[] {digit1, digit2, digit3});
		mesh.setIndexCount((short) 6);
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

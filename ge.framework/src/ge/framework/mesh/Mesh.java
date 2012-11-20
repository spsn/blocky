package ge.framework.mesh;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;

import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.render.RenderBuffer;

/**
 * Represents a mesh to be rendered.
 * Includes a vertex buffer and an index buffer.
 * Maintains a texture, position, rotation and bounding box for the mesh.
 */
public class Mesh extends RenderBuffer
{
	// Mesh types
	public enum MeshType {OPAQUE, TRANSPARENT, MODEL, OVERLAY};

	// Mesh type
	private MeshType meshType;

	// Deferred rendering indicator
	private boolean deferred;

	// Vertex count
	private short vertexCount;

	// Index count
	private short indexCount;

	// Index offset
	private short indexOffset;

	// Index offsets
	private short[] indexOffsets;

	// Vertex buffer
	private FloatBuffer vertexBuffer;

	// Index buffer
	private ShortBuffer indexBuffer;

	// Texture
	private Texture texture;

	// Position
	private Vector3f position;

	// Rotation
	private Vector3f rotation;

	// Bounding box
	private BoundingBox boundingBox;

	/**
	 * Constructor.
	 * @param meshType The mesh type
	 * @param deferred The deferred rendering indicator
	 * @param vertexBuffer The vertex buffer
	 * @param indexBuffer The index buffer
	 */
	public Mesh(
		final MeshType meshType,
		final boolean deferred,
		final FloatBuffer vertexBuffer,
		final ShortBuffer indexBuffer)
	{
		// Call super constructor
		super();

		// Set mesh type
		this.meshType = meshType;

		// Set deferred rendering indicator
		this.deferred = deferred;

		// Set vertex buffer
		this.vertexBuffer = vertexBuffer;

		// Set index buffer
		this.indexBuffer = indexBuffer;

		// Create bounding box
		boundingBox = new BoundingBox();
	}

	/**
	 * Get mesh type.
	 * @return The mesh type
	 */
	public MeshType getMeshType()
	{
		return meshType;
	}

	/**
	 * Get deferred rendering indicator.
	 * @return true if rendering is deferred, false otherwise
	 */
	public boolean isDeferred()
	{
		return deferred;
	}

	/**
	 * Get vertex count.
	 * @return The vertex count
	 */
	public short getVertexCount()
	{
		return vertexCount;
	}

	/**
	 * Get index count.
	 * @return The index count
	 */
	public short getIndexCount()
	{
		return indexCount;
	}

	/**
	 * Get index offset.
	 * @return The index offset
	 */
	public short getIndexOffset()
	{
		return indexOffset;
	}

	/**
	 * Set index offset.
	 * @param indexOffset The index offset
	 */
	public void setIndexOffset(
		final short indexOffset)
	{
		this.indexOffset = indexOffset;
	}

	/**
	 * Get index offsets.
	 * @return The index offsets
	 */
	public short[] getIndexOffsets()
	{
		return indexOffsets;
	}

	/**
	 * Set index offsets.
	 * @param indexOffsets The index offsets
	 */
	public void setIndexOffsets(
		final short[] indexOffsets)
	{
		this.indexOffsets = indexOffsets;
	}

	/**
	 * Get vertex buffer.
	 * @return The vertex buffer
	 */
	public FloatBuffer getVertexBuffer()
	{
		return vertexBuffer;
	}

	/**
	 * Get index buffer.
	 * @return The index buffer
	 */
	public ShortBuffer getIndexBuffer()
	{
		return indexBuffer;
	}

	/**
	 * Get texture.
	 * @return The texture
	 */
	public Texture getTexture()
	{
		return texture;
	}

	/**
	 * Set texture.
	 * @param texture The texture
	 */
	public void setTexture(
		final Texture texture)
	{
		// Set texture
		this.texture = texture;
	}

	/**
	 * Get position.
	 * @return The position
	 */
	public Vector3f getPosition()
	{
		return position;
	}

	/**
	 * Set position.
	 * @param position The position
	 */
	public void setPosition(
		final Vector3f position)
	{
		// Set position
		this.position = position;
	}

	/**
	 * Get rotation.
	 * @return The rotation
	 */
	public Vector3f getRotation()
	{
		return rotation;
	}

	/**
	 * Set rotation.
	 * @param rotation The rotation
	 */
	public void setRotation(
		final Vector3f rotation)
	{
		// Set rotation
		this.rotation = rotation;
	}

	/**
	 * Get bounding box.
	 * @return The bounding box
	 */
	public BoundingBox getBoundingBox()
	{
		return boundingBox;
	}

	/**
	 * Add vertex to mesh.
	 * @param positionX The position X component
	 * @param positionY The position Y component
	 * @param positionZ The position Z component
	 * @param normalX The normal X component
	 * @param normalY The normal Y component
	 * @param normalZ The normal Z component
	 * @param colorR The color R component
	 * @param colorG The color G component
	 * @param colorB The color B component
	 * @param colorA The color A component
	 * @param textureX The texture coordinate X component
	 * @param textureY The texture coordinate Y component
	 */
	public void addVertex(
		final float positionX,
		final float positionY,
		final float positionZ,
		final float normalX,
		final float normalY,
		final float normalZ,
		final float colorR,
		final float colorG,
		final float colorB,
		final float colorA,
		final float textureX,
		final float textureY)
	{
		// Add position to vertex buffer
		vertexBuffer.add(positionX);
		vertexBuffer.add(positionY);
		vertexBuffer.add(positionZ);

		// Add normal to vertex buffer
		vertexBuffer.add(normalX);
		vertexBuffer.add(normalY);
		vertexBuffer.add(normalZ);

		// Add color to vertex buffer
		vertexBuffer.add(colorR);
		vertexBuffer.add(colorG);
		vertexBuffer.add(colorB);
		vertexBuffer.add(colorA);

		// Add texture coordinates to vertex buffer
		vertexBuffer.add(textureX);
		vertexBuffer.add(textureY);

		// Increment vertex count
		vertexCount++;

		// Add index to index buffer
		indexBuffer.add(indexCount);

		// Increment index count
		indexCount++;

		// Update bounding box
		boundingBox.update(positionX, positionY, positionZ);
	}

	//TODO - remove
	public void setIndexCount(
		final short indexCount)
	{
		this.indexCount = indexCount;
	}

}

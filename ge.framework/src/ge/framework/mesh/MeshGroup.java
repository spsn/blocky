package ge.framework.mesh;

/**
 * Represents a mesh group.
 * Maintains a bounding box for the mesh group.
 */
public class MeshGroup
{
	// Mesh list
	protected java.util.List<Mesh> meshList;

	// Bounding box
	private BoundingBox boundingBox;

	/**
	 * Constructor.
	 */
	public MeshGroup()
	{
		// Call super constructor
		super();

		// Create mesh list
		meshList = new java.util.ArrayList<Mesh>();

		// Create bounding box
		boundingBox = new BoundingBox();
	}

	/**
	 * Add mesh to scene.
	 * @param mesh The mesh
	 */
	public void addMesh(
		final Mesh mesh)
	{
		// Add mesh to mesh list
		meshList.add(mesh);

		// Update bounding box
		boundingBox.update(mesh.getBoundingBox());
	}

}

package ge.demo.terrain;
import ge.framework.mesh.Mesh;

//TODO
public class MeshSet
{
	//TODO
	private java.util.List<Mesh> removeMeshList;

	//TODO
	private java.util.List<Mesh> addMeshList;

	/**
	 * Constructor.
	 */
	public MeshSet()
	{
		// Call super constructor
		super();

		//TODO
		removeMeshList = new java.util.ArrayList<Mesh>();

		//TODO
		addMeshList = new java.util.ArrayList<Mesh>();
	}

	public java.util.List<Mesh> getRemoveMeshList()
	{
		return removeMeshList;
	}

	public java.util.List<Mesh> getAddMeshList()
	{
		return addMeshList;
	}

}

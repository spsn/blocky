package ge.framework.render;
import ge.framework.mesh.Mesh;
import ge.framework.util.Color;

import org.newdawn.slick.opengl.Texture;

/**
 * Represents a renderer.
 */
public abstract class Renderer
{
	// Background color
	protected Color backgroundColor;

	// Camera
	protected Camera camera;

	// Texture
	protected Texture texture;

	// Opaque mesh list
	protected java.util.List<Mesh> opaqueMeshList;

	// Transparent mesh list
	protected java.util.List<Mesh> transparentMeshList;

	// Overlay mesh list
	protected java.util.List<Mesh> overlayMeshList;

	// Viewing frustum
	protected Frustum frustum;

	//TODO
	// Counters
	public Counters counters;

	/**
	 * Constructor.
	 */
	public Renderer()
	{
		// Call super constructor
		super();

		// Create opaque mesh list
		opaqueMeshList = new java.util.ArrayList<Mesh>();

		// Create transparent mesh list
		transparentMeshList = new java.util.ArrayList<Mesh>();

		// Create overlay mesh list
		overlayMeshList = new java.util.ArrayList<Mesh>();

		// Create viewing frustum
		frustum = new Frustum();

		// Create counters
		counters = new Counters();
	}

	/**
	 * Set background color.
	 * @param backgroundColor The background color
	 */
	public void setBackgroundColor(
		final Color backgroundColor)
	{
		// Set background color
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Set camera.
	 * @param The camera
	 */
	public void setCamera(
		final Camera camera)
	{
		// Set camera
		this.camera = camera;
	}

	/**
	 * Set texture.
	 * @param The texture
	 */
	public void setTexture(
		final Texture texture)
	{
		// Set texture
		this.texture = texture;
	}

	/**
	 * Add mesh to scene.
	 * @param mesh The mesh
	 */
	public void addMesh(
		final Mesh mesh)
	{

		// Mesh is configured for immediate rendering?
		if (mesh.isDeferred() == false)
		{
			// Prepare mesh for rendering
			prepareMesh(mesh);
		}

		// Mesh is opaque mesh?
		if (mesh.getMeshType() == Mesh.MeshType.OPAQUE)
		{
			// Add mesh to opaque mesh list
			opaqueMeshList.add(mesh);
		}
		// Mesh is transparent mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.TRANSPARENT)
		{
			// Add mesh to transparent mesh list
			transparentMeshList.add(mesh);
		}
		// Mesh is overlay mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.OVERLAY)
		{
			// Add mesh to overlay mesh list
			overlayMeshList.add(mesh);
		}

	}

	/**
	 * Remove mesh from scene.
	 * @param mesh The mesh
	 */
	public void removeMesh(
		final Mesh mesh)
	{
		// Destroy mesh from rendering
		destroyMesh(mesh);

		// Mesh is opaque mesh?
		if (mesh.getMeshType() == Mesh.MeshType.OPAQUE)
		{
			// Remove mesh from opaque mesh list
			opaqueMeshList.remove(mesh);
		}
		// Mesh is transparent mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.TRANSPARENT)
		{
			// Remove mesh from transparent mesh list
			transparentMeshList.remove(mesh);
		}
		// Mesh is overlay mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.OVERLAY)
		{
			// Remove mesh from overlay mesh list
			overlayMeshList.remove(mesh);
		}

	}

	/**
	 * Create display.
	 */
	public abstract void createDisplay() throws java.lang.Exception;

	/**
	 * Render scene.
	 */
	public abstract boolean renderScene();
	//TODO
	public abstract boolean renderOverlays();

	/**
	 * Prepare mesh for rendering.
	 * @param mesh The mesh
	 */
	protected abstract void prepareMesh(
		final Mesh mesh);

	/**
	 * Destroy mesh from rendering.
	 * @param mesh The mesh
	 */
	protected abstract void destroyMesh(
		final Mesh mesh);

}

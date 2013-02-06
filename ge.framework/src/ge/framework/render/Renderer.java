package ge.framework.render;
import ge.framework.mesh.Mesh;
import ge.framework.profile.Profiler;
import ge.framework.shader.Program;
import ge.framework.util.Color;

import org.newdawn.slick.opengl.Texture;

/**
 * Represents a renderer.
 */
public abstract class Renderer
{
	// Wait for vsync indicator
	protected boolean waitForVsync;

	// Background color
	protected Color backgroundColor;

	// Camera
	protected Camera camera;

	// Texture
	protected Texture texture;

	// Opaque mesh list
	protected java.util.List<Mesh> opaqueMeshList;

	// Model mesh list
	protected java.util.List<Mesh> modelMeshList;

	// Transparent mesh list
	protected java.util.List<Mesh> transparentMeshList;

	// Overlay mesh list
	protected java.util.List<Mesh> overlayMeshList;

	// Viewing frustum
	protected Frustum frustum;

	// Current texture
	protected Texture currentTexture;

	// Current shader program
	protected Program currentProgram;

	//TODO
	// Profiler
	public Profiler profiler;

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

		// Create model mesh list
		modelMeshList = new java.util.ArrayList<Mesh>();

		// Create transparent mesh list
		transparentMeshList = new java.util.ArrayList<Mesh>();

		// Create overlay mesh list
		overlayMeshList = new java.util.ArrayList<Mesh>();

		// Create viewing frustum
		frustum = new Frustum();

		// Create profiler
		profiler = new Profiler();

		// Create counters
		counters = new Counters();
	}

	/**
	 * Set wait for vsync indicator.
	 * @param backgroundColor The wait for vsync indicator
	 */
	public void setWaitForVsync(
		final boolean waitForVsync)
	{
		// Set wait for vsync indicator
		this.waitForVsync = waitForVsync;
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
		// Mesh is model mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.MODEL)
		{
			// Add mesh to model mesh list
			modelMeshList.add(mesh);
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
		// Mesh is model mesh?
		else if (mesh.getMeshType() == Mesh.MeshType.MODEL)
		{
			// Remove mesh from model mesh list
			modelMeshList.remove(mesh);
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

	/**
	 * Activate shader program.
	 * The current shader program is deactivated before the new shader program is activated.
	 * If the new shader program is the same as the current shader program then no action is taken.
	 * @param program The shader program
	 */
	protected void activateProgram(
		final Program program)
	{

		// New shader program is different from current shader program
		if (program != currentProgram)
		{

//			// Current shader program defined?
//			if (currentProgram != null)
//			{
//				// Deactivate current shader program
//				currentProgram.deactivate();
//
//				//TODO
//				profiler.measure(Profiler.DEACTIVATE_PROGRAM);
//			}

			// Activate new shader program
			program.activate();

			// Set current shader program
			currentProgram = program;

			//TODO
			profiler.measure(Profiler.ACTIVATE_PROGRAM);
		}

	}

	/**
	 * Bind texture.
	 * If the new texture is the same as the current texture then no action is taken.
	 * @param texture The texture
	 */
	protected void bindTexture(
		final Texture texture)
	{
		// Bind texture
		bindTexture(texture, true);
	}

	/**
	 * Bind texture.
	 * If the new texture is the same as the current texture then no action is taken.
	 * @param texture The texture
	 */
	protected void bindTexture(
		final Texture texture,
		final boolean profile)
	{

		// New texture is different from current texture
		if (texture != currentTexture)
		{
			// Bind new texture
			texture.bind();

			// Set texture parameters
			setTextureParameters(texture);

			// Set current shader program
			currentTexture = texture;

			//TODO
			if (profile == true)
			{
				profiler.measure(Profiler.BIND_TEXTURE);
			}

		}

	}

	/**
	 * Set texture parameters.
	 * @param texture The texture
	 */
	protected abstract void setTextureParameters(
		final Texture texture);

}

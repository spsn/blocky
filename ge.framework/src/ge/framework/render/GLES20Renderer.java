package ge.framework.render;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import ge.framework.profile.Profiler;
import ge.framework.shader.GLES20Program;
import ge.framework.util.Matrix;
import ge.framework.util.Ray;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.opengles.PixelFormat;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Represents a renderer for OpenGL ES 2.0.
 */
public class GLES20Renderer extends Renderer
{
	// Display mode
	private DisplayMode displayMode;

	// Shader program for opaque meshes
	private GLES20Program opaqueProgram;

	// Shader program for model meshes
	private GLES20Program modelProgram;

	// Shader program for transparent meshes
	private GLES20Program transparentProgram;

	// Vertex buffer
	private java.nio.FloatBuffer sharedVertexBuffer;

	// Index buffer
	private java.nio.ShortBuffer sharedIndexBuffer;

	// Projection matrix
	private Matrix4f projectionMatrix;

	// Model view matrix
	private Matrix4f modelViewMatrix;

	// Orthogonal matrix
	private Matrix4f orthogonalMatrix;

	// Model view projection matrix
	private Matrix4f mvpMatrix;

	// Model view projection matrix buffer
	private java.nio.FloatBuffer mvpMatrixBuffer;

	// Profiler
	public Profiler profiler;

	//TODO
	private int visBatchCount;

	//TODO
	private int eventSwivel;
	private boolean eventsReady;

	/**
	 * Constructor.
	 */
	public GLES20Renderer()
	{
		// Call super constructor
		super();

		// Create shared vertex buffer
		//TODO
		sharedVertexBuffer = BufferUtils.createFloatBuffer(16 * 16 * 16 * 6 * 4 * 12);

		// Create shared index buffer
		//TODO
		sharedIndexBuffer = BufferUtils.createShortBuffer(16 * 16 * 16 * 6 * 4);

		//TODO
		projectionMatrix = new Matrix4f();
		modelViewMatrix = new Matrix4f();
		orthogonalMatrix = new Matrix4f();
		mvpMatrix = new Matrix4f();

		//TODO
		mvpMatrixBuffer = BufferUtils.createFloatBuffer(16);

		// Create profiler
		profiler = new Profiler();
	}

	//TODO
	public void setOpaqueProgram(
		final GLES20Program opaqueProgram)
	{
		this.opaqueProgram = opaqueProgram;
	}

	//TODO
	public void setModelProgram(
		final GLES20Program modelProgram)
	{
		this.modelProgram = modelProgram;
	}

	//TODO
	public void setTransparentProgram(
		final GLES20Program transparentProgram)
	{
		this.transparentProgram = transparentProgram;
	}

	/**
	 * Create display.
	 */
	public void createDisplay() throws java.lang.Exception
	{
		// Control mouse
		Mouse.setGrabbed(true);

		// Set display mode
		displayMode = Display.getDesktopDisplayMode();
//		displayMode = new DisplayMode(1280, 720);
		Display.setDisplayMode(displayMode);

		//TODO
		Display.setInitialBackground(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue());

		//TODO
		Display.setVSyncEnabled(true);
		Display.setFullscreen(true);

		// Create display
		Display.create(new PixelFormat());

		//TODO
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
//		GLES20.glDepthMask(true);
//		GLES20.glShadeModel(GLES20.GL_SMOOTH);
	}

	/**
	 * Set projection from camera parameters.
	 */
	private void setProjection()
	{
		// Reset projection matrix
		Matrix.glMatrixMode(Matrix.GL_PROJECTION);
		Matrix.glLoadIdentity();

		// Set viewing perspective
		Matrix.gluPerspective(camera.getFieldOfView(), ((float) displayMode.getWidth() / (float) displayMode.getHeight()), 0.1f, camera.getViewingDistance());

		// Get projection matrix
		Matrix.glMatrixMode(Matrix.GL_PROJECTION);
		Matrix.glGetMatrix(projectionMatrix);
	}

	/**
	 * Prepare mesh for rendering.
	 * @param mesh The mesh
	 */
	protected void prepareMesh(
		final Mesh mesh)
	{
		// Local variables
		FloatBuffer vertexBuffer;
		ShortBuffer indexBuffer;
		int vertexBufferId;
		int indexBufferId;

		// Get vertex buffer data
		vertexBuffer = mesh.getVertexBuffer();
		sharedVertexBuffer.put(vertexBuffer.getContent(), 0, vertexBuffer.getSize());
		sharedVertexBuffer.flip();

		// Get index buffer data
		//TODO
		indexBuffer = convertIndexBuffer(mesh.getIndexBuffer());
		sharedIndexBuffer.put(indexBuffer.getContent(), 0, indexBuffer.getSize());
		sharedIndexBuffer.flip();

		// Send vertex data
		vertexBufferId = GLES20.glGenBuffers();
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferId);
		GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, sharedVertexBuffer, GLES20.GL_STATIC_DRAW);

		// Send index data
		indexBufferId = GLES20.glGenBuffers();
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, indexBufferId);
		GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, sharedIndexBuffer, GLES20.GL_STATIC_DRAW);

		// Clear buffers
		sharedVertexBuffer.clear();
		sharedIndexBuffer.clear();

		// Update mesh
		mesh.setVertexBufferId(vertexBufferId);
		mesh.setIndexBufferId(indexBufferId);
		//TODO
		mesh.setIndexCount((short)indexBuffer.getSize());
	}

	/**
	 * Destroy mesh from rendering.
	 * @param mesh The mesh
	 */
	protected void destroyMesh(
		final Mesh mesh)
	{
		// Remove vertex data
		GLES20.glDeleteBuffers(mesh.getVertexBufferId());

		// Remove index data
		GLES20.glDeleteBuffers(mesh.getIndexBufferId());
	}

	/**
	 * Convert index buffer from quad layout to triangle layout.
	 * @param indexBuffer The index buffer
	 * @return The converted index buffer
	 */
	private ShortBuffer convertIndexBuffer(
		final ShortBuffer indexBuffer)
	{
		// Local variables
		int size;
		short[] data;
		ShortBuffer convertedBuffer;

		// Get index buffer data
		size = indexBuffer.getSize();
		data = indexBuffer.getContent();

		// Convert index buffer data
		convertedBuffer = new ShortBuffer((int)(size * 1.5));

		for (int i = 0; i < (size / 4); i++)
		{
			int iindex = (i * 4);
			convertedBuffer.add(data[iindex + 0]);
			convertedBuffer.add(data[iindex + 1]);
			convertedBuffer.add(data[iindex + 2]);
			convertedBuffer.add(data[iindex + 2]);
			convertedBuffer.add(data[iindex + 3]);
			convertedBuffer.add(data[iindex + 0]);
		}

		return convertedBuffer;
	}

	/**
	 * Render scene.
	 */
	public boolean renderScene()
	{
		//TODO
		profiler.measure(Profiler.OTHER);

		//TODO
		// Close requested?
		if (Display.isCloseRequested() == true)
		{
			// Destroy display
			Display.destroy();

			return false;
		}

		// Has camera view changed?
		if (camera.hasViewChanged() == true)
		{
			// Set projection from camera parameters
			setProjection();
		}

		//TODO
		GLES20.glClearColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());

		// Clear buffers
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		//TODO
		profiler.measure(Profiler.CLEAR_BUFFER);

		// Reset model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glLoadIdentity();

		// Rotate view to camera orientation
		Matrix.glRotatef(camera.getYaw(), 1.0f, 0.0f, 0.0f);
		Matrix.glRotatef(camera.getPitch(), 0.0f, 1.0f, 0.0f);

		// Move view to camera position
		Matrix.glTranslatef(camera.getPositionX(), camera.getPositionY(), camera.getPositionZ());

		//TODO
		profiler.measure(Profiler.UPDATE_MATRIX);

		// Get model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glGetMatrix(modelViewMatrix);

		// Generate model view projection matrix
		Matrix4f.mul(projectionMatrix, modelViewMatrix, mvpMatrix);
		mvpMatrix.store(mvpMatrixBuffer);
		mvpMatrixBuffer.flip();

		//TODO
		profiler.measure(Profiler.GET_MATRIX);

		// Calculate viewing frustum for culling
		frustum.calculateFrustum(mvpMatrixBuffer);

		//TODO
		profiler.measure(Profiler.CALCULATE_FRUSTUM);

		//TODO - per mesh / per list
		// Texture defined?
		if (texture != null)
		{
			// Bind texture
			texture.bind();
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		}

		//TODO
		profiler.measure(Profiler.BIND_TEXTURE);

		//TODO
		visBatchCount = 0;

		// Disable alpha blending for opaque meshes
		GLES20.glDisable(GLES20.GL_BLEND);

		// Activate shader program for opaque meshes
		opaqueProgram.activate();

		//TODO
		profiler.measure(Profiler.ACTIVATE_PROGRAM);

		// Set model view projection matrix in shader program
		GLES20.glUniformMatrix4(opaqueProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

		// Set texture sampler in shader program
		GLES20.glUniform1i(opaqueProgram.getFragmentSamplerUniform(), 0);

		//TODO
		profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

		// Render opaque meshes
		renderMeshList(opaqueMeshList,
			opaqueProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
			opaqueProgram.getVertexPositionAttribute(), opaqueProgram.getVertexNormalAttribute(),
			opaqueProgram.getVertexColorAttribute(), opaqueProgram.getVertexTextureAttribute());

		// Deactivate shader program for opaque meshes
		opaqueProgram.deactivate();

		//TODO
		profiler.measure(Profiler.DEACTIVATE_PROGRAM);

		// Scene contains model meshes?
		if (modelMeshList.size() > 0)
		{
			// Activate shader program for model meshes
			modelProgram.activate();

			//TODO
			profiler.measure(Profiler.ACTIVATE_PROGRAM);

			// Set model view projection matrix in shader program
			GLES20.glUniformMatrix4(modelProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

			// Set texture sampler in shader program
			GLES20.glUniform1i(modelProgram.getFragmentSamplerUniform(), 0);

			//TODO
			profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

			// Render model meshes
			renderMeshList(modelMeshList,
				modelProgram.getModelPositionUniform(), modelProgram.getModelRotationUniform(),
				modelProgram.getVertexPositionAttribute(), modelProgram.getVertexNormalAttribute(),
				modelProgram.getVertexColorAttribute(), modelProgram.getVertexTextureAttribute());

			// Deactivate shader program for model meshes
			modelProgram.deactivate();

			//TODO
			profiler.measure(Profiler.DEACTIVATE_PROGRAM);
		}

		// Scene contains transparent meshes?
		if ((transparentMeshList.size() > 0)
			|| (overlayMeshList.size() > 0))
		{
			// Enable alpha blending for transparent meshes
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
			GLES20.glEnable(GLES20.GL_BLEND);

			// Activate shader program for transparent meshes
			transparentProgram.activate();

			//TODO
			profiler.measure(Profiler.ACTIVATE_PROGRAM);

			// Set model view projection matrix in shader program
			GLES20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

			// Set texture sampler in shader program
			GLES20.glUniform1i(transparentProgram.getFragmentSamplerUniform(), 0);

			//TODO
			profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

			// Render transparent meshes
			renderMeshList(transparentMeshList,
				transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
				transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
				transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());

			//TODO
			if (overlayMeshList.size() > 0)
			{
				//TODO
				// Reset model view matrix
				Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
				Matrix.glLoadIdentity();

				// Get model view matrix
				Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
				Matrix.glGetMatrix(orthogonalMatrix);

				// Generate orthogonal projection matrix
				orthogonalMatrix.store(mvpMatrixBuffer);
				mvpMatrixBuffer.flip();

				//TODO
				profiler.measure(Profiler.GET_MATRIX);

				// Enable color invert blending for overlay meshes
//				GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
//				GL11.glEnable(GL11.GL_BLEND);

				// Set orthogonal projection matrix in shader program
				GLES20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

				//TODO
				profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

				// Render overlay meshes
				renderMeshList(overlayMeshList,
					transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
					transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
					transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());
			}

			// Deactivate shader program for transparent meshes
			transparentProgram.deactivate();

			//TODO
			profiler.measure(Profiler.DEACTIVATE_PROGRAM);
		}

		//TODO
		counters.visBatchCount = visBatchCount;

		// Swap buffers
//		GL11.glFlush();
//		GL11.glFinish();
		Display.update(false);

		//TODO
		if (eventSwivel == 0)
		{
			Display.processMessages();
			eventsReady = true;
		}
		else
		{
			eventsReady = false;
		}

		//TODO
		eventSwivel = (eventSwivel == 0) ? 0 : eventSwivel + 1;

		//TODO
		profiler.measure(Profiler.SWAP_BUFFERS);

		return true;
	}

	/**
	 * Render overlays.
	 */
	public boolean renderOverlays()
	{
		//TODO
		profiler.measure(Profiler.OTHER);

		//TODO
		// Close requested?
		if (Display.isCloseRequested() == true)
		{
			// Destroy display
			Display.destroy();

			return false;
		}

		//TODO
		GLES20.glClearColor(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue(), backgroundColor.getAlpha());

		// Clear buffers
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		//TODO
		profiler.measure(Profiler.CLEAR_BUFFER);

		//TODO
		// Reset model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glLoadIdentity();

		// Get model view matrix
		Matrix.glMatrixMode(Matrix.GL_MODELVIEW);
		Matrix.glGetMatrix(orthogonalMatrix);

		// Generate orthogonal projection matrix
		orthogonalMatrix.store(mvpMatrixBuffer);
		mvpMatrixBuffer.flip();

		//TODO
		profiler.measure(Profiler.GET_MATRIX);

		//TODO - per mesh / per list
		// Bind texture
		if (texture != null)
		{
			texture.bind();
		}

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

		//TODO
		profiler.measure(Profiler.BIND_TEXTURE);

		//TODO
		visBatchCount = 0;

		// Enable alpha blending for transparent meshes
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA); 
		GLES20.glEnable(GLES20.GL_BLEND);
		// Enable color invert blending for overlay meshes
//		GL11.glBlendFunc(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ZERO);
//		GL11.glEnable(GL11.GL_BLEND);

		// Activate shader program for transparent meshes
		transparentProgram.activate();

		//TODO
		profiler.measure(Profiler.ACTIVATE_PROGRAM);

		// Set orthogonal projection matrix in shader program
		GLES20.glUniformMatrix4(transparentProgram.getMvpMatrixUniform(), false, mvpMatrixBuffer);

		// Set texture sampler in shader program
		GLES20.glUniform1i(transparentProgram.getFragmentSamplerUniform(), 0);

		//TODO
		profiler.measure(Profiler.SET_PROGRAM_VARIABLES);

		// Render overlay meshes
		renderMeshList(overlayMeshList,
			transparentProgram.getModelPositionUniform(), opaqueProgram.getModelRotationUniform(),
			transparentProgram.getVertexPositionAttribute(), transparentProgram.getVertexNormalAttribute(),
			transparentProgram.getVertexColorAttribute(), transparentProgram.getVertexTextureAttribute());

		// Deactivate shader program for transparent meshes
		transparentProgram.deactivate();

		//TODO
		profiler.measure(Profiler.DEACTIVATE_PROGRAM);

		//TODO
		counters.visBatchCount = visBatchCount;

		// Swap buffers
//		GL11.glFlush();
//		GL11.glFinish();
		Display.update(false);
//		Display.processMessages();

		//TODO
		profiler.measure(Profiler.SWAP_BUFFERS);

		return true;
	}

	/**
	 * TODO
	 * Render mesh list.
	 * @param meshList The mesh list
	 * @param modelPositionUniform The model position uniform location
	 * @param modelRotationUniform The model rotation uniform location
	 * @param vertexPositionAttribute The vertex position attribute location
	 * @param vertexNormalAttribute The vertex normal attribute location
	 * @param vertexColorAttribute The vertex color attribute location
	 * @param vertexTextureAttribute The vertex texture attribute location
	 */
	private void renderMeshList(
		final java.util.List<Mesh> meshList,
		final int modelPositionUniform,
		final int modelRotationUniform,
		final int vertexPositionAttribute,
		final int vertexNormalAttribute,
		final int vertexColorAttribute,
		final int vertexTextureAttribute)
	{
		// Local variables
		java.util.ListIterator<Mesh> iterator;
		Mesh mesh;
		boolean render;
		Vector3f position;
		Vector3f rotation;

		//TODO - get from mesh
		int stride = 48; 
		int positionOffset = 0;
		int normalOffset = 12;
		int colorOffset = 24;
		int textureOffset = 40;

		// Render mesh list
		for (iterator = meshList.listIterator(); iterator.hasNext() == true;)
		{
			mesh = (Mesh) iterator.next();

			//TODO
//			profiler.measure(Profiler.ITERATE_LOOP);

			// Default do not render mesh
			render = false;

			// Mesh is overlay mesh?
			if ((mesh.getMeshType() == Mesh.MeshType.OVERLAY) || (mesh.getMeshType() == Mesh.MeshType.MODEL))
			{
				// Render mesh
				render = true;
			}
			else
			{

				// Bounding box within viewing frustum?
				if (frustum.boxInFrustum(mesh.getBoundingBox(), profiler) == true)
				{
					// Render mesh
					render = true;
				}

				//TODO
//				profiler.measure(Profiler.BOX_IN_FRUSTUM);
			}

			// Render mesh ?
			if (render == true)
			{

				// Texture defined for mesh?
				if (mesh.getTexture() != null)
				{
					// Bind texture
					mesh.getTexture().bind();
					GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
					GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

					//TODO
//					profiler.measure(Profiler.BIND_TEXTURE);
				}

				//TODO - model position
				if ((modelPositionUniform != -1) && (mesh.getPosition() != null))
				{
					//TODO
					position = mesh.getPosition();

					//TODO
					GLES20.glUniform3f(modelPositionUniform, position.x, position.y, position.z);
				}

				//TODO - model rotation
				if ((modelRotationUniform != -1) && (mesh.getRotation() != null))
				{
					//TODO
					rotation = mesh.getRotation();

					//TODO
					GLES20.glUniform3f(modelRotationUniform, (float) Math.toRadians(rotation.x), (float) Math.toRadians(rotation.y), (float) Math.toRadians(rotation.z));
				}

				// Bind to vertex buffer
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.getVertexBufferId());

				//TODO
//				profiler.measure(Profiler.BIND_BUFFER);

				// Set vertex attributes
				if (vertexPositionAttribute != -1)
				{
					GLES20.glVertexAttribPointer(vertexPositionAttribute, 3, GLES20.GL_FLOAT, false, stride, positionOffset);
				}

				if (vertexNormalAttribute != -1)
				{
					GLES20.glVertexAttribPointer(vertexNormalAttribute, 3, GLES20.GL_FLOAT, false, stride, normalOffset);
				}

				if (vertexColorAttribute != -1)
				{
					GLES20.glVertexAttribPointer(vertexColorAttribute, 4, GLES20.GL_FLOAT, false, stride, colorOffset);
				}

				if (vertexTextureAttribute != -1)
				{
					GLES20.glVertexAttribPointer(vertexTextureAttribute, 2, GLES20.GL_FLOAT, false, stride, textureOffset);
				}

				//TODO
//				profiler.measure(Profiler.SET_ATTRIB_POINTER);

				// Bind to index buffer
				GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.getIndexBufferId());

				//TODO
//				profiler.measure(Profiler.BIND_BUFFER);

				//TODO
				if (mesh.getIndexOffsets() != null)
				{
					short[] indexOffsets = mesh.getIndexOffsets();

					//TODO
					for (int i = 0; i < indexOffsets.length; i++)
					{
						// Draw mesh
						GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, indexOffsets[i]);
					}

				}
				else
				{
					// Draw mesh
					GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndexCount(), GLES20.GL_UNSIGNED_SHORT, mesh.getIndexOffset());
				}

				//TODO
//				profiler.measure(Profiler.DRAW_ELEMENTS);

				//TODO
				visBatchCount++;
			}

		}

		//TODO
		profiler.measure(Profiler.DRAW_ELEMENTS);
	}

	//TODO
	public Ray pick(
		final float magnitude)
	{
		//TODO - picking
		Matrix4f mviMatrix = new Matrix4f();
		Matrix4f.invert(modelViewMatrix, mviMatrix);

		Vector4f camSpaceNear = new Vector4f(0, 0, 0, 1);
		Vector4f worldSpaceNear = new Vector4f();
		Matrix4f.transform(mviMatrix, camSpaceNear, worldSpaceNear);

		Vector4f camSpaceFar = new Vector4f(0, 0, magnitude, 1);
		Vector4f worldSpaceFar = new Vector4f();
		Matrix4f.transform(mviMatrix, camSpaceFar, worldSpaceFar);
		
		Vector3f rayPosition = new Vector3f(worldSpaceNear.x, worldSpaceNear.y, worldSpaceNear.z);
		Vector3f rayDirection = new Vector3f(worldSpaceFar.x - worldSpaceNear.x, worldSpaceFar.y - worldSpaceNear.y, worldSpaceFar.z - worldSpaceNear.z);
		rayDirection.normalise();
		//TODO - picking

		return new Ray(rayPosition, rayDirection);
	}

	//TODO
	public boolean eventsReady()
	{
		return eventsReady;
	}

}

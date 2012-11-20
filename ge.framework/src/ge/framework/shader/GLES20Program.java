package ge.framework.shader;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.test.opengles.util.Shader;

/**
 * Represents a shader program.
 */
public class GLES20Program
{
	// Identifier
	private int id;

	// Model view projection matrix uniform location
	private int mvpMatrixUniform;

	// Model position rotation matrix uniform location
	private int mprMatrixUniform;

	// Model position uniform location
	private int modelPositionUniform;

	// Model rotation uniform location
	private int modelRotationUniform;

	// Vertex position attribute location
	private int vertexPositionAttribute;

	// Vertex normal attribute location
	private int vertexNormalAttribute;

	// Vertex color attribute location
	private int vertexColorAttribute;

	// Vertex texture coordinate attribute location
	private int vertexTextureAttribute;

	// Fragment texture sampler uniform location
	private int fragmentSamplerUniform;

	/**
	 * Constructor.
	 * @param vertexShader The vertex shader code
	 * @param fragmentShader The fragment shader code
	 */
	public GLES20Program(
		final String vertexShaderCode,
		final String fragmentShaderCode)
	{
		// Call constructor
		this(new Shader(GLES20.GL_VERTEX_SHADER, vertexShaderCode), new Shader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode));
	}

	/**
	 * Constructor.
	 * @param shaders The list of shaders to attach
	 */
	public GLES20Program(
		final Shader... shaders)
	{
		// Create program
		id = GLES20.glCreateProgram();

		// Attach shaders to program
		for (Shader shader : shaders)
		{
			GLES20.glAttachShader(id, shader.getID());
		}

		// Link program
		GLES20.glLinkProgram(id);

		// Failed to link program?
		if (GLES20.glGetProgram(id, GLES20.GL_LINK_STATUS) == GLES20.GL_FALSE)
		{
			// Log program information
			logProgramInformation();

			// Destroy program
			destroy();

			throw new java.lang.RuntimeException("Failed to link shader program: " + id);
		}

		// Get model view projection matrix uniform location
		mvpMatrixUniform = getUniformLocationChecked("ModelViewProjectionMatrix");

		// Get model position rotation matrix uniform location
		mprMatrixUniform = getUniformLocationChecked("ModelPositionRotationMatrix");

		// Get model position uniform location
		modelPositionUniform = getUniformLocationChecked("mPosition");

		// Get model rotation uniform location
		modelRotationUniform = getUniformLocationChecked("mRotation");

		// Get vertex position attribute location
		vertexPositionAttribute = getAttributeLocationChecked("vPosition");

		// Get vertex normal attribute location
		vertexNormalAttribute = getAttributeLocationChecked("vNormal");

		// Get vertex color attribute location
		vertexColorAttribute = getAttributeLocationChecked("vColor");

		// Get vertex texture coordinate attribute location
		vertexTextureAttribute = getAttributeLocationChecked("vTexture");

		// Get fragment texture sampler uniform location
		fragmentSamplerUniform = getUniformLocationChecked("fTextureSampler");
	}

	/**
	 * Get identifier.
	 * @return The identifier
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * Validate shader program.
	 */
	public void validate()
	{
		// Validate program
		GLES20.glValidateProgram(id);

		// Failed to validate program?
		if (GLES20.glGetProgram(id, GLES20.GL_VALIDATE_STATUS) == GLES20.GL_FALSE)
		{
			// Log program information
			logProgramInformation();

			throw new RuntimeException("Failed to validate shader program.");
		}

	}

	/**
	 * Destroy shader program.
	 */
	public void destroy()
	{
		// Delete program
		GLES20.glDeleteProgram(id);
	}

	/**
	 * Get uniform location.
	 * @param uniform The uniform
	 * @return The uniform location
	 */
	public int getUniformLocation(
		final String uniform)
	{
		// Local variables
		int location;

		// Get uniform location
		location = GLES20.glGetUniformLocation(id, uniform);

		// Uniform not defined?
		if (location == -1)
		{
			throw new IllegalArgumentException("Invalid uniform name specified: " + uniform);
		}

		return location;
	}

	/**
	 * Get uniform location.
	 * @param uniform The uniform
	 * @return The uniform location
	 */
	private int getUniformLocationChecked(
		final String uniform)
	{

		try
		{
			// Get uniform location
			return getUniformLocation(uniform);
		}
		catch (java.lang.IllegalArgumentException exception)
		{
			// Uniform not used
			return -1;
		}

	}

	/**
	 * Get attribute location.
	 * @param attribute The attribute
	 * @return The attribute location
	 */
	public int getAttributeLocation(
		final String attribute)
	{
		// Local variables
		int location;

		// Get attribute location
		location = GLES20.glGetAttribLocation(id, attribute);

		// Attribute not defined?
		if (location == -1)
		{
			throw new IllegalArgumentException("Invalid attribute name specified: " + attribute);
		}

		return location;
	}

	/**
	 * Get attribute location.
	 * @param attribute The attribute
	 * @return The attribute location
	 */
	private int getAttributeLocationChecked(
		final String attribute)
	{

		try
		{
			// Get attribute location
			return getAttributeLocation(attribute);
		}
		catch (java.lang.IllegalArgumentException exception)
		{
			// Attribute not used
			return -1;
		}

	}

	/**
	 * Activate shader program.
	 */
	public void activate()
	{
		// Enable shader program
		GLES20.glUseProgram(id);

		// Enable vertex attributes
		if (vertexPositionAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(vertexPositionAttribute);
		}

		if (vertexNormalAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(vertexNormalAttribute);
		}

		if (vertexColorAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(vertexColorAttribute);
		}

		if (vertexTextureAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(vertexTextureAttribute);
		}

	}

	/**
	 * Deactivate shader program.
	 */
	public void deactivate()
	{

		// Disable vertex attributes
		if (vertexPositionAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(vertexPositionAttribute);
		}

		if (vertexNormalAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(vertexNormalAttribute);
		}

		if (vertexColorAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(vertexColorAttribute);
		}

		if (vertexTextureAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(vertexTextureAttribute);
		}

		// Disable shader program
		GLES20.glUseProgram(0);
	}

	/**
	 * Get model view projection matrix uniform location.
	 * @return The model view projection matrix uniform location
	 */
	public int getMvpMatrixUniform()
	{
		return mvpMatrixUniform;
	}

	/**
	 * Get model position rotation matrix uniform location.
	 * @return The model position rotation matrix uniform location
	 */
	public int getMprMatrixUniform()
	{
		return mprMatrixUniform;
	}

	/**
	 * Get model position uniform location.
	 * @return The model position uniform location
	 */
	public int getModelPositionUniform()
	{
		return modelPositionUniform;
	}

	/**
	 * Get model rotation uniform location.
	 * @return The model rotation uniform location
	 */
	public int getModelRotationUniform()
	{
		return modelRotationUniform;
	}

	/**
	 * Get vertex position attribute location.
	 * @return The vertex position attribute location
	 */
	public int getVertexPositionAttribute()
	{
		return vertexPositionAttribute;
	}

	/**
	 * Get vertex normal attribute location.
	 * @return The vertex normal attribute location
	 */
	public int getVertexNormalAttribute()
	{
		return vertexNormalAttribute;
	}

	/**
	 * Get vertex color attribute location.
	 * @return The vertex color attribute location
	 */
	public int getVertexColorAttribute()
	{
		return vertexColorAttribute;
	}

	/**
	 * Get vertex texture coordinate attribute location.
	 * @return The vertex texture coordinate attribute location
	 */
	public int getVertexTextureAttribute()
	{
		return vertexTextureAttribute;
	}

	/**
	 * Get fragment texture sampler uniform location.
	 * @return The fragment texture sampler uniform location
	 */
	public int getFragmentSamplerUniform()
	{
		return fragmentSamplerUniform;
	}

	/**
	 * Log program information.
	 */
	private void logProgramInformation()
	{
		// Local variables
		int logLength;

		// Get information log length
		logLength = GLES20.glGetProgram(id, GLES20.GL_INFO_LOG_LENGTH);

		// Print information log length
		System.out.println(logLength);

		// Information log not defined?
		if (logLength <= 1)
		{
			return;
		}

		// Print information log length
		System.out.println("\nInfo log of shader program: " + id);
		System.out.println("-------------------");
		System.out.println(GLES20.glGetProgramInfoLog(id, logLength));
		System.out.println("-------------------");
	}

}

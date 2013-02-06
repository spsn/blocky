package ge.framework.shader;

/**
 * Represents a shader program.
 */
public abstract class Program
{
	// Identifier
	protected int id;

	// Model view projection matrix uniform location
	protected int mvpMatrixUniform;

	// Model position rotation matrix uniform location
	protected int mprMatrixUniform;

	// Model position uniform location
	protected int modelPositionUniform;

	// Model rotation uniform location
	protected int modelRotationUniform;

	// Vertex position attribute location
	protected int vertexPositionAttribute;

	// Vertex normal attribute location
	protected int vertexNormalAttribute;

	// Vertex color attribute location
	protected int vertexColorAttribute;

	// Vertex texture coordinate attribute location
	protected int vertexTextureAttribute;

	// Fragment texture sampler uniform location
	protected int fragmentSamplerUniform;

	/**
	 * Constructor.
	 */
	public Program()
	{
		// Call super constructor
		super();
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
	public abstract void validate();

	/**
	 * Destroy shader program.
	 */
	public abstract void destroy();

	/**
	 * Get uniform location.
	 * @param uniform The uniform
	 * @return The uniform location
	 */
	public abstract int getUniformLocation(
		final String uniform);

	/**
	 * Get attribute location.
	 * @param attribute The attribute
	 * @return The attribute location
	 */
	public abstract int getAttributeLocation(
		final String attribute);

	/**
	 * Activate shader program.
	 */
	public abstract void activate();

	/**
	 * Deactivate shader program.
	 */
	public abstract void deactivate();

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

}

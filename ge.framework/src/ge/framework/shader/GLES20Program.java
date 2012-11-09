package ge.framework.shader;
import org.lwjgl.opengles.GLES20;
import org.lwjgl.test.opengles.util.Shader;
import org.lwjgl.test.opengles.util.ShaderProgram;

/**
 * Represents a shader program.
 */
public class GLES20Program extends ShaderProgram
{
	// Model view projection matrix uniform location
	private int mvpMatrixUniform;

	// Position attribute location
	private int positionAttribute;

	// Normal attribute location
	private int normalAttribute;

	// Color attribute location
	private int colorAttribute;

	// Texture coordinate attribute location
	private int textureAttribute;

	// Texture sampler uniform location
	private int samplerUniform;

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
	 * @param vertexShader The vertex shader
	 * @param fragmentShader The fragment shader
	 */
	public GLES20Program(
		final Shader vertexShader,
		final Shader fragmentShader)
	{
		// Call super constructor
		super(vertexShader, fragmentShader);

		// Get model view projection matrix uniform location
		mvpMatrixUniform = getUniformLocationChecked("vModelViewProjectionMatrix");

		// Get position attribute location
		positionAttribute = getAttributeLocationChecked("vPosition");

		// Get normal attribute location
		normalAttribute = getAttributeLocationChecked("vNormal");

		// Get color attribute location
		colorAttribute = getAttributeLocationChecked("vColor");

		// Get texture coordinate attribute location
		textureAttribute = getAttributeLocationChecked("vTexture");

		// Get texture sampler uniform location
		samplerUniform = getUniformLocationChecked("fTextureSampler");
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
		super.enable();

		// Enable vertex attributes
		if (positionAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(positionAttribute);
		}

		if (normalAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(normalAttribute);
		}

		if (colorAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(colorAttribute);
		}

		if (textureAttribute != -1)
		{
			GLES20.glEnableVertexAttribArray(textureAttribute);
		}

	}

	/**
	 * Deactivate shader program.
	 */
	public void deactivate()
	{

		// Disable vertex attributes
		if (positionAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(positionAttribute);
		}

		if (normalAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(normalAttribute);
		}

		if (colorAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(colorAttribute);
		}

		if (textureAttribute != -1)
		{
			GLES20.glDisableVertexAttribArray(textureAttribute);
		}

		// Disable shader program
		super.disable();
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
	 * Get position attribute location.
	 * @return The position attribute location
	 */
	public int getPositionAttribute()
	{
		return positionAttribute;
	}

	/**
	 * Get normal attribute location.
	 * @return The normal attribute location
	 */
	public int getNormalAttribute()
	{
		return normalAttribute;
	}

	/**
	 * Get color attribute location.
	 * @return The color attribute location
	 */
	public int getColorAttribute()
	{
		return colorAttribute;
	}

	/**
	 * Get texture coordinate attribute location.
	 * @return The texture coordinate attribute location
	 */
	public int getTextureAttribute()
	{
		return textureAttribute;
	}

	/**
	 * Get texture sampler uniform location.
	 * @return The texture sampler uniform location
	 */
	public int getSamplerUniform()
	{
		return samplerUniform;
	}

}

package ge.framework.shader;

/**
 * Represents a basic shader program.
 */
public class BasicProgram extends GLES20Program
{

	/**
	 * Constructor.
	 */
	public BasicProgram()
	{
		// Call super constructor
		super
		(
            "uniform highp mat4 ModelViewProjectionMatrix;\n" +
            "attribute highp vec3 vPosition;\n" +
            "attribute lowp vec4 vColor;\n" +
            "attribute lowp vec2 vTexture;\n" +
            "varying lowp vec4 fColor;\n" +
			"varying lowp vec2 fTexture;\n" +
            "void main(void) {\n" +
            "\tfColor = vColor;\n" +
            "\tfTexture = vTexture;\n" +
            "\tgl_Position = ModelViewProjectionMatrix * vec4(vPosition, 1.0);\n" +
            "}"
			,
			"uniform sampler2D fTextureSampler;\n" +
			"varying lowp vec4 fColor;\n" +
			"varying lowp vec2 fTexture;\n" +
		    "void main(void) {\n" +
		    "\tvec4 color = fColor * texture2D(fTextureSampler, fTexture);\n" +
		    "\tif (color.a == 0.0) discard;" +
		    "\tgl_FragColor = color;\n" +
		    "}"
		);

	}

}

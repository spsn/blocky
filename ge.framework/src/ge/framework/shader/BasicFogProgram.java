package ge.framework.shader;

/**
 * Represents a basic shader program with fog.
 */
public class BasicFogProgram extends GLES20Program
{

	/**
	 * Constructor.
	 */
	public BasicFogProgram()
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
				"const vec4 fogcolor = vec4(0.615, 0.615, 1.0, 1.0);\n" +
				"const float fogdensity = 0.00001;\n" +
		    "void main(void) {\n" +
		    "\tvec4 color = fColor * texture2D(fTextureSampler, fTexture);\n" +
		    "\tif (color.a == 0.0) discard;" +
		    	"\tfloat z = gl_FragCoord.z / gl_FragCoord.w;\n" +
		    	"\tfloat fog = clamp(exp(-fogdensity * z * z), 0.05, 1.0);\n" +
			    "\tgl_FragColor = mix(fogcolor, color, fog);\n" +
		    "}"
		);

	}

}

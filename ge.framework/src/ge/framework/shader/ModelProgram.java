package ge.framework.shader;

/**
 * Represents a basic shader program.
 */
public class ModelProgram extends GLES20Program
{

	/**
	 * Constructor.
	 */
	public ModelProgram()
	{
		// Call super constructor
		super
		(
            "uniform highp mat4 ModelViewProjectionMatrix;\n" +
            "uniform highp vec3 mPosition;\n" +
            "uniform highp vec3 mRotation;\n" +
            "attribute highp vec3 vPosition;\n" +
            "attribute lowp vec4 vColor;\n" +
            "attribute lowp vec2 vTexture;\n" +
            "varying lowp vec4 fColor;\n" +
			"varying lowp vec2 fTexture;\n" +
            "void main(void) {\n" +
            "\tfloat cosx=cos(mRotation.x);\n" +
            "\tfloat sinx=sin(mRotation.x);\n" +
            "\tfloat cosy=cos(mRotation.y);\n" +
            "\tfloat siny=sin(mRotation.y);\n" +
            "\tfloat cosz=cos(mRotation.z);\n" +
            "\tfloat sinz=sin(mRotation.z);\n" +
            "\tmat4 position=mat4(1.0);\n" +
            "\tposition[3].x=mPosition.x;\n" +
            "\tposition[3].y=mPosition.y;\n" +
            "\tposition[3].z=mPosition.z;\n" +
            "\tmat4 yaw=mat4(1.0);\n" +
            "\tyaw[0][0]=cosy;\n" +
            "\tyaw[0][2]=-siny;\n" +
            "\tyaw[2][0]=siny;\n" +
            "\tyaw[2][2]=cosy;\n" +
            "\tmat4 pitch=mat4(1.0);\n" +
            "\tpitch[1][1]=cosx;\n" +
            "\tpitch[1][2]=sinx;\n" +
            "\tpitch[2][1]=-sinx;\n" +
            "\tpitch[2][2]=cosx;\n" +
            "\tmat4 roll=mat4(1.0);\n" +
            "\troll[0][0]=cosz;\n" +
            "\troll[0][1]=sinz;\n" +
            "\troll[1][0]=-sinz;\n" +
            "\troll[1][1]=cosz;\n" +
            "\tfColor = vColor;\n" +
            "\tfTexture = vTexture;\n" +
            "\tgl_Position = ModelViewProjectionMatrix * position * yaw * pitch * roll * vec4(vPosition, 1.0);\n" +
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


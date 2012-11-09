package ge.framework.profile;
import org.lwjgl.Sys;

/**
 * Represents a render profiler.
 */
public class Profiler
{
	// Items
	public static int CLEAR_BUFFER = 0;
	public static int UPDATE_MATRIX = 1;
	public static int CALCULATE_FRUSTUM = 2;
	public static int BIND_TEXTURE = 3;
	public static int ACTIVATE_PROGRAM = 4;
	public static int GET_MATRIX = 5;
	public static int SET_PROGRAM_VARIABLES = 6;
	public static int DEACTIVATE_PROGRAM = 7;
	public static int ITERATE_LOOP = 8;
	public static int BOX_IN_FRUSTUM = 9;
	public static int BIND_BUFFER = 10;
	public static int SET_ATTRIB_POINTER = 11;
	public static int DRAW_ELEMENTS = 12;
	public static int SWAP_BUFFERS = 13;
	public static int OTHER = 14;

	//TODO
	private long[] measures;

	//TODO
	private long lastTime;

	//TODO
	private boolean enabled = false;

	/**
	 * Constructor.
	 */
	public Profiler()
	{
		// Call super constructor
		super();

		//TODO
		measures = new long[15];
	}

	//TODO
	public void start()
	{
		//TODO
		lastTime = Sys.getTime();
		enabled = true;
	}

	//TODO
	public void measure(
		final int index)
	{
		// Local variables
		long currentTime;
		long timeDiff;

		if (enabled == true)
		{
			//TODO
			currentTime = Sys.getTime();

			//TODO
			timeDiff = currentTime - lastTime;

			//TODO
			lastTime = currentTime;

			//TODO
			measures[index] += timeDiff;
		}

	}

	//TODO
	public void display()
	{
		// Local variables
		long totalTime;

		if (enabled == true)
		{
			//TODO
			totalTime = 0;

			for (int i = 0; i < measures.length; i++)
			{
				totalTime += measures[i];
			}

			System.out.println("CLEAR_BUFFER          = " + (measures[CLEAR_BUFFER] * 100f) / totalTime + " % [" + measures[CLEAR_BUFFER] + "]");
			System.out.println("UPDATE_MATRIX         = " + (measures[UPDATE_MATRIX] * 100f) / totalTime + " % [" + measures[UPDATE_MATRIX] + "]");
			System.out.println("CALCULATE_FRUSTUM     = " + (measures[CALCULATE_FRUSTUM] * 100f) / totalTime + " % [" + measures[CALCULATE_FRUSTUM] + "]");
			System.out.println("BIND_TEXTURE          = " + (measures[BIND_TEXTURE] * 100f) / totalTime + " % [" + measures[BIND_TEXTURE] + "]");
			System.out.println("ACTIVATE_PROGRAM      = " + (measures[ACTIVATE_PROGRAM] * 100f) / totalTime + " % [" + measures[ACTIVATE_PROGRAM] + "]");
			System.out.println("GET_MATRIX            = " + (measures[GET_MATRIX] * 100f) / totalTime + " % [" + measures[GET_MATRIX] + "]");
			System.out.println("SET_PROGRAM_VARIABLES = " + (measures[SET_PROGRAM_VARIABLES] * 100f) / totalTime + " % [" + measures[SET_PROGRAM_VARIABLES] + "]");
			System.out.println("DEACTIVATE_PROGRAM    = " + (measures[DEACTIVATE_PROGRAM] * 100f) / totalTime + " % [" + measures[DEACTIVATE_PROGRAM] + "]");
			System.out.println("ITERATE_LOOP          = " + (measures[ITERATE_LOOP] * 100f) / totalTime + " % [" + measures[ITERATE_LOOP] + "]");
			System.out.println("BOX_IN_FRUSTUM        = " + (measures[BOX_IN_FRUSTUM] * 100f) / totalTime + " % [" + measures[BOX_IN_FRUSTUM] + "]");
			System.out.println("BIND_BUFFER           = " + (measures[BIND_BUFFER] * 100f) / totalTime + " % [" + measures[BIND_BUFFER] + "]");
			System.out.println("SET_ATTRIB_POINTER    = " + (measures[SET_ATTRIB_POINTER] * 100f) / totalTime + " % [" + measures[SET_ATTRIB_POINTER] + "]");
			System.out.println("DRAW_ELEMENTS         = " + (measures[DRAW_ELEMENTS] * 100f) / totalTime + " % [" + measures[DRAW_ELEMENTS] + "]");
			System.out.println("SWAP_BUFFERS          = " + (measures[SWAP_BUFFERS] * 100f) / totalTime + " % [" + measures[SWAP_BUFFERS] + "]");
			System.out.println("OTHER                 = " + (measures[OTHER] * 100f) / totalTime + " % [" + measures[OTHER] + "]");
		}

	}

}

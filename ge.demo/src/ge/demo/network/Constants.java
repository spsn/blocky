package ge.demo.network;

//TODO
public class Constants
{
	// Message identifiers
	public static final byte MESSAGE_ID_END_OF_MESSAGES		= 0;
	public static final byte MESSAGE_ID_PLAYER_ID			= 1;
	public static final byte MESSAGE_ID_WORLD_DATA			= 2;
	public static final byte MESSAGE_ID_PLAYER_POSITION		= 3;
	public static final byte MESSAGE_ID_PLAYER_REMOVE		= 4;
	public static final byte MESSAGE_ID_ACTOR_POSITION		= 5;
	public static final byte MESSAGE_ID_ACTOR_REMOVE		= 6;
	public static final byte MESSAGE_ID_CHANGED_BLOCK		= 7;

	// Message sizes
	public static final int MESSAGE_SIZE_PLAYER_ID			= 4;
	public static final int MESSAGE_SIZE_PLAYER_POSITION	= 28;
	public static final int MESSAGE_SIZE_PLAYER_REMOVE		= 4;
	public static final int MESSAGE_SIZE_ACTOR_POSITION		= 28;
	public static final int MESSAGE_SIZE_ACTOR_REMOVE		= 4;
	public static final int MESSAGE_SIZE_CHANGED_BLOCK		= 17;

	// Message size lookup
	public static final int[] MESSAGE_SIZE = new int[]
	{
		0,
		MESSAGE_SIZE_PLAYER_ID,
		0,
		MESSAGE_SIZE_PLAYER_POSITION,
		MESSAGE_SIZE_PLAYER_REMOVE,
		MESSAGE_SIZE_ACTOR_POSITION,
		MESSAGE_SIZE_ACTOR_REMOVE,
		MESSAGE_SIZE_CHANGED_BLOCK
	};

}

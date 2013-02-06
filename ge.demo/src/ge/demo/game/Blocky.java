package ge.demo.game;
import ge.demo.actor.Actor;
import ge.demo.actor.Box;
import ge.demo.actor.FlyingBlock;
import ge.demo.actor.MovingBlock;
import ge.demo.actor.PlayBlock;
import ge.demo.actor.Player;
import ge.demo.network.Client;
import ge.demo.network.Server;
import ge.demo.shape.Cube;
import ge.demo.shape.Deco;
import ge.demo.shape.DualWedgeA;
import ge.demo.shape.DualWedgeB;
import ge.demo.shape.HalfCube;
import ge.demo.shape.Platform;
import ge.demo.shape.Pole;
import ge.demo.shape.Shape;
import ge.demo.shape.Slate;
import ge.demo.shape.SunkenCube;
import ge.demo.shape.Wedge;
import ge.demo.terrain.Generator;
import ge.demo.terrain.MeshSet;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.material.Material;
import ge.framework.mesh.Mesh;
import ge.framework.overlay.CounterOverlay;
import ge.framework.overlay.ProgressBarOverlay;
import ge.framework.overlay.SpriteOverlay;
import ge.framework.overlay.SpriteSelectOverlay;
import ge.framework.render.Camera;
import ge.framework.render.Counters;
import ge.framework.render.GLES20Renderer;
import ge.framework.shader.BasicProgram;
import ge.framework.shader.GLES20Program;
import ge.framework.shader.ModelProgram;
import ge.framework.shader.NightFogProgram;
import ge.framework.shader.RainFogProgram;
import ge.framework.util.Color;
import ge.framework.util.Ray;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Blocky
{

	//TODO
	public class ActorThread extends java.lang.Thread
	{
		private Blocky implementation;

		public ActorThread(
			final Blocky implementation)
		{
			this.implementation = implementation;
		}

		public void run()
		{
			implementation.updateActors();
		}

	}

	//TODO
	public class UpdateThread extends java.lang.Thread
	{
		private Blocky implementation;

		public UpdateThread(
			final Blocky implementation)
		{
			this.implementation = implementation;
		}

		public void run()
		{
			implementation.updateFPS();
		}

	}

	public final int DEFAULT_WORLD_SIZE = 128;
	public final int DEFAULT_FIELD_OF_VIEW = 45;
	public final int DEFAULT_VIEWING_DISTANCE = DEFAULT_WORLD_SIZE;

	private Settings settings;

	private int xs = DEFAULT_WORLD_SIZE, ys = 128, zs = DEFAULT_WORLD_SIZE;
	private int xsh, ysh, zsh;
	private Camera camera;
	private SpriteSelectOverlay materialOverlay;
	private SpriteOverlay materialBorder;
	private CounterOverlay materialId;
	private SpriteOverlay targetOverlay;
	private CounterOverlay counterOverlay;
	private GLES20Renderer renderer;

	private Material[] materials;
	private Shape[] blocks;

	private java.io.RandomAccessFile worldFile;

	private byte[][][] space;
	private byte[][][] light;

	private Generator generator;
	private Player player;

	private float pheight = 1.7f;

	private float gspeed = 0.001f;

	private double lastFrame;
	private int fps;

	private Counters counters = new Counters();

	private float walkMaxSpeed = 0.005f;
	private float walkAcceleration = walkMaxSpeed / 25.0f;
	private float bfspeed = 0.0f;
	private float lrspeed = 0.0f;
	private boolean movef, moveb, movel, mover;

	private byte handblock = 1;

	private int environmenType;
	private byte worldLight;

	private FloatBuffer vertexBuffer;
	private ShortBuffer indexBuffer;

	private Server server;
	private Client client;

	private java.util.Map<Integer, Player> playerMap;
	private java.util.List<Vector4f> changedBlockList;
	private java.util.List<Vector4f> unsavedBlockList;
	private java.util.List<Actor> actorList;

	private Texture texture;
	private Texture playerTexture;

	private SpriteOverlay serverClosed;
	private float[] deltaSet;
	
	private Vector3f infectionBlockPos;
	private Vector3f plantBlockPos;

	public void start(
		final String[] argv)
	{
		// Local variables
		Color backgroundColor;
		GLES20Program opaqueProgram;
		GLES20Program modelProgram;
		GLES20Program transparentProgram;
		ActorThread actorThread;
		UpdateThread updateThread;
		long time;

		try
		{
			// Get settings
			settings = getSettings(argv);

			// Set world size
			xs = settings.getWorldSize();
			zs = xs;

			// Set environmenType
			environmenType = settings.getEnvironmentType();

			//TODO
			vertexBuffer = new FloatBuffer(16 * 16 * 16 * 6 * 4 * 12);
			indexBuffer = new ShortBuffer(16 * 16 * 16 * 6 * 4);

			playerMap = new java.util.HashMap<Integer, Player>();
			changedBlockList = new java.util.LinkedList<Vector4f>();
			unsavedBlockList = new java.util.LinkedList<Vector4f>();
			actorList = new java.util.ArrayList<Actor>();

			// Create camera
			camera = new Camera();
			camera.setFieldOfView(settings.getFieldOfView());
			camera.setViewingDistance(settings.getViewingDistance());

			// Create renderer
			renderer = new GLES20Renderer();
			renderer.setWaitForVsync(settings.getWaitForVsync());
			renderer.setBackgroundColor(new Color(0.6f, 0.6f, 1.0f, 1.0f));
			renderer.createDisplay();
			renderer.setCamera(camera);

			//TODO
			if (environmenType == 1)
			{
				// Create shader program for opaque meshes
				opaqueProgram = new NightFogProgram();

				// Create shader program for transparent meshes
				transparentProgram = new NightFogProgram();
			}
			else if (environmenType == 2)
			{
				// Create shader program for opaque meshes
				opaqueProgram = new RainFogProgram();

				// Create shader program for transparent meshes
				transparentProgram = new RainFogProgram();
			}
			else
			{
				// Create shader program for opaque meshes
				opaqueProgram = new BasicProgram();

				// Create shader program for transparent meshes
				transparentProgram = new BasicProgram();
			}

			//TODO
			modelProgram = new ModelProgram();

			//TODO
			renderer.setOpaqueProgram(opaqueProgram);
			renderer.setModelProgram(modelProgram);
			renderer.setTransparentProgram(transparentProgram);

			SpriteOverlay b = new SpriteOverlay(0.0f, 0.3f, 1.0f, 0.8f, getTexture("res/Blocky4.png"));
			renderer.addMesh(b.getMesh());

			SpriteOverlay o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/a.png"));
			renderer.addMesh(o.getMesh());

			SpriteOverlay s1 = new SpriteOverlay(0.0f, -0.2f, 1.0f, 0.03f, getTexture("res/barback.png"));
			renderer.addMesh(s1.getMesh());

			ProgressBarOverlay s2 = new ProgressBarOverlay(0.0f, -0.2f, 0.985f, 0.02f, getTexture("res/bar.png"));
			renderer.addMesh(s2.getMesh());

			s2.setValue(20, 100);
			renderer.renderOverlays();

			// Create materials
			createMaterials();

			// Create blocks
			createBlocks();

			s2.setValue(40, 100);
			renderer.renderOverlays();

			// Load texture
			//TODO
			texture = getTexture(settings.getTextureFileName());
			renderer.setTexture(texture);

			s2.setValue(60, 100);
			renderer.renderOverlays();

			//TODO
			materialOverlay = new SpriteSelectOverlay(-0.935f, -0.92f, 0.075f, 0.133f, texture);

			for (int i = 1; i < blocks.length; i++)
			{
				Material frontMaterial = blocks[i].getMaterials()[2];

				materialOverlay.addSprite(frontMaterial.getX1(), frontMaterial.getX2(), frontMaterial.getY1(), frontMaterial.getY2());
			}

			//TODO
			materialBorder = new SpriteOverlay(-0.935f, -0.92f, 0.08f, 0.142f, getTexture("res/border.png"));

			//TODO
			materialId = new CounterOverlay(-0.985f, -0.78f, 0.1f, 0.05f, getTexture("res/numbers1.png"));

			//TODO
			targetOverlay = new SpriteOverlay(0, 0, 0.045f, 0.075f, getTexture("res/spiral3.png"));

			s2.setValue(80, 100);
			renderer.renderOverlays();

			//TODO
			counterOverlay = new CounterOverlay(-0.985f, 0.98f, 0.1f, 0.05f, getTexture("res/numbers1.png"));

			s2.setValue(90, 100);
			renderer.renderOverlays();

			//TODO
			serverClosed = new SpriteOverlay(0.19f, 0.0f, 1.0f, 0.2f, getTexture("res/ServerClosed.png"));

			s2.setValue(100, 100);
			renderer.renderOverlays();

			renderer.removeMesh(o.getMesh());

			//TODO - sky

			//TODO
			if (settings.getServerAddress() != null)
			{

				if (settings.getServerAddress().equals("local") == true)
				{
					server = new Server(this, 50000);
				}
				else
				{
					client = new Client(this, settings.getServerAddress(), 50000);
				}

			}

			//TODO
			buildSpace(settings, client, s2);

			//TODO
			xsh = xs >> 1;
			ysh = ys >> 1;
			zsh = zs >> 1;

			//TODO
			if (settings.getServerAddress() != null)
			{

				if (settings.getServerAddress().equals("local") == true)
				{
					client = new Client(this, "localhost", 50000);
				}

			}

			//TODO
			renderer.addMesh(materialOverlay.getMesh());
			renderer.addMesh(materialBorder.getMesh());
			renderer.addMesh(materialId.getMesh());
			renderer.addMesh(targetOverlay.getMesh());
			renderer.addMesh(counterOverlay.getMesh());

			materialOverlay.start();
			materialId.setValue(handblock);
			counterOverlay.setValue(0);

			renderer.removeMesh(b.getMesh());
			renderer.removeMesh(s1.getMesh());
			renderer.removeMesh(s2.getMesh());

			//TODO
			if (environmenType == 1)
			{
				backgroundColor = new Color(0.0f, 0.0f, 0.01f, 1.0f);
			}
			else if (environmenType == 2)
			{
				backgroundColor = new Color(0.6f, 0.6f, 0.6f, 1.0f);
			}
			else
			{
				backgroundColor = new Color(0.6f, 0.6f, 1.0f, 1.0f);
			}

			renderer.setBackgroundColor(backgroundColor);

			// Set player starting position
			//TODO v
			for (int y = (ys - 1); y >= 0; y--)
			{
				//TODO
				byte block = space[xsh][y][zsh];

				if ((block != 0) && (block != 6) && (block != 14))
				{
					// Set player starting position
					camera.setPositionY(-pheight + (ysh - y) - 0.5f);

					break;
				}

			}
			//TODO ^

			//TODO
			playerTexture = getTexture(settings.getSkinFileName());

			//TODO
			player = new Player();
			player.setPosition(new Vector3f(-camera.getPositionX(), -(camera.getPositionY() + pheight + 0.5f), -camera.getPositionZ()));
			player.setRotation(new Vector3f(0, 0, 0));

			//TODO
			addPlayerMeshes(player);

			//TODO
			actorThread = new ActorThread(this);
			actorThread.setDaemon(true);
			actorThread.start();

			//TODO
			updateThread = new UpdateThread(this);
			updateThread.setDaemon(true);
			updateThread.start();
		}
		catch (java.lang.Exception exception)
		{
			exception.printStackTrace();
			System.exit(-1);
		}

		//TODO
		deltaSet = new float[settings.getRenderSmooth()];

		//TODO
		getDelta(); // call once before loop to initialise lastFrame

		time = System.currentTimeMillis();
		renderer.profiler.start();

		while (true)
		{
			//TODO
			generator.applyChangedMeshes(renderer);

			//TODO
			if (renderer.renderScene() == false)
			{
				break;
			}

			//TODO
			if ((client != null) && (client.isConnected() == false))
			{
				//TODO
				renderer.addMesh(serverClosed.getMesh());

				//TODO
				client = null;
			}

			//TODO
			if (update(getDelta()) == false)
			{
				break;
			}

			fps++;
		}

		//TODO
		try
		{

			if (worldFile != null)
			{
				saveChangedBlocks();
				worldFile.close();
			}

		}
		catch (java.lang.Exception exception)
		{
		}

//		System.out.println();
//		System.out.println("FPS                   = " + (fps * 1000f) / (System.currentTimeMillis() - time));
		renderer.profiler.display();

		System.exit(-1);
	}

	//TODO
	private Settings getSettings(
		final String[] argv) throws java.lang.Exception
	{
		// Local variables
		Settings settings;
		String[] components;
		int worldSize;

		// Default settings
		settings = new Settings();
		settings.setWorldSize(DEFAULT_WORLD_SIZE);
		settings.setEnvironmentType(0);
		settings.setTerrainType(0);
		settings.setFieldOfView(DEFAULT_FIELD_OF_VIEW);
		settings.setViewingDistance(DEFAULT_VIEWING_DISTANCE);
		settings.setWaitForVsync(true);
		settings.setBlockMode(0);
		settings.setWorldFileName("world.blocky");
		settings.setTextureFileName("res/terrain.png");
		settings.setSkinFileName("res/BlockyGuyPic.png");
		settings.setRenderSmooth(5);

		// Read settings from command line
		for (int i = 0; i < argv.length; i++)
		{
			components = argv[i].split("[=]");

			if (components.length == 2)
			{

				if ("size".equalsIgnoreCase(components[0]) == true)
				{

					try
					{
						worldSize = Integer.parseInt(components[1]);

						if ((worldSize % 16) != 0)
						{
							throw new java.lang.Exception("Must be multiple of 16.");
						}

					}
					catch (java.lang.Exception exception)
					{
						throw new java.lang.Exception("Invalid world size specified.  Must be multiple of 16.");
					}

					settings.setWorldSize(worldSize);
				}
				else if ("mood".equalsIgnoreCase(components[0]) == true)
				{

					if ("day".equalsIgnoreCase(components[1]) == true)
					{
						settings.setEnvironmentType(0);
					}
					else if ("night".equalsIgnoreCase(components[1]) == true)
					{
						settings.setEnvironmentType(1);
					}
					else if ("gloomy".equalsIgnoreCase(components[1]) == true)
					{
						settings.setEnvironmentType(2);
					}

				}
				else if ("terrain".equalsIgnoreCase(components[0]) == true)
				{

					if (components[1].startsWith("hills") == true)
					{
						settings.setTerrainType(0);
					}
					else if (components[1].startsWith("ponds") == true)
					{
						settings.setTerrainType(1);
					}
					else if (components[1].startsWith("flat") == true)
					{
						settings.setTerrainType(2);
					}

					if (components[1].endsWith("noveg") == true)
					{
						settings.setVegetationMode(1);
					}

				}
				else if ("fov".equalsIgnoreCase(components[0]) == true)
				{
					settings.setFieldOfView(Integer.parseInt(components[1]));
				}
				else if ("viewdist".equalsIgnoreCase(components[0]) == true)
				{
					settings.setViewingDistance(Integer.parseInt(components[1]));
				}
				else if ("vsync".equalsIgnoreCase(components[0]) == true)
				{
					settings.setWaitForVsync("no".equals(components[1]) == false);
				}
				else if ("block".equalsIgnoreCase(components[0]) == true)
				{

					if ("place".equalsIgnoreCase(components[1]) == true)
					{
						settings.setBlockMode(0);
					}
					else if ("move".equalsIgnoreCase(components[1]) == true)
					{
						settings.setBlockMode(1);
					}
					else if ("fly".equalsIgnoreCase(components[1]) == true)
					{
						settings.setBlockMode(2);
					}
					else if ("play".equalsIgnoreCase(components[1]) == true)
					{
						settings.setBlockMode(3);
					}

				}
				else if ("world".equalsIgnoreCase(components[0]) == true)
				{
					settings.setWorldFileName(components[1]);
				}
				else if ("texture".equalsIgnoreCase(components[0]) == true)
				{
					settings.setTextureFileName(components[1]);
				}
				else if ("skin".equalsIgnoreCase(components[0]) == true)
				{
					settings.setSkinFileName(components[1]);
				}
				else if ("server".equalsIgnoreCase(components[0]) == true)
				{
					settings.setServerAddress(components[1]);
				}
				else if ("xmov".equalsIgnoreCase(components[0]) == true)
				{
					settings.setXmov(Float.parseFloat(components[1]));
				}
				else if ("ymov".equalsIgnoreCase(components[0]) == true)
				{
					settings.setYmov(Float.parseFloat(components[1]));
				}
				else if ("zmov".equalsIgnoreCase(components[0]) == true)
				{
					settings.setZmov(Float.parseFloat(components[1]));
				}
				else if ("xrot".equalsIgnoreCase(components[0]) == true)
				{
					settings.setXrot(Float.parseFloat(components[1]));
				}
				else if ("yrot".equalsIgnoreCase(components[0]) == true)
				{
					settings.setYrot(Float.parseFloat(components[1]));
				}
				else if ("zrot".equalsIgnoreCase(components[0]) == true)
				{
					settings.setZrot(Float.parseFloat(components[1]));
				}
				else if ("xsin".equalsIgnoreCase(components[0]) == true)
				{
					settings.setXsin(Float.parseFloat(components[1]));
				}
				else if ("ysin".equalsIgnoreCase(components[0]) == true)
				{
					settings.setYsin(Float.parseFloat(components[1]));
				}
				else if ("zsin".equalsIgnoreCase(components[0]) == true)
				{
					settings.setZsin(Float.parseFloat(components[1]));
				}
				else if ("xamp".equalsIgnoreCase(components[0]) == true)
				{
					settings.setXamp(Float.parseFloat(components[1]));
				}
				else if ("yamp".equalsIgnoreCase(components[0]) == true)
				{
					settings.setYamp(Float.parseFloat(components[1]));
				}
				else if ("zamp".equalsIgnoreCase(components[0]) == true)
				{
					settings.setZamp(Float.parseFloat(components[1]));
				}
				else if ("rsf".equalsIgnoreCase(components[0]) == true)
				{
					settings.setRenderSmooth(Integer.parseInt(components[1]));
				}

			}

		}

		return settings;
	}

	//TODO
	private Texture getTexture(
		final String fileName) throws java.lang.Exception
	{
		// Local variables
		Texture texture;

		long st = System.currentTimeMillis();

		texture = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream(fileName));

		long et = System.currentTimeMillis();

		System.out.println("getTexture(" + fileName + ") = " + (et - st));

		return texture;
	}

	//TODO
	public void createMaterials2()
	{
		materials = new Material[34];
		// Grass
		materials[0] = new Material(8, 8, 0, 0);
		// Grass+dirt
		materials[1] = new Material(8, 8, 1, 0);
		// Dirt
		materials[2] = new Material(8, 8, 2, 0);
		// Rock
		materials[3] = new Material(8, 8, 3, 0);
		// Rock+snow
		materials[4] = new Material(8, 8, 4, 0);
		// Snow
		materials[5] = new Material(8, 8, 5, 0);
		// Water
		materials[6] = new Material(8, 8, 0, 1);
		// Log side
		materials[7] = new Material(8, 8, 1, 1);
		// Log T&B
		materials[8] = new Material(8, 8, 2, 1);
		// Chest front
		materials[9] = new Material(8, 8, 5, 1);
		// Chest side
		materials[10] = new Material(8, 8, 4, 1);
		// Chest T&B&B
		materials[11] = new Material(8, 8, 3, 1);
		// Leaves
		materials[12] = new Material(8, 8, 0, 2);
		// GoldenOak Log side
		materials[13] = new Material(8, 8, 1, 2);
		// GoldenOak Log T&B
		materials[14] = new Material(8, 8, 2, 2);
		// GoldenOak Leaves
		materials[15] = new Material(8, 8, 3, 2);
		// Wooden planks
		materials[16] = new Material(8, 8, 4, 2);
		// GoldenOak planks
		materials[17] = new Material(8, 8, 5, 2);
		// Cloud
		materials[18] = new Material(8, 8, 0, 3);
		// Sand
		materials[19] = new Material(8, 8, 1, 3);
		// ELog Side
		materials[20] = new Material(8, 8, 4, 3);
		// ELog T&B
		materials[21] = new Material(8, 8, 5, 3);
		// ELog leaves
		materials[22] = new Material(8, 8, 0, 4);
		// Birch
		materials[23] = new Material(8, 8, 6, 2);
		// EGrass side
		materials[24] = new Material(8, 8, 6, 0);
		// EGrass top
		materials[25] = new Material(8, 8, 6, 1);
		// Tall Grass
		materials[26] = new Material(8, 8, 0, 5);
		// Sapling
		materials[27] = new Material(8, 8, 1, 5);
		// Flower
		materials[28] = new Material(8, 8, 2, 5);
		// Rose
		materials[29] = new Material(8, 8, 3, 5);
		// Off Bit
		materials[30] = new Material(8, 8, 0, 6);
		// On Bit
		materials[31] = new Material(8, 8, 1, 6);
		// Coal Ore
		materials[32] = new Material(8, 8, 6, 7);
		// Torch
		materials[33] = new Material(8, 8, 5, 5);
	}

	//TODO
	public void createMaterials()
	{
		materials = new Material[53];
		// Grass
		materials[0] = new Material(16, 16, 0, 0);
		// Grass+dirt
		materials[1] = new Material(16, 16, 3, 0);
		// Dirt
		materials[2] = new Material(16, 16, 2, 0);
		// Rock
		materials[3] = new Material(16, 16, 1, 0);
		// Rock+snow
		materials[4] = new Material(16, 16, 1, 0);
		// Snow
		materials[5] = new Material(16, 16, 2, 4);
		// Water
		materials[6] = new Material(16, 16, 13, 12);
		// Log side
		materials[7] = new Material(16, 16, 4, 1);
		// Log T&B
		materials[8] = new Material(16, 16, 5, 1);
		// Chest front
		materials[9] = new Material(16, 16, 0, 10);
		// Chest side
		materials[10] = new Material(16, 16, 0, 10);
		// Chest T&B&B
		materials[11] = new Material(16, 16, 0, 10);
		// Leaves
		materials[12] = new Material(16, 16, 4, 3);
		// GoldenOak Log side
		materials[13] = new Material(16, 16, 2, 10);
		// GoldenOak Log T&B
		materials[14] = new Material(16, 16, 5, 1);
		// GoldenOak Leaves
		materials[15] = new Material(16, 16, 4, 12);
		// Wooden planks
		materials[16] = new Material(16, 16, 4, 0);
		// GoldenOak planks
		materials[17] = new Material(16, 16, 4, 0);
		// Cloud
		materials[18] = new Material(16, 16, 2, 4);
		// Sand
		materials[19] = new Material(16, 16, 2, 1);
		// ELog Side
		materials[20] = new Material(16, 16, 4, 1);
		// ELog T&B
		materials[21] = new Material(16, 16, 5, 1);
		// ELog leaves
		materials[22] = new Material(16, 16, 4, 3);
		// Birch
		materials[23] = new Material(16, 16, 5, 7);
		// EGrass side
		materials[24] = new Material(16, 16, 3, 0);
		// EGrass top
		materials[25] = new Material(16, 16, 0, 0);
		// Tall Grass
		materials[26] = new Material(16, 16, 7, 2);
		// Sapling
		materials[27] = new Material(16, 16, 15, 0);
		// Flower
		materials[28] = new Material(16, 16, 13, 0);
		// Rose
		materials[29] = new Material(16, 16, 12, 0);
		// Off Bit
		materials[30] = new Material(16, 16, 4, 3);
		// On Bit
		materials[31] = new Material(16, 16, 4, 3);
		// Coal Ore
		materials[32] = new Material(16, 16, 2, 2);
		// Torch
		materials[33] = new Material(16, 16, 0, 5);
		// Leaf
		materials[34] = new Material(16, 16, 4, 3);
		// Pumpkin front
		materials[35] = new Material(16, 16, 7, 7);
		// Pumpkin top
		materials[36] = new Material(16, 16, 6, 6);
		// Pumpkin sides
		materials[37] = new Material(16, 16, 6, 7);
		// Glass
		materials[38] = new Material(16, 16, 1, 3);
		// Dark wood planks
		materials[39] = new Material(16, 16, 6, 12);
		// Infection Core
		materials[40] = new Material(16, 16, 9, 2);
		// Mutant Plant Block
		materials[41] = new Material(16, 16, 9, 3);
		// Mutant Seed
		materials[42] = new Material(16, 16, 11, 1);
		// Infected Grass Top
		materials[43] = new Material(16, 16, 10, 1);
		// Infected Dirt
		materials[44] = new Material(16, 16, 10, 2);
		// Infected Grass Side
		materials[45] = new Material(16, 16, 10, 3);
		// Infected Stone
		materials[46] = new Material(16, 16, 8, 11);
		// Blue glass
		materials[47] = new Material(16, 16, 7, 14);
		// Green glass
		materials[48] = new Material(16, 16, 8, 14);
		// Red glass
		materials[49] = new Material(16, 16, 7, 13);
		// Yellow glass
		materials[50] = new Material(16, 16, 8, 13);
		// White glass
		materials[51] = new Material(16, 16, 9, 14);
		// Black glass
		materials[52] = new Material(16, 16, 9, 13);
	}

	//TODO
	public void createBlocks()
	{
		blocks = new Shape[61];
		// Air
		blocks[0] = null;
		// Grass+dirt
		blocks[1] = new Cube(new Material[] {materials[0], materials[2], materials[1], materials[1], materials[1], materials[1]}); // Top, Bottom, Front, Back, Left, Right
		// Dirt
		blocks[2] = new Cube(new Material[] {materials[2], materials[2], materials[2], materials[2], materials[2], materials[2]}); // Top, Bottom, Front, Back, Left, Right
		// Rock
		blocks[3] = new Cube(new Material[] {materials[3], materials[3], materials[3], materials[3], materials[3], materials[3]}); // Top, Bottom, Front, Back, Left, Right
		// Rock+snow
		blocks[4] = new Cube(new Material[] {materials[5], materials[3], materials[4], materials[4], materials[4], materials[4]}); // Top, Bottom, Front, Back, Left, Right
		// Snow
		blocks[5] = new Cube(new Material[] {materials[5], materials[5], materials[5], materials[5], materials[5], materials[5]}); // Top, Bottom, Front, Back, Left, Right
		// Water				
		blocks[6] = new SunkenCube(new Material[] {materials[6], materials[6], materials[6], materials[6], materials[6], materials[6]}); // Top, Bottom, Front, Back, Left, Right
		// Log				
		blocks[7] = new Cube(new Material[] {materials[8], materials[8], materials[7], materials[7], materials[7], materials[7]}); // Top, Bottom, Front, Back, Left, Right
		// Chest				
		blocks[8] = new Cube(new Material[] {materials[11], materials[11], materials[9], materials[11], materials[10], materials[10]}); // Top, Bottom, Front, Back, Left, Right
		// Leaves				
		blocks[9] = new Cube(new Material[] {materials[12], materials[12], materials[12], materials[12], materials[12], materials[12]}); // Top, Bottom, Front, Back, Left, Right
		// GoldenOak Log				
		blocks[10] = new Cube(new Material[] {materials[14], materials[14], materials[13], materials[13], materials[13], materials[13]}); // Top, Bottom, Front, Back, Left, Right
		// GoldenOak Leaves				
		blocks[11] = new Cube(new Material[] {materials[15], materials[15], materials[15], materials[15], materials[15], materials[15]}); // Top, Bottom, Front, Back, Left, Right
		// Wooden planks
		blocks[12] = new Cube(new Material[] {materials[16], materials[16], materials[16], materials[16], materials[16], materials[16]}); // Top, Bottom, Front, Back, Left, Right
		// GoldenOak planks
		blocks[13] = new Cube(new Material[] {materials[17], materials[17], materials[17], materials[17], materials[17], materials[17]}); // Top, Bottom, Front, Back, Left, Right
		// Cloud
		blocks[14] = new Cube(new Material[] {materials[18], materials[18], materials[18], materials[18], materials[18], materials[18]}); // Top, Bottom, Front, Back, Left, Right
		// Sand
		blocks[15] = new Cube(new Material[] {materials[19], materials[19], materials[19], materials[19], materials[19], materials[19]}); // Top, Bottom, Front, Back, Left, Right
		// ELog				
		blocks[16] = new Cube(new Material[] {materials[21], materials[21], materials[20], materials[20], materials[20], materials[20]}); // Top, Bottom, Front, Back, Left, Right
		// ELog	leaves			
		blocks[17] = new Cube(new Material[] {materials[22], materials[22], materials[22], materials[22], materials[22], materials[22]}); // Top, Bottom, Front, Back, Left, Right
		// Birch			
		blocks[18] = new Cube(new Material[] {materials[8], materials[8], materials[23], materials[23], materials[23], materials[23]}); // Top, Bottom, Front, Back, Left, Right
		// EGrass+Dirt
		blocks[19] = new Cube(new Material[] {materials[25], materials[2], materials[24], materials[24], materials[24], materials[24]}); // Top, Bottom, Front, Back, Left, Right
		// Tall Grass
		blocks[20] = new Deco(new Material[] {materials[26], materials[26], materials[26], materials[26], materials[26], materials[26]}); // Top, Bottom, Front, Back, Left, Right
		// Sapling
		blocks[21] = new Deco(new Material[] {materials[27], materials[27], materials[27], materials[27], materials[27], materials[27]}); // Top, Bottom, Front, Back, Left, Right
		// Flower
		blocks[22] = new Deco(new Material[] {materials[28], materials[28], materials[28], materials[28], materials[28], materials[28]}); // Top, Bottom, Front, Back, Left, Right
		// Rose
		blocks[23] = new Deco(new Material[] {materials[29], materials[29], materials[29], materials[29], materials[29], materials[29]}); // Top, Bottom, Front, Back, Left, Right
		// Off Bit
		blocks[24] = new Cube(new Material[] {materials[30], materials[30], materials[30], materials[30], materials[30], materials[30]}); // Top, Bottom, Front, Back, Left, Right
		// On Bit
		blocks[25] = new Cube(new Material[] {materials[31], materials[31], materials[31], materials[31], materials[31], materials[31]}); // Top, Bottom, Front, Back, Left, Right
		// Full Water
		blocks[26] = new Cube(new Material[] {materials[6], materials[6], materials[6], materials[6], materials[6], materials[6]}); // Top, Bottom, Front, Back, Left, Right
		// Half Water
		blocks[27] = new HalfCube(new Material[] {materials[6], materials[6], materials[6], materials[6], materials[6], materials[6]}); // Top, Bottom, Front, Back, Left, Right
		// Quarter Water
		blocks[28] = new Slate(new Material[] {materials[6], materials[6], materials[6], materials[6], materials[6], materials[6]}); // Top, Bottom, Front, Back, Left, Right
		// Coal ore
		blocks[29] = new Cube(new Material[] {materials[32], materials[32], materials[32], materials[32], materials[32], materials[32]}); // Top, Bottom, Front, Back, Left, Right
		// Torch
		blocks[30] = new Deco(new Material[] {materials[33], materials[33], materials[33], materials[33], materials[33], materials[33]}); // Top, Bottom, Front, Back, Left, Right
		// Leaf
		blocks[31] = new Deco(new Material[] {materials[34], materials[34], materials[34], materials[34], materials[34], materials[34]}); // Top, Bottom, Front, Back, Left, Right
		// Pumpkin
		blocks[32] = new Cube(new Material[] {materials[36], materials[37], materials[35], materials[37], materials[37], materials[37]}); // Top, Bottom, Front, Back, Left, Right
		// Glass
		blocks[33] = new Cube(new Material[] {materials[38], materials[38], materials[38], materials[38], materials[38], materials[38]}); // Top, Bottom, Front, Back, Left, Right
		// Wooden platform
		blocks[34] = new Platform(new Material[] {materials[16], materials[16], materials[16], materials[16], materials[16], materials[16]}); // Top, Bottom, Front, Back, Left, Right
		// Wooden pole
		blocks[35] = new Pole(new Material[] {materials[16], materials[16], materials[16], materials[16], materials[16], materials[16]}); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[36] = new Wedge(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 1); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[37] = new Wedge(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 2); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[38] = new Wedge(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 3); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[39] = new Wedge(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 4); // Top, Bottom, Front, Back, Left, Right
		// Dark wooden plank
		blocks[40] = new Cube(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[41] = new DualWedgeA(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 1); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[42] = new DualWedgeB(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 2); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[43] = new DualWedgeA(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 3); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge
		blocks[44] = new DualWedgeB(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 4); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge 2
		blocks[45] = new DualWedgeB(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 1); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge 2
		blocks[46] = new DualWedgeA(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 2); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge 2
		blocks[47] = new DualWedgeB(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 3); // Top, Bottom, Front, Back, Left, Right
		// Wooden wedge 2
		blocks[48] = new DualWedgeA(new Material[] {materials[39], materials[39], materials[39], materials[39], materials[39], materials[39]}, 4); // Top, Bottom, Front, Back, Left, Right
		// Infection Core
		blocks[49] = new Cube(new Material[] {materials[40], materials[40], materials[40], materials[40], materials[40], materials[40]}); // Top, Bottom, Front, Back, Left, Right
		// Mutant Plant Block
		blocks[50] = new Cube(new Material[] {materials[41], materials[41], materials[41], materials[41], materials[41], materials[41]}); // Top, Bottom, Front, Back, Left, Right
		// Mutant Seed
		blocks[51] = new Cube(new Material[] {materials[42], materials[42], materials[42], materials[42], materials[42], materials[42]}); // Top, Bottom, Front, Back, Left, Right
		// Infected Grass
		blocks[52] = new Cube(new Material[] {materials[43], materials[44], materials[45], materials[45], materials[45], materials[45]}); // Top, Bottom, Front, Back, Left, Right
		// Infected Dirt
		blocks[53] = new Cube(new Material[] {materials[44], materials[44], materials[44], materials[44], materials[44], materials[44]}); // Top, Bottom, Front, Back, Left, Right
		// Infected Stone
		blocks[54] = new Cube(new Material[] {materials[46], materials[46], materials[46], materials[46], materials[46], materials[46]}); // Top, Bottom, Front, Back, Left, Right
		// Blue glass
		blocks[55] = new Cube(new Material[] {materials[47], materials[47], materials[47], materials[47], materials[47], materials[47]}); // Top, Bottom, Front, Back, Left, Right
		// Green glass
		blocks[56] = new Cube(new Material[] {materials[48], materials[48], materials[48], materials[48], materials[48], materials[48]}); // Top, Bottom, Front, Back, Left, Right
		// Red class
		blocks[57] = new Cube(new Material[] {materials[49], materials[49], materials[49], materials[49], materials[49], materials[49]}); // Top, Bottom, Front, Back, Left, Right
		// Yellow class
		blocks[58] = new Cube(new Material[] {materials[50], materials[50], materials[50], materials[50], materials[50], materials[50]}); // Top, Bottom, Front, Back, Left, Right
		// White class
		blocks[59] = new Cube(new Material[] {materials[51], materials[51], materials[51], materials[51], materials[51], materials[51]}); // Top, Bottom, Front, Back, Left, Right
		// Black class
		blocks[60] = new Cube(new Material[] {materials[52], materials[52], materials[52], materials[52], materials[52], materials[52]}); // Top, Bottom, Front, Back, Left, Right
	}

	//TODO
	public void buildSpace(
		final Settings settings,
		final Client client,
		final ProgressBarOverlay s2) throws java.lang.Exception
	{
		// Local variables
		java.io.File file;

		//TODO
		SpriteOverlay o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/b.png"));
		renderer.addMesh(o.getMesh());

		//TODO
		if (client != null)
		{
			space = client.getWorldData(renderer, s2);
			xs = space.length;
			ys = space[0].length;
			zs = space[0][0].length;
			light = new byte[xs][ys][zs];

			generator = new Generator(xs, ys, zs, (float) Math.pow((xs / 128.0f), 2.0f) / 4.0f, space, light, blocks, counters);
		}
		else
		{
			//TODO - load or generate
			file = new java.io.File(settings.getWorldFileName());

			if (file.exists() == true)
			{
				loadWorld(file, s2);

				generator = new Generator(xs, ys, zs, (float) Math.pow((xs / 128.0f), 2.0f) / 4.0f, space, light, blocks, counters);
			}
			else
			{
				space = new byte[xs][ys][zs];
				light = new byte[xs][ys][zs];

				generator = new Generator(xs, ys, zs, (float) Math.pow((xs / 128.0f), 2.0f) / 4.0f, space, light, blocks, counters);

				generator.generateTerrain(settings, renderer, s2);

				saveWorld(file, s2);
			}

		}

		renderer.removeMesh(o.getMesh());

		o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/c.png"));
		renderer.addMesh(o.getMesh());

		if (environmenType == 1)
		{
			worldLight = 10;
		}
		else
		{
			worldLight = 127;
		}

		generator.setLighting(renderer, worldLight, s2);

		renderer.removeMesh(o.getMesh());

		o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/d.png"));
		renderer.addMesh(o.getMesh());

		generator.generateMeshes(renderer, s2);

		renderer.removeMesh(o.getMesh());
	}

	private void loadWorld(
		final java.io.File file,
		final ProgressBarOverlay s2) throws java.lang.Exception
	{
		//TODO
		worldFile = new java.io.RandomAccessFile(file, "rw");

		//TODO
		worldFile.seek(0);

		//TODO
		worldFile.readByte();
		xs = worldFile.readByte() << 4;
		zs = xs;
		settings.setWorldSize(xs);

		worldFile.readByte();
		worldFile.readByte();
		worldFile.readByte();
		worldFile.readByte();
		worldFile.readByte();
		worldFile.readByte();

		//TODO
		space = new byte[xs][ys][zs];
		light = new byte[xs][ys][zs];

		for (int x = 0; x < xs; x++)
		{

			for (int y = 0; y < ys; y++)
			{
				worldFile.read(space[x][y]);
			}

			if ((x & 7) == 0)
			{
				s2.setValue((x * 100) / xs, 100);
				renderer.renderOverlays();
			}

		}

	}

	private void saveWorld(
		final java.io.File file,
		final ProgressBarOverlay s2) throws java.lang.Exception
	{
		//TODO
		worldFile = new java.io.RandomAccessFile(file, "rw");

		//TODO
		worldFile.seek(0);

		//TODO
		worldFile.writeByte(1);
		worldFile.writeByte(xs >> 4);
		worldFile.writeByte(0);
		worldFile.writeByte(0);
		worldFile.writeByte(0);
		worldFile.writeByte(0);
		worldFile.writeByte(0);
		worldFile.writeByte(0);

		for (int x = 0; x < xs; x++)
		{

			for (int y = 0; y < ys; y++)
			{
				worldFile.write(space[x][y]);
			}

			if ((x & 7) == 0)
			{
				s2.setValue((x * 100) / xs, 100);
				renderer.renderOverlays();
			}

		}

	}

	private float smoothDelta(float newDelta)
	{
		//TODO
		int length;
		float total;

		//TODO
		length = deltaSet.length;
		total = 0;

		//TODO
		if (length > 1)
		{

			//TODO
			for (int i = 1; i < length; i++)
			{
				total += deltaSet[i];
				deltaSet[i - 1] = deltaSet[i];
			}

		}

		//TODO
		total += newDelta;
		deltaSet[length - 1] = newDelta;

		return total / length;
	}

	//TODO
//	private float lastDelta = 0;
	public boolean update(float newDelta)
	{
		//TODO
//		if (newDelta > 18)
//		System.out.println("----------------------------(+) " + newDelta);
//		if (newDelta < 15)
//		System.out.println("----------------------------(-) " + newDelta);
		//TODO
		if (newDelta <= 0) return true;
		if (newDelta < 16) newDelta = 16;
//		System.out.println("------------------------------> " + newDelta);
		//TODO
//		delta = 16;
//		float delta = (newDelta + lastDelta) / 2.0f;
//		lastDelta = newDelta;
		float delta = smoothDelta(newDelta);
		//TODO
//		System.out.println("--------------------------->==> " + delta);

		// Local variables
		float xa = camera.getPitch();
		float ya = camera.getYaw();
		float xp = camera.getPositionX();
		float yp = camera.getPositionY();
		float zp = camera.getPositionZ();

//		if (delta == 0) return true;

//    	float feet = yp + pheight;
//
//    	byte block = space[xsh - Math.round(xp)][ysh - Math.round(feet)][zsh - Math.round(zp)];
//
//		if (gspeed != 0)
//		{
//			gspeed += 0.0098f * 0.01f * delta;
//		}
//		else if ((block == 0) || (block == 6) || (block == 14) || (block == 26))
    	if ((Keyboard.getEventKey() == Keyboard.KEY_LSHIFT) && (Keyboard.getEventKeyState() == true) || ((Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) && (Keyboard.getEventKeyState() == true)))
		{
			gspeed += -0.098f * 0.0025f * delta;
		}
    	else
		{
			gspeed += 0.098f * 0.0025f * delta;
		}

	    while (Keyboard.next() == true)
	    {

	    	if ((Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) && (Keyboard.getEventKeyState() == true))
	    	{
				return false;
	        }

	    	//TODO - forward
			if ((Keyboard.getEventKey() == Keyboard.KEY_W) && (Keyboard.getEventKeyState() == true))
			{
		    	movef = true;
		    	moveb = false;
		    }

			if ((Keyboard.getEventKey() == Keyboard.KEY_W) && (Keyboard.getEventKeyState() == false))
			{
		    	movef = false;
		    }

	    	//TODO - backward
			if ((Keyboard.getEventKey() == Keyboard.KEY_S) && (Keyboard.getEventKeyState() == true))
			{
		    	moveb = true;
		    	movef = false;
		    }

			if ((Keyboard.getEventKey() == Keyboard.KEY_S) && (Keyboard.getEventKeyState() == false))
			{
		    	moveb = false;
		    }

	    	//TODO - left
			if ((Keyboard.getEventKey() == Keyboard.KEY_A) && (Keyboard.getEventKeyState() == true))
			{
		    	movel = true;
		    	mover = false;
		    }

			if ((Keyboard.getEventKey() == Keyboard.KEY_A) && (Keyboard.getEventKeyState() == false))
			{
		    	movel = false;
		    }

	    	//TODO - right
			if ((Keyboard.getEventKey() == Keyboard.KEY_D) && (Keyboard.getEventKeyState() == true))
			{
		    	mover = true;
		    	movel = false;
		    }

			if ((Keyboard.getEventKey() == Keyboard.KEY_D) && (Keyboard.getEventKeyState() == false))
			{
		    	mover = false;
		    }

			//TODO - selector
			if ((Keyboard.getEventKey() == Keyboard.KEY_1) && (Keyboard.getEventKeyState() == true))
	    	{
	    		materialOverlay.shiftDown();
				handblock = (byte) (materialOverlay.getIndex() + 1);
				materialId.setValue(handblock);
	        }

	    	if ((Keyboard.getEventKey() == Keyboard.KEY_2) && (Keyboard.getEventKeyState() == true))
	    	{
	    		materialOverlay.shiftUp();
				handblock = (byte) (materialOverlay.getIndex() + 1);
				materialId.setValue(handblock);
	        }

	    	//TODO
	    	//TODO
	    	if ((Keyboard.getEventKey() == Keyboard.KEY_7) && (Keyboard.getEventKeyState() == true))
	    	{
	    		deltaSet = new float[1];
	        }

	    	//TODO
	    	//TODO
	    	if ((Keyboard.getEventKey() == Keyboard.KEY_8) && (Keyboard.getEventKeyState() == true))
	    	{
	    		deltaSet = new float[2];
	        }

	    	//TODO
	    	//TODO
	    	if ((Keyboard.getEventKey() == Keyboard.KEY_9) && (Keyboard.getEventKeyState() == true))
	    	{
	    		deltaSet = new float[5];
	        }

	    	//TODO
	    	//TODO
	    	if ((Keyboard.getEventKey() == Keyboard.KEY_0) && (Keyboard.getEventKeyState() == true))
	    	{
	    		deltaSet = new float[10];
	        }

	    }

	    // Mouse rotation
		int mdx;
		int mdy;

		mdx = Mouse.getDX();
//		mdx = Mouse.getDX() + 10;
//		if (mdx != 10) System.out.println(mdx + " ******************************");
		mdy = Mouse.getDY();

		if (mdx != 0)
		{
			xa += (mdx * 0.1f * (delta / newDelta));
//			xa += (mdx * 0.1f);
//TODO			System.out.println("------------------------------> " + xa);
		}

		if (mdy != 0)
		{
			ya -= (mdy  * 0.1f * (delta / newDelta));
//			ya -= (mdy  * 0.1f);
		}

		if (xa > 180)
		{
			xa -= 360;
		}

		if (xa <= -180)
		{
			xa += 360;
		}

		if (ya < -90)
		{
			ya = -90;
		}

		if (ya > 90)
		{
			ya = 90;
		}

		// Keyboard movement
		boolean keymoved = false;

	    //TODO - forward
	    if (movef == true)
	    {
	    	bfspeed += walkAcceleration;
	    	bfspeed = (bfspeed > walkMaxSpeed) ? walkMaxSpeed: bfspeed;
		}
	    else if (bfspeed > 0)
	    {
	    	bfspeed -= walkAcceleration;
	    	bfspeed = (bfspeed < 0) ? 0 : bfspeed;
	    }

	    //TODO - backward
	    if (moveb == true)
	    {
	    	bfspeed -= walkAcceleration;
	    	bfspeed = (bfspeed < -walkMaxSpeed) ? -walkMaxSpeed: bfspeed;
		}
	    else if (bfspeed < 0)
	    {
	    	bfspeed += walkAcceleration;
	    	bfspeed = (bfspeed > 0) ? 0 : bfspeed;
	    }

	    //TODO forward/backward
	    if (bfspeed != 0)
	    {
	    	xp -= (float)Math.sin(Math.toRadians(xa)) * (bfspeed * delta);
	    	zp += (float)Math.cos(Math.toRadians(xa)) * (bfspeed * delta);

	    	keymoved = true;
	    }

	    //TODO - left
	    if (movel == true)
	    {
	    	lrspeed += walkAcceleration;
	    	lrspeed = (lrspeed > walkMaxSpeed) ? walkMaxSpeed: lrspeed;
		}
	    else if (lrspeed > 0)
	    {
	    	lrspeed -= walkAcceleration;
	    	lrspeed = (lrspeed < 0) ? 0 : lrspeed;
	    }

	    //TODO - right
	    if (mover == true)
	    {
	    	lrspeed -= walkAcceleration;
	    	lrspeed = (lrspeed < -walkMaxSpeed) ? -walkMaxSpeed: lrspeed;
		}
	    else if (lrspeed < 0)
	    {
	    	lrspeed += walkAcceleration;
	    	lrspeed = (lrspeed > 0) ? 0 : lrspeed;
	    }

	    //TODO left/right
	    if (lrspeed != 0)
	    {
	    	xp -= (float)Math.sin(Math.toRadians(xa - 90)) * (lrspeed * delta);
	    	zp += (float)Math.cos(Math.toRadians(xa - 90)) * (lrspeed * delta);

	    	keymoved = true;
	    }

	    //TODO - movement
		if (keymoved == true)
		{
	    	int bxp = Math.round(xp);
	    	int bzp = Math.round(zp);

	    	float feet = yp + pheight - 0.01f;

	    	byte blockeye = space[xsh - bxp][ysh - Math.round(yp)][zsh - bzp];
	    	byte blockfeet = space[xsh - bxp][ysh - Math.round(feet)][zsh - bzp];

			if (((blockeye != 0) && (blockeye != 6) && (blockeye != 14) && (blockeye != 20) && (blockeye != 21) && (blockeye != 22) && (blockeye != 23) && (blockeye != 26) && (blockeye != 27) && (blockeye != 28) && (blockeye != 30))
	    		|| ((blockfeet != 0) && (blockfeet != 6) && (blockfeet != 14) && (blockfeet != 20) && (blockfeet != 21) && (blockfeet != 22) && (blockfeet != 23) && (blockfeet != 26) && (blockfeet != 27) && (blockfeet != 28) && (blockfeet != 30)))
	    	{
	    		float nzp = Math.round(zp) - (0.51f * Math.signum(bzp - zp));

		    	int czp = Math.round(nzp);

		    	blockeye = space[xsh - bxp][ysh - Math.round(yp)][zsh - czp];
		    	blockfeet = space[xsh - bxp][ysh - Math.round(feet)][zsh - czp];

		    	if (((blockeye != 0) && (blockeye != 6) && (blockeye != 14) && (blockeye != 20) && (blockeye != 21) && (blockeye != 22) && (blockeye != 23) && (blockeye != 26) && (blockeye != 27) && (blockeye != 28) && (blockeye != 30))
		    		|| ((blockfeet != 0) && (blockfeet != 6) && (blockfeet != 14) && (blockfeet != 20) && (blockfeet != 21) && (blockfeet != 22) && (blockfeet != 23) && (blockfeet != 26) && (blockfeet != 27) && (blockfeet != 28) && (blockfeet != 30)))
		    	{
		    		float nxp = Math.round(xp) - (0.51f * Math.signum(bxp - xp));

		    		int cxp = Math.round(nxp);

			    	blockeye = space[xsh - cxp][ysh - Math.round(yp)][zsh - bzp];
			    	blockfeet = space[xsh - cxp][ysh - Math.round(feet)][zsh - bzp];

			    	if (((blockeye != 0) && (blockeye != 6) && (blockeye != 14) && (blockeye != 20) && (blockeye != 21) && (blockeye != 22) && (blockeye != 23) && (blockeye != 26) && (blockeye != 27) && (blockeye != 28) && (blockeye != 30))
			    		|| ((blockfeet != 0) && (blockfeet != 6) && (blockfeet != 14) && (blockfeet != 20) && (blockfeet != 21) && (blockfeet != 22) && (blockfeet != 23) && (blockfeet != 26) && (blockfeet != 27) && (blockfeet != 28) && (blockfeet != 30)))
			    	{
			    		xp = nxp;
			    		zp = nzp;
			    	}
			    	else
			    	{
			    		xp = nxp;
			    	}

		    	}
		    	else
		    	{
		    		zp = nzp;
		    	}

	    	}

		}

		// Bounds checks
		if (xp > xsh - 1)
		{
			xp = xsh - 1;
		}

		if (xp < -xsh + 1)
		{
			xp = -xsh + 1;
		}

		if (yp > ysh - 1)
		{
			yp = ysh - 1;
		}

		if (yp < -ysh + 1)
		{
			yp = -ysh + 1;
		}

		if (zp > zsh - 1)
		{
			zp = zsh - 1;
		}

		if (zp < -zsh + 1)
		{
			zp = -zsh + 1;
		}

		// Jump
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE) && (gspeed > 0))
		{
//	    	yp -= (0.01f * delta);
	    	gspeed = -0.098f;
		}

		// Gravity
		if ((Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) || (gspeed != 0))
		{
//	    	yp += (0.01f * delta);
	    	yp += gspeed;

	    	float feet = yp + pheight;

	    	byte block = space[xsh - Math.round(xp)][ysh - Math.round(feet)][zsh - Math.round(zp)];

	    	if ((block != 0) && (block != 6) && (block != 14) && (block != 20) && (block != 21) && (block != 22) && (block != 23)
	    		&& (block != 26) && (block != 27) && (block != 28) && (block != 30))
	    	{
	    		feet = Math.round(feet + 0.5f) - 0.5f;
	    		yp = feet - pheight;
	    		gspeed = 0;
	    	}

		}

		//TODO
		camera.setPitch(xa);
		camera.setYaw(ya);
		camera.setPositionX(xp);
		camera.setPositionY(yp);
		camera.setPositionZ(zp);

		//TODO
		if (client != null)
		{
			//TODO
			player.setPosition(new Vector3f(-xp, -(yp + pheight + 0.5f), -zp));
			player.setRotation(new Vector3f(ya, (180f - xa), 0));
		}

		//TODO
		while (Mouse.next() == true)
		{

			// Remove block
			if ((Mouse.getEventButton() == 0) && (Mouse.isButtonDown(0)))
			{
				//TODO
				Vector3f pb = getRemoveBlock();

				if (pb != null)
				{
					Vector4f cb = new Vector4f(pb.x, pb.y, pb.z, 0);

					//TODO
					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(pb, (byte) 0, true, worldLight);
				}

			}

			// Place block
			if ((handblock != 0) && (Mouse.getEventButton() == 1) && (Mouse.isButtonDown(1)))
			{
				//TODO
				Vector3f pb = getPlaceBlock();

				if (pb != null)
				{

					//TODO
					if ((settings.getBlockMode() == 1) || (settings.getBlockMode() == 2) || (settings.getBlockMode() == 3))
					{
						vertexBuffer.clear();
						indexBuffer.clear();
						Mesh m = new Mesh(Mesh.MeshType.MODEL, false, vertexBuffer, indexBuffer);

						blocks[handblock].draw(m, 0, 0, 0, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
							true, true, true, true, true, true,
							false, false, false, false);

						//TODO
						Actor actor;

						if (settings.getBlockMode() == 2)
						{
							actor = new FlyingBlock();
						}
						else if (settings.getBlockMode() == 3)
						{
							actor = new PlayBlock(settings.getXmov(), settings.getYmov(), settings.getZmov(),
								settings.getXrot(), settings.getYrot(), settings.getZrot(),
								settings.getXsin(), settings.getYsin(), settings.getZsin(),
								settings.getXamp(), settings.getYamp(), settings.getZamp());
						}
						else
						{
							actor = new MovingBlock();
						}

						actor.setPosition(new Vector3f(pb.x - xsh, pb.y - ysh, pb.z - zsh));
						actor.setRotation(new Vector3f(0, 0, 0));

						//TODO
						actor.setMesh(m);

						//TODO
						renderer.addMesh(m);

						//TODO
						synchronized (actorList)
						{
							actorList.add(actor);
						}

					}
					else
					{
						Vector4f cb = new Vector4f(pb.x, pb.y, pb.z, handblock);

						//TODO
						if (client != null)
						{
							//TODO
							changedBlockList.add(cb);
						}

						//TODO
						if ((client == null) || (server != null))
						{
							//TODO
							unsavedBlockList.add(cb);
						}

						//TODO
						generator.updateBlock(pb, handblock, true, worldLight);

						//TODO
						if (handblock == 49)
						{
							infectionBlockPos = pb;
						}

						if (handblock == 51)
						{
							plantBlockPos = pb;
						}
					}

				}

			}

		}

		return true;
	}

	//TODO
	private Vector3f getRemoveBlock()
	{
		// Local variables
		Ray ray;
		Vector3f rayPosition;
		Vector3f rayDirection;
		int x, y, z;
		byte block;

		// Get picking ray
		ray = renderer.pick(10);
		rayPosition = ray.getPosition();
		rayDirection = ray.getDirection();

		// Find remove block
		for (int i = 0; i < 10; i++)
		{
			// Get block under ray
			x = xsh + Math.round(rayPosition.x);
			y = ysh + Math.round(rayPosition.y);
			z = zsh + Math.round(rayPosition.z);

			if ((y < (ys - 1)) && (y > 0)
				&& (z < (zs - 1)) && (z > 0)
				&& (x < (xs - 1)) && (x > 0))
			{
		    	block = space[x][y][z];

		    	// Block defined?
		    	if ((block != 0) && (block != 6) && (block != 14) && (block != 26) && (block != 27) && (block != 28))
		    	{
			    	return new Vector3f(x, y, z);
		    	}

			}

	    	// Advance ray
	    	rayPosition.x -= rayDirection.x / 2.0f;
	    	rayPosition.y -= rayDirection.y / 2.0f;
	    	rayPosition.z -= rayDirection.z / 2.0f;
		}

		return null;
	}

	//TODO
	private Vector3f getPlaceBlock()
	{
		// Local variables
		Ray ray;
		Vector3f rayPosition;
		Vector3f rayDirection;
		Vector3f position;
		int x, y, z;
		byte block;

		// Get picking ray
		ray = renderer.pick(10);
		rayPosition = ray.getPosition();
		rayDirection = ray.getDirection();

		// Default no place block
		position = null;

		// Find place block
		for (int i = 0; i < 10; i++)
		{
			// Get block under ray
			x = xsh + Math.round(rayPosition.x);
			y = ysh + Math.round(rayPosition.y);
			z = zsh + Math.round(rayPosition.z);

			if ((y < (ys - 1)) && (y > 0)
				&& (z < (zs - 1)) && (z > 0)
				&& (x < (xs - 1)) && (x > 0))
			{
				block = space[x][y][z];

		    	// Block defined?
		    	if ((i > 3) && (block != 0) && (block != 6) && (block != 14) && (block != 26) && (block != 27) && (block != 28))
		    	{
			    	return position;
		    	}

		    	// Get place block
		    	position = new Vector3f(x, y, z);
			}

	    	// Advance ray
	    	rayPosition.x -= rayDirection.x / 2.0f;
	    	rayPosition.y -= rayDirection.y / 2.0f;
	    	rayPosition.z -= rayDirection.z / 2.0f;
		}

		return null;
	}

	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	private float getDelta()
	{
		double time = getTime();
		float delta = (float)(time - lastFrame);
	    lastFrame = time;

	    return delta;
	}

	/**
	 * Get the accurate system time
	 * 
	 * @return The system time in milliseconds
	 */
	private double getTime()
	{
	    return (Sys.getTime() * 1000d) / Sys.getTimerResolution();
	}

	/**
	 * Calculate the FPS and set it in the title bar
	 */
	private void updateFPS()
	{
		// Local variables
		double lastTime;
		long lastFps;
		double timeDiff;
		long fpsDiff;
		int infectionBlockDirection;
		int plantBlockDirection;
		byte blockType1;
		byte blockType2;

		lastTime = getTime();
		lastFps = fps;

		while (true)
		{
			timeDiff = getTime() - lastTime;
			fpsDiff = fps - lastFps;

//			System.out.println
//				("FPS = " + ((fpsDiff * 1000d) / timeDiff) + ", regions = " + counters.visRegionCount + "/" + counters.regionCount
//				+ ", blocks = " + counters.visBlockCount +  "/" + counters.blockCount
//				+ ", faces = " + counters.faceCount + ", memory = " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())
//				+ ", batches = " + renderer.counters.visBatchCount + "/" + counters.batchCount);

			//TODO
			counterOverlay.setValue((int) ((fpsDiff * 1000d) / timeDiff));

			//TODO
			if (plantBlockPos != null)
			{
				//TODO - Select Direction
				plantBlockDirection = (int)(Math.random() * 9);
				
				//TODO - Set New Pos
				if (plantBlockDirection == 0)
				{
					plantBlockPos.z += 1;
				}
				else if (plantBlockDirection == 1)
				{
					plantBlockPos.z -= 1;
				}
				else if (plantBlockDirection == 2)
				{
					plantBlockPos.x += 1;
				}
				else if (plantBlockDirection == 3)
				{
					plantBlockPos.x -= 1;
				}
				else if (plantBlockDirection == 4)
				{
					plantBlockPos.y += 1;
				}
				else if (plantBlockDirection == 5)
				{
					plantBlockPos.y -= 1;
				}
				else if (plantBlockDirection == 6)
				{
					plantBlockPos.y += 1;
				}
				else if (plantBlockDirection == 7)
				{
					plantBlockPos.y += 1;
				}
				else if (plantBlockDirection == 8)
				{
					plantBlockPos.y += 1;
				}

				//TODO - Get Block At New Pos
				blockType2 = space[(int)plantBlockPos.x][(int)plantBlockPos.y][(int)plantBlockPos.z];
				
				//TODO - If Not Air -> Change Block
				if (blockType2 == 0)
				{
					Vector4f cb = new Vector4f(plantBlockPos.x, plantBlockPos.y, plantBlockPos.z, 50);

					//TODO
					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(plantBlockPos, (byte)50, true, worldLight);
				}
				
			}
			
			//TODO
			if (infectionBlockPos != null)
			{
				//TODO - Select Direction
				infectionBlockDirection = (int)(Math.random() * 10);
				
				//TODO - Set New Pos
				if (infectionBlockDirection == 0)
				{
					infectionBlockPos.z += 1;
				}
				else if (infectionBlockDirection == 1)
				{
					infectionBlockPos.z -= 1;
				}
				else if (infectionBlockDirection == 2)
				{
					infectionBlockPos.x += 1;
				}
				else if (infectionBlockDirection == 3)
				{
					infectionBlockPos.x -= 1;
				}
				else if (infectionBlockDirection == 4)
				{
					infectionBlockPos.y += 1;
				}
				else if (infectionBlockDirection == 5)
				{
					infectionBlockPos.y -= 1;
				}
				else if (infectionBlockDirection == 6)
				{
					infectionBlockPos.z += 1;
					infectionBlockPos.x += 1;
				}
				else if (infectionBlockDirection == 7)
				{
					infectionBlockPos.z -= 1;
					infectionBlockPos.x -= 1;
				}
				else if (infectionBlockDirection == 8)
				{
					infectionBlockPos.z += 1;
					infectionBlockPos.x -= 1;
				}
				else if (infectionBlockDirection == 9)
				{
					infectionBlockPos.z -= 1;
					infectionBlockPos.x += 1;
				}

				//TODO - Get Block At New Pos
				blockType1 = space[(int)infectionBlockPos.x][(int)infectionBlockPos.y][(int)infectionBlockPos.z];
				
				//TODO - If Not Air -> Change Block
				if ((blockType1 == 3) || (blockType1 == 54))
				{
					Vector4f cb = new Vector4f(infectionBlockPos.x, infectionBlockPos.y, infectionBlockPos.z, 54);

					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(infectionBlockPos, (byte)54, true, worldLight);
				}

				else if ((blockType1 == 2) || (blockType1 == 53))
				{
					Vector4f cb = new Vector4f(infectionBlockPos.x, infectionBlockPos.y, infectionBlockPos.z, 53);

					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(infectionBlockPos, (byte)53, true, worldLight);
				}

				else if ((blockType1 == 1) || (blockType1 == 52))
				{
					Vector4f cb = new Vector4f(infectionBlockPos.x, infectionBlockPos.y, infectionBlockPos.z, 52);

					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(infectionBlockPos, (byte)52, true, worldLight);
				}

				else if (blockType1 != 0)
				{
					Vector4f cb = new Vector4f(infectionBlockPos.x, infectionBlockPos.y, infectionBlockPos.z, 49);

					//TODO
					if (client != null)
					{
						//TODO
						changedBlockList.add(cb);
					}

					//TODO
					if ((client == null) || (server != null))
					{
						//TODO
						unsavedBlockList.add(cb);
					}

					//TODO
					generator.updateBlock(infectionBlockPos, (byte)49, true, worldLight);
				}
				
			}

//			//TODO - picking
//			Ray ray = renderer.pick(5);
//			Vector3f rayPosition = ray.getPosition();
//			Vector3f rayDirection = ray.getDirection();
//
//			System.out.println("(" + rayPosition.x + ", " + rayPosition.y + ", " + rayPosition.z + ") -> (" + rayDirection.x + ", " + rayDirection.y + ", " + rayDirection.z + ")");
//
//			byte block = 0;
//
//			for (int i = 0; i < 10; i++)
//			{
//		    	block = space[xsh + Math.round(rayPosition.x)][ysh + Math.round(rayPosition.y)][zsh + Math.round(rayPosition.z)];
//
//		    	if (block != 0)
//		    	{
//		    		System.out.print(block + "[" + (xsh + Math.round(rayPosition.x)) + "," + (ysh + Math.round(rayPosition.y)) + "," + (zsh + Math.round(rayPosition.z)) + "],");
//
//			    	if ((block == 6) || (block == 14) || (block == 26))
//			    	{
//				    	System.out.print(" Can't Break!");
//			    	}
//			    	else
//			    	{
//				    	System.out.print(" Can Break!");
//			    	}
//
//			    	if ((block == 6) || (block == 14) || (block == 26))
//			    	{
//				    	System.out.print(" Can Scoup!");
//			    	}
//			    	else
//			    	{
//				    	System.out.print(" Can't Scoup!");
//			    	}
//
//			    	if ((block == 6) || (block == 14) || (block == 20) || (block == 26))
//			    	{
//				    	System.out.print(" Can't Collide!");
//			    	}
//			    	else
//			    	{
//				    	System.out.print(" Can Collide!");
//			    	}
//			    	
//			    	break;
//		    	}
//
//		    	rayPosition.x -= rayDirection.x;
//		    	rayPosition.y -= rayDirection.y;
//		    	rayPosition.z -= rayDirection.z;
//			}
//
//			if (block == 0)
//	    	{
//		    	System.out.print("No Block!");
//	    	}
//
//	    	System.out.println();
//			//TODO  - picking

			lastTime = getTime();
			lastFps = fps;

			//TODO
			if (worldFile != null)
			{
				saveChangedBlocks();
			}

			try
			{
				Thread.sleep(1000);
			}
			catch (java.lang.Exception exception)
			{
				exception.printStackTrace();
			}

		}

	}

	private void saveChangedBlocks()
	{

		//TODO
		while (unsavedBlockList.size() > 0)
		{
			//TODO
			Vector4f v = unsavedBlockList.remove(0);

			//TODO
			try
			{
				worldFile.seek(8 + ((int) v.x * ys * zs) + ((int) v.y * zs) + (int) v.z);
				worldFile.writeByte((byte) v.w);
			}
			catch (java.lang.Exception exception)
			{
			}

		}

	}

	//TODO
	public void updateActors()
	{
		// Local variables
		long last;
		long stime;
		long etime;
		long duration;
		Actor actor;
		long sleep;

		last = 0;
		stime = System.nanoTime();

		while (true)
		{
			etime = System.nanoTime();

			//TODO
//			duration = (etime - stime) / 1000000;
			duration = (((etime - stime) + last) >> 1) / 1000000;

			synchronized (actorList)
			{

				//TODO
				for (int i = 0; i < actorList.size(); i++)
				{
					actor = actorList.get(i);

					//TODO
					actor.act(duration);
				}

			}

			//TODO
			if (client != null)
			{

				//TODO
				try
				{
					client.sendPlayerData(player, changedBlockList);
				}
				catch (java.lang.Exception exception)
				{
					exception.printStackTrace();
				}

			}

			try
			{
				//TODO
				sleep = 30 - duration;
				sleep = (sleep < 0) ? 0 : sleep;
				Thread.sleep(sleep);
			}
			catch (java.lang.Exception exception)
			{
				exception.printStackTrace();
			}

			last = etime - stime;
			stime = etime;
		}

	}

	//TODO
	public byte[][][] getSpace()
	{
		return space;
	}

	//TODO
	public void addPlayerMeshes(
		final Player player1)
	{
		// Local variables
		Material[] materials;

		//TODO
		Mesh m = new Mesh(Mesh.MeshType.MODEL, false, new FloatBuffer(16 * 16 * 16 * 6 * 4 * 12), new ShortBuffer(16 * 16 * 16 * 6 * 4));

		try
		{
			m.setTexture(playerTexture);
		}
		catch (java.lang.Exception exception)
		{
			exception.printStackTrace();
		}

		//TODO - head
		materials = new Material[]
		{
			new Material(6 / 128f, 12 / 128f, 0 / 32f, 6 / 32f),
			new Material(12 / 128f, 18 / 128f, 0 / 32f, 6 / 32f),
			new Material(6 / 128f, 12 / 128f, 6 / 32f, 12 / 32f),
			new Material(18 / 128f, 24 / 128f, 6 / 32f, 12 / 32f),
			new Material(0 / 128f, 6 / 128f, 6 / 32f, 12 / 32f),
			new Material(12 / 128f, 18 / 128f, 6 / 32f, 12 / 32f)
		};

		//TODO
		Box.draw(m, 0.16875f, -0.16875f, 0.3375f, 0.0f, 0.16875f, -0.16875f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO
		player1.setHeadMesh(m);

		//TODO
		m = new Mesh(Mesh.MeshType.MODEL, false, new FloatBuffer(16 * 16 * 16 * 6 * 4 * 12), new ShortBuffer(16 * 16 * 16 * 6 * 4));

		try
		{
			m.setTexture(playerTexture);
		}
		catch (java.lang.Exception exception)
		{
			exception.printStackTrace();
		}

		//TODO - body
		materials = new Material[]
		{
			new Material(6 / 128f, 14 / 128f, 12 / 32f, 16 / 32f),
			new Material(14 / 128f, 22 / 128f, 12 / 32f, 16 / 32f),
			new Material(6 / 128f, 14 / 128f, 16 / 32f, 32 / 32f),
			new Material(18 / 128f, 26 / 128f, 16 / 32f, 32 / 32f),
			new Material(2 / 128f, 6 / 128f, 16 / 32f, 32 / 32f),
			new Material(14 / 128f, 18 / 128f, 16 / 32f, 32 / 32f)
		};

		//TODO
		Box.draw(m, 0.225f, -0.225f, 1.49375f, 0.7875f, 0.1125f, -0.1125f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO - left arm
		materials = new Material[]
		{
			new Material(36 / 128f, 40 / 128f, 14 / 32f, 18 / 32f),
			new Material(40 / 128f, 44 / 128f, 14 / 32f, 18 / 32f),
			new Material(36 / 128f, 40 / 128f, 18 / 32f, 32 / 32f),
			new Material(44 / 128f, 48 / 128f, 18 / 32f, 32 / 32f),
			new Material(32 / 128f, 36 / 128f, 18 / 32f, 32 / 32f),
			new Material(40 / 128f, 44 / 128f, 18 / 32f, 32 / 32f)
		};

		//TODO
		Box.draw(m, 0.45f - 0.05625f, 0.225f, 1.49375f, 0.70625f, 0.1125f - 0.05625f, -0.1125f + 0.05625f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO - right arm
		materials = new Material[]
		{
			new Material(52 / 128f, 56 / 128f, 14 / 32f, 18 / 32f),
			new Material(56 / 128f, 60 / 128f, 14 / 32f, 18 / 32f),
			new Material(52 / 128f, 56 / 128f, 18 / 32f, 32 / 32f),
			new Material(60 / 128f, 64 / 128f, 18 / 32f, 32 / 32f),
			new Material(48 / 128f, 52 / 128f, 18 / 32f, 32 / 32f),
			new Material(56 / 128f, 60 / 128f, 18 / 32f, 32 / 32f)
		};

		//TODO
		Box.draw(m, -0.225f, -0.45f + 0.05625f, 1.49375f, 0.70625f, 0.1125f - 0.05625f, -0.1125f + 0.05625f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO - left leg
		materials = new Material[]
		{
			new Material(84 / 128f, 88 / 128f, 14 / 32f, 18 / 32f),
			new Material(88 / 128f, 92 / 128f, 14 / 32f, 18 / 32f),
			new Material(84 / 128f, 88 / 128f, 18 / 32f, 32 / 32f),
			new Material(92 / 128f, 96 / 128f, 18 / 32f, 32 / 32f),
			new Material(80 / 128f, 84 / 128f, 18 / 32f, 32 / 32f),
			new Material(88 / 128f, 92 / 128f, 18 / 32f, 32 / 32f)
		};

		//TODO
		Box.draw(m, 0.225f, 0.0f + 0.005625f, 0.7875f, 0.0f, 0.1125f, -0.1125f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO - right leg
		materials = new Material[]
		{
			new Material(84 / 128f, 88 / 128f, 14 / 32f, 18 / 32f),
			new Material(88 / 128f, 92 / 128f, 14 / 32f, 18 / 32f),
			new Material(84 / 128f, 88 / 128f, 18 / 32f, 32 / 32f),
			new Material(92 / 128f, 96 / 128f, 18 / 32f, 32 / 32f),
			new Material(80 / 128f, 84 / 128f, 18 / 32f, 32 / 32f),
			new Material(88 / 128f, 92 / 128f, 18 / 32f, 32 / 32f)
		};

		//TODO
		Box.draw(m, 0.0f - 0.005625f, -0.225f, 0.7875f, 0.0f, 0.1125f, -0.1125f,
			materials, new float[] {127, 127, 127, 127, 127, 127, 127, 127},
			//top, bottom, front, back, left, right
			true, true, true, true, true, true);

		//TODO
		player1.setBodyMesh(m);
	}

	public void updatePlayerData(
		final int id,
		final Vector3f position,
		final Vector3f rotation)
	{
		// Local variables
		Player otherPlayer;
		MeshSet meshSet;
		java.util.List<Mesh> addMeshList;

		//TODO
		otherPlayer = playerMap.get(id);

		//TODO
		if (otherPlayer == null)
		{
			//TODO
			otherPlayer = new Player();
			otherPlayer.setId(id);
			otherPlayer.setPosition(position);
			otherPlayer.setRotation(rotation);

			//TODO
			otherPlayer.setHeadMesh(player.getHeadMesh().clone());

			//TODO
			otherPlayer.setBodyMesh(player.getBodyMesh().clone());

			//TODO
//			addPlayerMeshes(otherPlayer);

			//TODO
			playerMap.put(otherPlayer.getId(), otherPlayer);

			//TODO
			meshSet = new MeshSet();
			addMeshList = meshSet.getAddMeshList();

			//TODO
			addMeshList.add(otherPlayer.getHeadMesh());
			addMeshList.add(otherPlayer.getBodyMesh());

			//TODO
			generator.addChangedMeshes(meshSet);

			//TODO
			System.out.println("Player[" + id + "] added.");
		}
		else
		{
			//TODO
			otherPlayer.setPosition(position);
			otherPlayer.setRotation(rotation);
		}

	}

	public void removePlayer(
		final int id)
	{
		// Local variables
		Player otherPlayer;
		MeshSet meshSet;
		java.util.List<Mesh> removeMeshList;

		//TODO
		otherPlayer = playerMap.remove(id);

		//TODO
		if (otherPlayer != null)
		{
			//TODO
			meshSet = new MeshSet();
			removeMeshList = meshSet.getRemoveMeshList();

			//TODO
			removeMeshList.add(otherPlayer.getHeadMesh());
			removeMeshList.add(otherPlayer.getBodyMesh());

			//TODO
			generator.addChangedMeshes(meshSet);

			//TODO
			System.out.println("Player[" + id + "] removed.");
		}

	}

	public void updateBlock(
		final Vector3f position,
		final byte newBlock)
	{

		//TODO
		if (server != null)
		{
			//TODO
			unsavedBlockList.add(new Vector4f(position.x, position.y, position.z, newBlock));
		}

		//TODO
		generator.updateBlock(position, newBlock, false, worldLight);
	}

	public static void main(String[] argv)
	{
		Blocky blocky = new Blocky();
		blocky.start(argv);
	}

}

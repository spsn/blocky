package ge.demo.game;
import ge.demo.actor.Player;
import ge.demo.shape.Cube;
import ge.demo.shape.Deco;
import ge.demo.shape.HalfCube;
import ge.demo.shape.Shape;
import ge.demo.shape.Slate;
import ge.demo.shape.SunkenCube;
import ge.demo.terrain.Generator;
import ge.framework.material.Material;
import ge.framework.overlay.CounterOverlay;
import ge.framework.overlay.ProgressBarOverlay;
import ge.framework.overlay.SpriteOverlay;
import ge.framework.overlay.SpriteSelectOverlay;
import ge.framework.render.Camera;
import ge.framework.render.Counters;
import ge.framework.render.GLES20Renderer;
import ge.framework.shader.BasicProgram;
import ge.framework.shader.GLES20Program;
import ge.framework.shader.NightFogProgram;
import ge.framework.shader.RainFogProgram;
import ge.framework.util.Color;
import ge.framework.util.Ray;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Blocky
{

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

	private int xs = 128, ys = 128, zs = 128;
	private Camera camera;
	private SpriteSelectOverlay materialOverlay;
	private SpriteOverlay materialBorder;
	private SpriteOverlay targetOverlay;
	private CounterOverlay counterOverlay;
	private GLES20Renderer renderer;

	private Material[] materials;
	private Shape[] blocks;

	private byte[][][] space;
	private byte[][][] light;

	private Generator generator;
	private Player player;

	private float pheight = 1.7f;

	private float gspeed = 0.001f;

	private double lastFrame;
	private int fps;

	private Counters counters = new Counters();

	private float walkspeed = 0.005f;

	private byte handblock = 1;

	private byte worldLight;

	private int environmenType = 0;

	public void start(String[] argv)
	{
		// Local variables
		Color backgroundColor;
		GLES20Program opaqueProgram;
		GLES20Program transparentProgram;
		UpdateThread updateThread;
		long time;

		try
		{

			//TODO
			if ((argv.length > 0) && (argv[0].length() > 0))
			{
				xs = Integer.parseInt(argv[0]);
				zs = xs;
			}

			// Create camera
			camera = new Camera();
			camera.setFieldOfView(45.0f);
			camera.setViewingDistance(xs);

			// Set player starting position
			camera.setPositionY(-pheight - 30);

			// Create renderer
			renderer = new GLES20Renderer();
			renderer.setBackgroundColor(new Color(0.6f, 0.6f, 1.0f, 1.0f));
			renderer.createDisplay();
			renderer.setCamera(camera);

			//TODO
			if (environmenType == 1)
			{
				// Create shader program for opaque meshes
				opaqueProgram = new RainFogProgram();

				// Create shader program for transparent meshes
				transparentProgram = new RainFogProgram();
			}
			else if (environmenType == 2)
			{
				// Create shader program for opaque meshes
				opaqueProgram = new NightFogProgram();

				// Create shader program for transparent meshes
				transparentProgram = new NightFogProgram();
			}
			else
			{
				// Create shader program for opaque meshes
				opaqueProgram = new BasicProgram();

				// Create shader program for transparent meshes
				transparentProgram = new BasicProgram();
			}

			//TODO
			renderer.setOpaqueProgram(opaqueProgram);
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
			Texture texture;
//			texture = getTexture("res/TP8.png");
			texture = getTexture("res/terrain.png");
			renderer.setTexture(texture);

			s2.setValue(60, 100);
			renderer.renderOverlays();

			//TODO
			materialOverlay = new SpriteSelectOverlay(-0.954f, -0.92f, 0.075f, 0.133f, texture);

			for (int i = 1; i < blocks.length; i++)
			{
				Material frontMaterial = blocks[i].getMaterials()[2];

				materialOverlay.addSprite(frontMaterial.getX1(), frontMaterial.getX2(), frontMaterial.getY1(), frontMaterial.getY2());
			}

			//TODO
			materialBorder = new SpriteOverlay(-0.954f, -0.92f, 0.08f, 0.142f, getTexture("res/border.png"));

			//TODO
			targetOverlay = new SpriteOverlay(0, 0, 0.045f, 0.075f, getTexture("res/spiral3.png"));

			s2.setValue(80, 100);
			renderer.renderOverlays();

			//TODO
			counterOverlay = new CounterOverlay(-0.985f, 0.98f, 0.1f, 0.04f, getTexture("res/numbers1.png"));

			s2.setValue(100, 100);
			renderer.renderOverlays();

			renderer.removeMesh(o.getMesh());

			//TODO
			buildSpace(s2);

			//TODO
			renderer.addMesh(materialOverlay.getMesh());
			renderer.addMesh(materialBorder.getMesh());
			renderer.addMesh(targetOverlay.getMesh());
			renderer.addMesh(counterOverlay.getMesh());

			materialOverlay.start();
			counterOverlay.setValue(0);

			renderer.removeMesh(b.getMesh());
			renderer.removeMesh(s1.getMesh());
			renderer.removeMesh(s2.getMesh());

			//TODO
			if (environmenType == 1)
			{
				backgroundColor = new Color(0.6f, 0.6f, 0.6f, 1.0f);
			}
			else if (environmenType == 2)
			{
				backgroundColor = new Color(0.0f, 0.0f, 0.01f, 1.0f);
			}
			else
			{
				backgroundColor = new Color(0.6f, 0.6f, 1.0f, 1.0f);
			}

			renderer.setBackgroundColor(backgroundColor);

			//TODO
			player = new Player();

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
		getDelta(); // call once before loop to initialise lastFrame

		time = System.currentTimeMillis();
		renderer.profiler.start();

		while (true)
		{

			if (renderer.renderScene() == false)
			{
				break;
			}

			//TODO
			if (update(getDelta()) == false)
			{
				break;
			}

			fps++;

//			try
//			{
//				overlay.setText(" " + fps + "");
//			}
//			catch (java.lang.Exception exception)
//			{
//				exception.printStackTrace();
//			}

		}

//		System.out.println();
//		System.out.println("FPS                   = " + (fps * 1000f) / (System.currentTimeMillis() - time));
		renderer.profiler.display();

		System.exit(-1);
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
		materials = new Material[34];
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
	}

	//TODO
	public void createBlocks()
	{
		blocks = new Shape[31];
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
	}

	//TODO
	public void buildSpace(
		final ProgressBarOverlay s2) throws java.lang.Exception
	{
		//TODO
		space = new byte[xs][ys][zs];
		light = new byte[xs][ys][zs];

		generator = new Generator(xs, ys, zs, (float) Math.pow((xs / 128.0f), 2.0f) / 4.0f, space, light, blocks, counters);

		SpriteOverlay o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/b.png"));
		renderer.addMesh(o.getMesh());

		generator.generateTerrain(renderer, s2);

		try
		{
			Thread.sleep(250);
		}
		catch (java.lang.Exception exception)
		{
		}

		renderer.removeMesh(o.getMesh());

		o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/c.png"));
		renderer.addMesh(o.getMesh());

		if (environmenType == 2)
		{
			worldLight = 10;
		}
		else
		{
			worldLight = 127;
		}

		generator.setLighting(renderer, worldLight, s2);

		try
		{
			Thread.sleep(250);
		}
		catch (java.lang.Exception exception)
		{
		}

		renderer.removeMesh(o.getMesh());

		o = new SpriteOverlay(0.05f, -0.17f, 0.25f, 0.125f, getTexture("res/d.png"));
		renderer.addMesh(o.getMesh());

		generator.generateMeshes(renderer, s2);

		try
		{
			Thread.sleep(250);
		}
		catch (java.lang.Exception exception)
		{
		}

		renderer.removeMesh(o.getMesh());
	}

	//TODO
	private float[] pitchh = new float[10];
	private float[] yawh = new float[10];
	private float[] deltah = new float[10];

	//TODO
	//TODO - d = 1-exp(log(0.5)*springiness*time_d | xd * d, yd * d
	public boolean update(float delta)
	{
		// Local variables
		float xa = camera.getPitch();
		float ya = camera.getYaw();
		float xp = camera.getPositionX();
		float yp = camera.getPositionY();
		float zp = camera.getPositionZ();

		if (delta == 0) return true;

//    	float feet = yp + pheight;
//
//    	byte block = space[(xs / 2) - Math.round(xp)][(ys / 2) - Math.round(feet)][(zs / 2) - Math.round(zp)];
//
//		if (gspeed != 0)
//		{
//			gspeed += 0.0098f * 0.01f * delta;
//		}
//		else if ((block == 0) || (block == 6) || (block == 14) || (block == 26))
		{
			gspeed += 0.098f * 0.0025f * delta;
		}

	    while(Keyboard.next() == true)
	    {

	    	if ((Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) && (Keyboard.getEventKeyState() == true))
	    	{
				return false;
	        }

	    	if ((Keyboard.getEventKey() == Keyboard.KEY_1) && (Keyboard.getEventKeyState() == true))
	    	{
	    		materialOverlay.shiftDown();
				handblock = (byte) (materialOverlay.getIndex() + 1);
	        }

	    	if ((Keyboard.getEventKey() == Keyboard.KEY_2) && (Keyboard.getEventKeyState() == true))
	    	{
	    		materialOverlay.shiftUp();
				handblock = (byte) (materialOverlay.getIndex() + 1);
	        }

	    }

	    // Mouse rotation
		int mdx = Mouse.getDX();
		int mdy = Mouse.getDY();

		if (mdx != 0)
		{
			float totx = 0;
			float weight = 1.0f;

			for (int i = 8; i >= 0; i--)
			{
				pitchh[i + 1] = pitchh[i];
			}
			pitchh[0] = (mdx * 0.1f);
			for (int i = 0; i < 10; i++)
			{
				totx += (pitchh[i] * weight);
				weight *= 0.5f;
			}

			xa += (totx / 10f);
//			xa += (mdx * 0.05f);
		}

		if (mdy != 0)
		{
			float toty = 0;
			float weight = 1.0f;

			for (int i = 8; i >= 0; i--)
			{
				yawh[i + 1] = yawh[i];
			}
			yawh[0] = (mdy  * 0.1f);
			for (int i = 0; i < 10; i++)
			{
				toty += (yawh[i] * weight);
				weight *= 0.5f;
			}

			ya -= (toty / 10f);
//			ya -= (mdy  * 0.05f);
		}

//		if (mdx != 0)
//		{
//			oxa += (mdx * 0.05f);
//		}
//
//		if (mdy != 0)
//		{
//			oya += (mdy * 0.05f);
//		}
//
//		float fd = (45f / 1000f) * delta;
//
//		if (oxa > 0)
//		{
//			float fdd = (fd > oxa) ? oxa : fd;
//			oxa -= fdd;
//			xa += fdd;
//		}
//		else if (oxa < 0)
//		{
//			float fdd = (fd > -oxa) ? -oxa : fd;
//			oxa += fdd;
//			xa -= fdd;
//		}
//
//		if (oya > 0)
//		{
//			float fdd = (fd > oya) ? oya : fd;
//			oya -= fdd;
//			ya -= fdd;
//		}
//		else if (oya < 0)
//		{
//			float fdd = (fd > -oya) ? -oya : fd;
//			oya += fdd;
//			ya += fdd;
//		}

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

	    if (Keyboard.isKeyDown(Keyboard.KEY_A))
	    {
	    	xp -= (float)Math.sin(Math.toRadians(xa - 90)) * (walkspeed * delta);
	    	zp += (float)Math.cos(Math.toRadians(xa - 90)) * (walkspeed * delta);

	    	keymoved = true;
	    }

	    if (Keyboard.isKeyDown(Keyboard.KEY_D))
		{
			xp += (float)Math.sin(Math.toRadians(xa - 90)) * (walkspeed * delta);
			zp -= (float)Math.cos(Math.toRadians(xa - 90)) * (walkspeed * delta);

	    	keymoved = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_W))
		{
	    	xp -= (float)Math.sin(Math.toRadians(xa)) * (walkspeed * delta);
	    	zp += (float)Math.cos(Math.toRadians(xa)) * (walkspeed * delta);

	    	keymoved = true;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S))
		{
	    	xp += (float)Math.sin(Math.toRadians(xa)) * (walkspeed * delta);
	    	zp -= (float)Math.cos(Math.toRadians(xa)) * (walkspeed * delta);

	    	keymoved = true;
		}

		if (keymoved == true)
		{
	    	int bxp = Math.round(xp);
	    	int bzp = Math.round(zp);

	    	float feet = yp + pheight - 0.01f;

	    	byte blockeye = space[(xs / 2) - bxp][(ys / 2) - Math.round(yp)][(zs / 2) - bzp];
	    	byte blockfeet = space[(xs / 2) - bxp][(ys / 2) - Math.round(feet)][(zs / 2) - bzp];

			if (((blockeye != 0) && (blockeye != 6) && (blockeye != 14) && (blockeye != 20) && (blockeye != 21) && (blockeye != 22) && (blockeye != 23) && (blockeye != 26) && (blockeye != 27) && (blockeye != 28) && (blockeye != 30))
	    		|| ((blockfeet != 0) && (blockfeet != 6) && (blockfeet != 14) && (blockfeet != 20) && (blockfeet != 21) && (blockfeet != 22) && (blockfeet != 23) && (blockfeet != 26) && (blockfeet != 27) && (blockfeet != 28) && (blockfeet != 30)))
	    	{
	    		float nzp = Math.round(zp) - (0.51f * Math.signum(bzp - zp));

		    	int czp = Math.round(nzp);

		    	blockeye = space[(xs / 2) - bxp][(ys / 2) - Math.round(yp)][(zs / 2) - czp];
		    	blockfeet = space[(xs / 2) - bxp][(ys / 2) - Math.round(feet)][(zs / 2) - czp];

		    	if (((blockeye != 0) && (blockeye != 6) && (blockeye != 14) && (blockeye != 20) && (blockeye != 21) && (blockeye != 22) && (blockeye != 23) && (blockeye != 26) && (blockeye != 27) && (blockeye != 28) && (blockeye != 30))
		    		|| ((blockfeet != 0) && (blockfeet != 6) && (blockfeet != 14) && (blockfeet != 20) && (blockfeet != 21) && (blockfeet != 22) && (blockfeet != 23) && (blockfeet != 26) && (blockfeet != 27) && (blockfeet != 28) && (blockfeet != 30)))
		    	{
		    		float nxp = Math.round(xp) - (0.51f * Math.signum(bxp - xp));

		    		int cxp = Math.round(nxp);

			    	blockeye = space[(xs / 2) - cxp][(ys / 2) - Math.round(yp)][(zs / 2) - bzp];
			    	blockfeet = space[(xs / 2) - cxp][(ys / 2) - Math.round(feet)][(zs / 2) - bzp];

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
		if (xp > (xs / 2) - 1)
		{
			xp = (xs / 2) - 1;
		}

		if (xp < -(xs / 2) + 1)
		{
			xp = -(xs / 2) + 1;
		}

		if (yp > (ys / 2) - 1)
		{
			yp = (ys / 2) - 1;
		}

		if (yp < -(ys / 2) + 1)
		{
			yp = -(ys / 2) + 1;
		}

		if (zp > (zs / 2) - 1)
		{
			zp = (zs / 2) - 1;
		}

		if (zp < -(zs / 2) + 1)
		{
			zp = -(zs / 2) + 1;
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

	    	byte block = space[(xs / 2) - Math.round(xp)][(ys / 2) - Math.round(feet)][(zs / 2) - Math.round(zp)];

	    	if ((block != 0) && (block != 6) && (block != 14) && (block != 20) && (block != 21) && (block != 22) && (block != 23)
	    		&& (block != 26) && (block != 27) && (block != 28) && (block != 30))
	    	{
	    		feet = Math.round(feet + 0.5f) - 0.5f;
	    		yp = feet - pheight;
	    		gspeed = 0;
	    	}

		}
//		if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
//		{
//	    	yp += (0.01f * delta);
//
//	    	float feet = yp + pheight;
//
//	    	byte block = space[(xs / 2) - Math.round(xp)][(ys / 2) - Math.round(feet)][(zs / 2) - Math.round(zd)];
//
//	    	System.out.println(((xs / 2) - Math.round(xp)) + "," + ((ys / 2) - Math.round(feet)) + "," + ((zs / 2) - Math.round(zd)) + "," + block);
//	    	if ((block != 0) && (block != 6) && (block != 14) && (block != 26))
//	    	{
//	    		feet = Math.round(feet);
//	    		yp = feet - pheight;
//	    	}
//
//		}

		//TODO
		camera.setPitch(xa);
		camera.setYaw(ya);
		camera.setPositionX(xp);
		camera.setPositionY(yp);
		camera.setPositionZ(zp);

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
					generator.updateBlock(renderer, pb, (byte) 0, worldLight);
				}

			}

			// Place block
			if ((handblock != 0) && (Mouse.getEventButton() == 1) && (Mouse.isButtonDown(1)))
			{
				//TODO
				Vector3f pb = getPlaceBlock();

				if (pb != null)
				{
					generator.updateBlock(renderer, pb, handblock, worldLight);
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
			x = (xs / 2) + Math.round(rayPosition.x);
			y = (ys / 2) + Math.round(rayPosition.y);
			z = (zs / 2) + Math.round(rayPosition.z);

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
	    	rayPosition.x -= rayDirection.x / 2;
	    	rayPosition.y -= rayDirection.y / 2;
	    	rayPosition.z -= rayDirection.z / 2;
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
			x = (xs / 2) + Math.round(rayPosition.x);
			y = (ys / 2) + Math.round(rayPosition.y);
			z = (zs / 2) + Math.round(rayPosition.z);

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
	    	rayPosition.x -= rayDirection.x / 2;
	    	rayPosition.y -= rayDirection.y / 2;
	    	rayPosition.z -= rayDirection.z / 2;
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
//		    	block = space[(xs / 2) + Math.round(rayPosition.x)][(ys / 2) + Math.round(rayPosition.y)][(zs / 2) + Math.round(rayPosition.z)];
//
//		    	if (block != 0)
//		    	{
//		    		System.out.print(block + "[" + ((xs / 2) + Math.round(rayPosition.x)) + "," + ((ys / 2) + Math.round(rayPosition.y)) + "," + ((zs / 2) + Math.round(rayPosition.z)) + "],");
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

	public static void main(String[] argv)
	{
		Blocky blocky = new Blocky();
		blocky.start(argv);
	}

}

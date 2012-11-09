package ge.demo.terrain;
import org.lwjgl.util.vector.Vector3f;

import ge.demo.shape.Shape;
import ge.framework.buffer.FloatBuffer;
import ge.framework.buffer.ShortBuffer;
import ge.framework.mesh.Mesh;
import ge.framework.overlay.ProgressBarOverlay;
import ge.framework.render.Counters;
import ge.framework.render.Renderer;

public class Generator
{
	private int xs;
	private int ys;
	private int zs;
	private byte[][][] space;
	private byte[][][] light;
	private Shape[] blocks;

	private Region[][][] regions;
	private Counters counters;

	private FloatBuffer vertexBuffer1;
	private FloatBuffer vertexBuffer2;
	private ShortBuffer indexBuffer1;
	private ShortBuffer indexBuffer2;

	private float factor;

	/**
	 * Constructor.
	 */
	public Generator(
		final int xs,
		final int ys,
		final int zs,
		final float factor,
		final byte[][][] space,
		final byte[][][] light,
		final Shape[] blocks,
		final Counters counters)
	{
		this.xs = xs;
		this.ys = ys;
		this.zs = zs;
		this.factor = factor;
		this.space = space;
		this.light = light;
		this.blocks = blocks;
		this.counters = counters;

		vertexBuffer1 = new FloatBuffer(16 * 16 * 16 * 6 * 4 * 12);
		vertexBuffer2 = new FloatBuffer(16 * 16 * 16 * 6 * 4 * 12);
		indexBuffer1 = new ShortBuffer(16 * 16 * 16 * 6 * 4);
		indexBuffer2 = new ShortBuffer(16 * 16 * 16 * 6 * 4);
	}

	public void generateTerrain2()
	{
		generatePlane(80, 239, 80, 80, 80, 239);
		generatePlane(80, 239, 239, 239, 80, 239);

		generatePlane(80, 80, 80, 239, 80, 239);
		generatePlane(239, 239, 80, 239, 80, 239);
		generatePlane(80, 239, 80, 239, 80, 80);
		generatePlane(80, 239, 80, 239, 239, 239);
	}

	private void generatePlane(int x1, int x2, int y1, int y2, int z1, int z2)
	{

		for (int x = x1; x <= x2; x++)
		{

			for (int y = y1; y <= y2; y++)
			{

				for (int z = z1; z <= z2; z++)
				{
					space[x][y][z] = 1;
				}

			}

		}

	}

	public void generateTerrain(
		final Renderer renderer,
		final ProgressBarOverlay s2)
	{
		// Local variables
		int total = 15;
		int count = 0;
		int height;
		byte[][] space_x;
		byte block;

		long st = System.currentTimeMillis();
		long ct = st % 128;

		for (int x = 0; x < xs; x++)
		{

			for (int z = 0; z < zs; z++)
			{
				height = (int) (Noise.noise((float) Math.sin(Math.toRadians((ct + x + x + x) % 360)), (float) Math.cos(Math.toRadians((ct + z) % 360)), (float) 0) * 50)
				+ (int)((128 / 5) * 0.9) + 32;

				for (int y = 0; y < height; y++)
				{

					if (y > (height * 0.90f))
					{
						space[x][y][z] = 2;
					}
					else
					{
						space[x][y][z] = 3;
					}

				}

				if (space[x][height - 1][z] == 2)
				{
					space[x][height - 1][z] = 1;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Sand
		for (int x = 0; x < xs; x++)
		{
			space_x = space[x];

			for (int z = 0; z < zs; z++)
			{

				for (int y = 0; y < ys; y++)
				{

					if ((y <= 48) && (y > 3) && (space_x[y][z] == 0))
					{
						space_x[y][z] = 15;
						space_x[y - 1][z] = 15;
						space_x[y - 2][z] = 15;
						space_x[y - 3][z] = 15;
						space_x[y - 4][z] = 15;

						break;
					}

				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Water
		for (int x = 0; x < xs; x++)
		{

			for (int z = 0; z < zs; z++)
			{

				for (int y = 0; y < ys; y++)
				{

					if ((y < 48) && (space[x][y][z] == 0))
					{
						space[x][y][z] = 26;
					}
					else if ((y == 48) && (space[x][y][z] == 0))
					{
						space[x][y][z] = 6;
					}

				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Stone areas
		for (int s = 0; s < 0; s++)
		{
			int xsize = (int)(Math.random() * 46 + 5);
			int zsize = (int)(Math.random() * 46 + 5);

			int xsa = (int)(Math.random() * (xs - xsize)) + (xsize / 2);
			int zsa = (int)(Math.random() * (zs - zsize)) + (zsize / 2);

			// Stone area
			for (int x = (xsa - (xsize / 2)); x < (xsa + (xsize / 2)); x++)
			{

				for (int z = (zsa - (zsize / 2)); z < (zsa + (zsize / 2)); z++)
				{

					for (int y = 0; y < ys; y++)
					{

						if ((space[x][y][z] != 0) && (space[x][y][z] != 14) && (space[x][y][z] != 9) && (space[x][y][z] != 11) && (space[x][y][z] != 17) && (space[x][y][z] != 6) && (space[x][y][z] != 26))
						{
							space[x][y][z] = 19;
						}

					}

				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Tree
		for (int i = 0; i < 100f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{
					block = space[x][y - 1][z];

					if ((block == 6) || (block == 15) || (block == 3) || (block == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 3) + 4;

					if ((y + h) < (ys - 10))
					{

						for (int j = 0; j < h; j++)
						{
							space[x][y + j][z] = 7;
							
							addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))), (byte)9);
						}

					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Birch tree
		for (int i = 0; i < 50f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{
					block = space[x][y - 1][z];

					if ((block == 6) || (block == 15) || (block == 3) || (block == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 3) + 4;

					if ((y + h) < (ys - 10))
					{

						for (int j = 0; j < h; j++)
						{
							space[x][y + j][z] = 7;
							
							addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))), (byte)9);
						}

					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// ETree
		for (int i = 0; i < 0f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{

					if ((space[x][y - 1][z] == 6) || (space[x][y - 1][z] == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 3) + 4;

					if ((y + h) < (ys - 10))
					{

						for (int j = 0; j < h; j++)
						{
							space[x][y + j][z] = 16;
						
							addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))), (byte)17);
						}

					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// GoldenOak
		for (int i = 0; i < 50f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{
					block = space[x][y - 1][z];

					if ((block == 6) || (block == 15) || (block == 3) || (block == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 3) + 4;

					if ((y + h) < (ys - 10))
					{

						for (int j = 0; j < h; j++)
						{
							space[x][y + j][z] = 10;

							addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))), (byte)11);
						}

					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Tall tree
		for (int i = 0; i < 25f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{
					block = space[x][y - 1][z];

					if ((block == 6) || (block == 15) || (block == 3) || (block == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 5) + 10; //TODO

					if ((y + h) < (ys - 10))
					{

						for (int j = 0; j < h; j++)
						{
							space[x][y + j][z] = 18;
							space[x + 1][y + j][z] = 18;
							space[x][y + j][z + 1] = 18;
							space[x + 1][y + j][z + 1] = 18;
						
							if (j > (h - 10))
							{
								addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))) * 2, (byte)9);
							}

						}

					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Virtual Tree
		for (int i = 0; i < 10f * factor; i++)
		{
			int x = (int)(Math.random() * (xs - 10)) + 5;
			int z = (int)(Math.random() * (zs - 10)) + 5;
			
			for (int y = 1; y < ys; y++)
			{

				if (space[x][y][z] == 0)
				{

					if ((space[x][y - 1][z] == 6) || (space[x][y - 1][z] == 15) || (space[x][y - 1][z] == 3) || (space[x][y - 1][z] == 26))
					{
						break;
					}

					int h = (int)(Math.random() * 3) + 4;

					for (int j = 0; j < h; j++)
					{
						space[x][y + j][z] = 24;
						
						addLeaves(x, y + j + 2, z, (int)(4 - (2 * (j / (h * 1f)))), (byte)25);
					}

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Clouds
		for (int i = 0; i < 100f * factor; i++)
		{
			int x = (int)(Math.random() * xs);
			int z = (int)(Math.random() * zs);
			int y = (int)(Math.random() * (ys - 2 - 112)) + 112;

			int h = (int)(Math.random() * 2);
			int w = (int)(Math.random() * 50);
			int d = (int)(Math.random() * 50);

			for (int j = 0; j < w; j++)
			{

				if ((x + j) < xs)
				{

					for (int k = 0; k < h; k++)
					{

						if ((y + k) < ys)
						{

							for (int l = 0; l < d; l++)
							{

								if ((z + l) < zs)
								{
									space[x + j][y + k][z + l] = 14;							
								}

							}

						}

					}

				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Chests
		for (int i = 0; i < 50f * factor; i++)
		{
			int x = (int)(Math.random() * xs);
			int z = (int)(Math.random() * zs);
			
			for (int y = 1; y < ys; y++)
			{
				block = space[x][y][z];

				if ((block == 0) || (block == 6) || (block == 26))
				{
					space[x][y][z] = 8;					

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Tall Grass
		for (int i = 0; i < 10000f * factor; i++)
		{
			int x = (int)(Math.random() * xs);
			int z = (int)(Math.random() * zs);
			
			for (int y = 1; y < ys; y++)
			{

				if ((space[x][y][z] == 0) && (space[x][y - 1][z] == 1))
				{
					space[x][y][z] = 20;					

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Flower
		for (int i = 0; i < 250f * factor; i++)
		{
			int x = (int)(Math.random() * xs);
			int z = (int)(Math.random() * zs);
			
			for (int y = 1; y < ys; y++)
			{

				if ((space[x][y][z] == 0) && (space[x][y - 1][z] == 1))
				{
					space[x][y][z] = 22;					

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

		// Rose
		for (int i = 0; i < 250f * factor; i++)
		{
			int x = (int)(Math.random() * xs);
			int z = (int)(Math.random() * zs);
			
			for (int y = 1; y < ys; y++)
			{

				if ((space[x][y][z] == 0) && (space[x][y - 1][z] == 1))
				{
					space[x][y][z] = 23;					

					break;
				}

			}

		}

		//TODO
		s2.setValue((++count * 100) / total, 100);
		renderer.renderOverlays();

//		space[xs / 2][ys / 2 + 20][zs / 2] = 8;

		long et = System.currentTimeMillis();

		System.out.println("generateTerrain = " + (et - st));
	}

	private void addLeaves(
		final int x,
		final int y,
		final int z,
		final int size,
		final byte block)
	{

		for (int i = 0; i < size * 4; i++)
		{
			int w = (int)(Math.random() * (size - 1)) + 1;
			w *= (1 - ((int)(Math.random() * 2) * 2));
			int d = (int)(Math.random() * (size - 1)) + 1;
			d *= (1 - ((int)(Math.random() * 2) * 2));

			float wd = (w * -1f) / size;
			float dd = (d * -1f) / size;
			float xp = w;
			float zp = d;

			for (int j = 0; j < size; j++)
			{

				if (space[(int)(x + xp)][y][(int)(z + zp)] == 0)
				{
					space[(int)(x + xp)][y][(int)(z + zp)] = block;
				}

				xp += wd;
				zp += dd;
			}

		}

	}

	//TODO
	public void setLighting(
		final Renderer renderer,
		final byte worldLight,
		final ProgressBarOverlay s2)
	{
		long st = System.currentTimeMillis();

		for (int x = 0; x < xs; x++)
		{

			for (int z = 0; z < zs; z++)
			{
				setLighting(x, z, worldLight);
			}

			if ((x % 16) == 0)
			{
				s2.setValue(((x * 100) / xs), 100);
				renderer.renderOverlays();
			}

		}

		s2.setValue(100, 100);
		renderer.renderOverlays();

		long et = System.currentTimeMillis();

		System.out.println("setLighting = " + (et - st));
	}

	//TODO
	public void setLighting(
		final int x,
		final int z,
		final byte worldLight)
	{
		// Local variables
		byte level;
		byte block;

		level = worldLight;

		for (int yi = (ys - 1); yi >= 0; yi--)
		{
			light[x][yi][z] = level;

			block = space[x][yi][z];

			// Water
			if ((block == 6) || (block == 26))
			{

				if (level > 0)
				{
					level -= 16;
				}

			}
			// Leaves
			else if ((block == 9) || (block == 11) || (block == 17))
			{

				if (level > 16)
				{
					level -= 24;
				}

			}
			// Clouds
			else if (block == 14)
			{

				// Minimum
				if (level > 64)
				{
					level -= 32;
				}

			}
			// Decorations
			else if ((block == 20) || (block == 21) || (block == 22) || (block == 23) || (block == 30))
			{
			}
			else if (block != 0)
			{
				level = 8;
			}

		}

	}

	//TODO
	public void updateBlock(
		final Renderer renderer,
		final Vector3f position,
		final byte newBlock,
		final byte worldLight)
	{
		// Local variables
		int x, y, z;
		byte oldBlock;
		java.util.Set<Region> regionSet;
		java.util.Iterator<Region> iterator;
		Region region;

		//TODO
		x = (int) position.getX();
		y = (int) position.getY();
		z = (int) position.getZ();

		//TODO
		oldBlock = space[x][y][z];

		// Update block
		space[x][y][z] = newBlock;

		//TODO !!!!!!!
		if (newBlock == 0)
		{

			if ((space[x + 1][y][z] == 6)
				|| (space[x][y + 1][z] == 6))
			{
				space[x][y][z] = 26;
			}

			if ((space[x + 1][y][z] == 26)
				|| (space[x - 1][y][z] == 26)
				|| (space[x][y + 1][z] == 26)
				|| (space[x][y][z + 1] == 26)
				|| (space[x][y][z - 1] == 26))
			{
				space[x][y][z] = 26;
			}

			if ((space[x + 1][y][z] == 6)
				|| (space[x - 1][y][z] == 6)
				|| (space[x][y][z + 1] == 6)
				|| (space[x][y][z - 1] == 6))
			{
				space[x][y][z] = 27;
			}

			if ((space[x + 1][y][z] == 27)
				|| (space[x - 1][y][z] == 27)
				|| (space[x][y][z + 1] == 27)
				|| (space[x][y][z - 1] == 27))
			{
				space[x][y][z] = 28;
			}

			if ((space[x + 1][y][z] == 27)
				|| (space[x][y + 1][z] == 27))
			{
				space[x][y][z] = 26;
			}

			if ((space[x + 1][y][z] == 28)
				|| (space[x][y + 1][z] == 28))
			{
				space[x][y][z] = 26;
			}

		}

		//TODO !!!!!!!
		setLighting(x, z, worldLight);

		//TODO
		regionSet = new java.util.HashSet<Region>();

		//TODO
		getAffectedRegions(x, y, z, regionSet);

		//TODO
		if (newBlock == 30)
		{
			light[x][y][z] = (byte) Math.max(127, light[x][y][z]);

			if (space[x - 1][y][z] == 0)
			{
				light[x - 1][y][z] = (byte) Math.max(64, light[x - 1][y][z]);
				getAffectedRegions(x - 1, y, z, regionSet);
			}

			if (space[x + 1][y][z] == 0)
			{
				light[x + 1][y][z] = (byte) Math.max(64, light[x + 1][y][z]);
				getAffectedRegions(x + 1, y, z, regionSet);
			}

			if (space[x][y][z - 1] == 0)
			{
				light[x][y][z - 1] = (byte) Math.max(64, light[x][y][z - 1]);
				getAffectedRegions(x, y, z - 1, regionSet);
			}

			if (space[x][y][z + 1] == 0)
			{
				light[x][y][z + 1] = (byte) Math.max(64, light[x][y][z + 1]);
				getAffectedRegions(x, y, z + 1, regionSet);
			}

		}

		if (oldBlock == 30)
		{

			if (space[x - 1][y][z] == 0)
			{
				light[x - 1][y][z] = (byte) Math.max(worldLight, light[x - 1][y][z] - 64);
				getAffectedRegions(x - 1, y, z, regionSet);
			}

			if (space[x + 1][y][z] == 0)
			{
				light[x + 1][y][z] = (byte) Math.max(worldLight, light[x + 1][y][z] - 64);
				getAffectedRegions(x + 1, y, z, regionSet);
			}

			if (space[x][y][z - 1] == 0)
			{
				light[x][y][z - 1] = (byte) Math.max(worldLight, light[x][y][z - 1] - 64);
				getAffectedRegions(x, y, z - 1, regionSet);
			}

			if (space[x][y][z + 1] == 0)
			{
				light[x][y][z + 1] = (byte) Math.max(worldLight, light[x][y][z + 1] - 64);
				getAffectedRegions(x, y, z + 1, regionSet);
			}

		}

		for (iterator = regionSet.iterator(); iterator.hasNext() == true;)
		{
			region = iterator.next();

			//TODO
			updateRegion(renderer, region);
		}

	}

	private void getAffectedRegions(
		final int x,
		final int y,
		final int z,
		final java.util.Set<Region> regionSet)
	{
		// Local variables
		int rx, ry, rz;
		int ex, ey, ez;

		//TODO
		rx = x / 16;
		ry = y / 16;
		rz = z / 16;

		ex = x % 16;
		ey = y % 16;
		ez = z % 16;

		//TODO
		regionSet.add(getRegion(rx, ry, rz));

		//TODO
		if (ex == 0)
		{
			//TODO
			regionSet.add(getRegion(rx - 1, ry, rz));
		}

		//TODO
		if (ex == 15)
		{
			//TODO
			regionSet.add(getRegion(rx + 1, ry, rz));
		}

		//TODO
		if (ey == 0)
		{
			//TODO
			regionSet.add(getRegion(rx, ry - 1, rz));
		}

		//TODO
		if (ey == 15)
		{
			//TODO
			regionSet.add(getRegion(rx, ry + 1, rz));
		}

		//TODO
		if (ez == 0)
		{
			//TODO
			regionSet.add(getRegion(rx, ry, rz - 1));
		}

		//TODO
		if (ez == 15)
		{
			//TODO
			regionSet.add(getRegion(rx, ry, rz + 1));
		}

	}

	private void updateRegion(
		final Renderer renderer,
		final Region region)
	{
		// Remove meshes for region
		renderer.removeMesh(region.getMesh1());
		renderer.removeMesh(region.getMesh2());

		// Generate new meshes for region
		generateMeshes(region);

		// Add meshes for region
		if (region.getMesh1().getVertexCount() > 0)
		{
			renderer.addMesh(region.getMesh1());
		}

		if (region.getMesh2().getVertexCount() > 0)
		{
			renderer.addMesh(region.getMesh2());
		}

	}

	private Region getRegion(
		final int x,
		final int y,
		final int z)
	{
		// Local variables
		Region region;

		// Get region
		region = regions[x][y][z];

		return region;
	}

	private Region getRegionFromBlock(
		final int x,
		final int y,
		final int z)
	{
		// Local variables
		Region region;

		// Get region
		region = regions[x / 16][y / 16][z / 16];

		return region;
	}

	public void generateMeshes(
		final Renderer renderer,
		final ProgressBarOverlay s2)
	{
		// Local variables
		int x1, x2, y1, y2, z1, z2;
		Region region;

		long st = System.currentTimeMillis();

		regions = new Region[xs / 16][ys / 16][zs / 16];
		int total = (xs / 16) * (ys / 16) * (zs / 16);
		int count = 0;

		for (int i = 0; i < (xs / 16); i++)
		{
			x1 = (i * 16);
			x2 = (x1 + 16);

			for (int j = 0; j < (ys / 16); j++)
			{
				y1 = (j * 16);
				y2 = (y1 + 16);

				for (int k = 0; k < (zs / 16); k++)
				{
					counters.regionCount++;

					z1 = (k * 16);
					z2 = (z1 + 16);

					region = new Region(x1, x2, y1, y2, z1, z2);

					regions[i][j][k] = region;

					//TODO - gen !!!
					generateMeshes(region);

					//TODO
					if (region.isVisible() == true)
					{

						//TODO
						if (region.getMesh1().getVertexCount() > 0)
						{
							renderer.addMesh(region.getMesh1());
							counters.batchCount++;
						}

						if (region.getMesh2().getVertexCount() > 0)
						{
							renderer.addMesh(region.getMesh2());
							counters.batchCount++;
						}

						//TODO
						counters.visRegionCount++;
					}

				}

				//TODO
				count += (zs / 16);
				s2.setValue(((count * 100) / total), 100);
				renderer.renderOverlays();
			}

		}

		long et = System.currentTimeMillis();

		System.out.println("generateMeshes = " + (et - st));
	}

	public void generateMeshes(
		final Region region)
	{
		// Local variables
		Mesh mesh1;
		Mesh mesh2;
		Mesh mesh;
		int xsh, ysh, zsh;

		byte[][] space_x;
		byte[] space_x_y;
		byte space_x_y_z;

		byte count;

		boolean top;
		boolean bottom;
		boolean front;
		boolean back;
		boolean left;
		boolean right;

		float[] lights;

		float light1;
		float light2;
		float light3;
		float light4;
		float light5;
		float light6;
		float light7;
		float light8;

		byte l_x_y_z;
		byte l_xp_y_z;
		byte l_xp_y_zm;
		byte l_x_y_zm;
		byte l_x_yp_z;
		byte l_xp_yp_z;
		byte l_xp_yp_zm;
		byte l_x_yp_zm;
		byte l_xm_y_z;
		byte l_xm_y_zm;
		byte l_xm_yp_z;
		byte l_xm_yp_zm;
		byte l_xm_y_zp;
		byte l_x_y_zp;
		byte l_xm_yp_zp;
		byte l_x_yp_zp;
		byte l_xp_y_zp;
		byte l_xp_yp_zp;
		byte l_x_ym_z;
		byte l_xp_ym_z;
		byte l_xp_ym_zm;
		byte l_x_ym_zm;
		byte l_xm_ym_z;
		byte l_xm_ym_zm;
		byte l_xm_ym_zp;
		byte l_x_ym_zp;
		byte l_xp_ym_zp;

		xsh = (xs / 2);
		ysh = (ys / 2);
		zsh = (zs / 2);

		vertexBuffer1.clear();
		vertexBuffer2.clear();
		indexBuffer1.clear();
		indexBuffer2.clear();

		mesh1 = new Mesh(Mesh.MeshType.OPAQUE, false, vertexBuffer1, indexBuffer1);
		region.setMesh1(mesh1);
		mesh2 = new Mesh(Mesh.MeshType.TRANSPARENT, false, vertexBuffer2, indexBuffer2);
		region.setMesh2(mesh2);

		for (int x = region.getX1(); x < region.getX2(); x++)
		{
			space_x = space[x];

			for (int y = region.getY1(); y < region.getY2(); y++)
			{
				space_x_y = space_x[y];

				for (int z = region.getZ1(); z < region.getZ2(); z++)
				{
					counters.blockCount++;

					space_x_y_z = space_x_y[z];

					if (space_x_y_z != 0)
					{
						top = ((y < (ys - 1)) && (hasFace(space_x_y_z, getSpace(x, y + 1, z)) == true));
						bottom = ((y > 0) && (hasFace(space_x_y_z, getSpace(x, y - 1, z)) == true));
						front = ((z < (zs - 1)) && (hasFace(space_x_y_z, getSpace(x, y, z + 1)) == true));
						back = ((z > 0) && (hasFace(space_x_y_z, getSpace(x, y, z - 1)) == true));
						left = ((x > 0) && (hasFace(space_x_y_z, getSpace(x - 1, y, z)) == true));
						right = ((x < (xs - 1)) && (hasFace(space_x_y_z, getSpace(x + 1, y, z)) == true));

						if ((top == true) || (bottom == true) || (front == true) || (back == true) || (left == true) || (right == true))
						{

							if ((y < (ys - 1)) && (y > 0)
								&& (z < (zs - 1)) && (z > 0)
								&& (x < (xs - 1)) && (x > 0))
							{
								light1 = 1.0f;
								light2 = 1.0f;
								light3 = 1.0f;
								light4 = 1.0f;
								light5 = 1.0f;
								light6 = 1.0f;
								light7 = 1.0f;
								light8 = 1.0f;

								l_x_y_z = light[x][y][z];
								l_xp_y_z = 0;
								l_xp_y_zm = 0;
								l_x_y_zm = 0;
								l_x_yp_z = 0;
								l_xp_yp_z = 0;
								l_xp_yp_zm = 0;
								l_x_yp_zm = 0;
								l_xm_y_z = 0;
								l_xm_y_zm = 0;
								l_xm_yp_z = 0;
								l_xm_yp_zm = 0;
								l_xm_y_zp = 0;
								l_x_y_zp = 0;
								l_xm_yp_zp = 0;
								l_x_yp_zp = 0;
								l_xp_y_zp = 0;
								l_xp_yp_zp = 0;
								l_x_ym_z = 0;
								l_xp_ym_z = 0;
								l_xp_ym_zm = 0;
								l_x_ym_zm = 0;
								l_xm_ym_z = 0;
								l_xm_ym_zm = 0;
								l_xm_ym_zp = 0;
								l_x_ym_zp = 0;
								l_xp_ym_zp = 0;

								if ((top == true) || (bottom == true) || (front == true) || (back == true) || (right == true))
									l_xp_y_z = light[x + 1][y][z];
								if ((top == true) || (bottom == true) || (back == true) || (right == true))
									l_xp_y_zm = light[x + 1][y][z - 1];
								if ((top == true) || (bottom == true) || (back == true) || (right == true) || (left == true))
									l_x_y_zm = light[x][y][z - 1];
								if ((top == true) || (front == true) || (back == true) || (right == true) || (left == true))
									l_x_yp_z = light[x][y + 1][z];
								if ((top == true) || (front == true) || (back == true) || (right == true))
									l_xp_yp_z = light[x + 1][y + 1][z];
								if ((top == true) || (back == true) || (right == true))
									l_xp_yp_zm = light[x + 1][y + 1][z - 1];
								if ((top == true) || (back == true) || (right == true) || (left == true))
									l_x_yp_zm = light[x][y + 1][z - 1];
								if ((top == true) || (bottom == true) || (front == true) || (back == true) || (left == true))
									l_xm_y_z = light[x - 1][y][z];
								if ((top == true) || (bottom == true) || (back == true) || (left == true))
									l_xm_y_zm = light[x - 1][y][z - 1];
								if ((top == true) || (front == true) || (back == true) || (left == true))
									l_xm_yp_z = light[x - 1][y + 1][z];
								if ((top == true) || (back == true) || (left == true))
									l_xm_yp_zm = light[x - 1][y + 1][z - 1];
								if ((top == true) || (bottom == true) || (front == true) || (left == true))
									l_xm_y_zp = light[x - 1][y][z + 1];
								if ((top == true) || (bottom == true) || (front == true) || (right == true) || (left == true))
									l_x_y_zp = light[x][y][z + 1];
								if ((top == true) || (front == true) || (left == true))
									l_xm_yp_zp = light[x - 1][y + 1][z + 1];
								if ((top == true) || (front == true) || (right == true) || (left == true))
									l_x_yp_zp = light[x][y + 1][z + 1];
								if ((top == true) || (bottom == true) || (front == true) || (right == true))
									l_xp_y_zp = light[x + 1][y][z + 1];
								if ((top == true) || (front == true) || (right == true))
									l_xp_yp_zp = light[x + 1][y + 1][z + 1];
								if ((bottom == true) || (front == true) || (back == true) || (right == true) || (left == true))
									l_x_ym_z = light[x][y - 1][z];
								if ((bottom == true) || (front == true) || (back == true) || (right == true))
									l_xp_ym_z = light[x + 1][y - 1][z];
								if ((bottom == true) || (back == true) || (right == true))
									l_xp_ym_zm = light[x + 1][y - 1][z - 1];
								if ((bottom == true) || (back == true) || (right == true) || (left == true))
									l_x_ym_zm = light[x][y - 1][z - 1];
								if ((bottom == true) || (front == true) || (back == true) || (left == true))
									l_xm_ym_z = light[x - 1][y - 1][z];
								if ((bottom == true) || (back == true) || (left == true))
									l_xm_ym_zm = light[x - 1][y - 1][z - 1];
								if ((bottom == true) || (front == true) || (left == true))
									l_xm_ym_zp = light[x - 1][y - 1][z + 1];
								if ((bottom == true) || (front == true) || (right == true) || (left == true))
									l_x_ym_zp = light[x][y - 1][z + 1];
								if ((bottom == true) || (front == true) || (right == true))
									l_xp_ym_zp = light[x + 1][y - 1][z + 1];

								if ((top == true) || (back == true) || (right == true))
								{
									light1 = (l_x_y_z + l_xp_y_z + l_xp_y_zm + l_x_y_zm + l_x_yp_z + l_xp_yp_z + l_xp_yp_zm + l_x_yp_zm) / 8.0f;
								}

								if ((top == true) || (back == true) || (left == true))
								{
									light2 = (l_x_y_z + l_xm_y_z + l_xm_y_zm + l_x_y_zm + l_x_yp_z + l_xm_yp_z + l_xm_yp_zm + l_x_yp_zm) / 8.0f;
								}

								if ((top == true) || (front == true) || (left == true))
								{
									light3 = (l_x_y_z + l_xm_y_z + l_xm_y_zp + l_x_y_zp + l_x_yp_z + l_xm_yp_z + l_xm_yp_zp + l_x_yp_zp) / 8.0f;
								}

								if ((top == true) || (front == true) || (right == true))
								{
									light4 = (l_x_y_z + l_xp_y_z + l_xp_y_zp + l_x_y_zp + l_x_yp_z + l_xp_yp_z + l_xp_yp_zp + l_x_yp_zp) / 8.0f;
								}

								if ((bottom == true) || (back == true) || (right == true))
								{
									light5 = (l_x_y_z + l_xp_y_z + l_xp_y_zm + l_x_y_zm + l_x_ym_z + l_xp_ym_z + l_xp_ym_zm + l_x_ym_zm) / 8.0f;
								}

								if ((bottom == true) || (back == true) || (left == true))
								{
									light6 = (l_x_y_z + l_xm_y_z + l_xm_y_zm + l_x_y_zm + l_x_ym_z + l_xm_ym_z + l_xm_ym_zm + l_x_ym_zm) / 8.0f;
								}

								if ((bottom == true) || (front == true) || (left == true))
								{
									light7 = (l_x_y_z + l_xm_y_z + l_xm_y_zp + l_x_y_zp + l_x_ym_z + l_xm_ym_z + l_xm_ym_zp + l_x_ym_zp) / 8.0f;
								}

								if ((bottom == true) || (front == true) || (right == true))
								{
									light8 = (l_x_y_z + l_xp_y_z + l_xp_y_zp + l_x_y_zp + l_x_ym_z + l_xp_ym_z + l_xp_ym_zp + l_x_ym_zp) / 8.0f;
								}

								lights = new float[] {light1, light2, light3, light4, light5, light6, light7, light8};
							}
							else
							{
								//TODO - handle edges ???
								float l = light[x][y][z];
								lights = new float[] {l, l, l, l, l, l, l, l};
							}

							if ((space_x_y_z == 6) || (space_x_y_z == 14) || (space_x_y_z == 26) || (space_x_y_z == 27) || (space_x_y_z == 28))
							{
								mesh = mesh2;
							}
							else
							{
								mesh = mesh1;
							}

							count = blocks[space_x_y_z].draw(mesh, x - xsh, y - ysh, z - zsh, lights,
								top, bottom, front, back, left, right,
								false, false, false, false);

							if (count != 0)
							{
								counters.visBlockCount++;
								region.setVisible(true);
							}

							counters.faceCount += count;
						}

					}

				}

			}

		}

	}

	private boolean hasFace(
		final byte block,
		final byte other)
	{
		if ((((block == 6) || (block == 14) || (block == 26) || (block == 27) || (block == 28)) && (block == other))
			|| ((block == 6) && (other == 26)) || ((block == 26) && (other == 6)) || ((block == 27) && (other == 6))
			|| ((block == 27) && (other == 26)) || ((block == 28) && (other == 6)) || ((block == 28) && (other == 26))
			|| ((block == 28) && (other == 27)))
		{
			return false;
		}

		return ((other == 0) || (other == 6) || (other == 9) ||  (other == 11) || (other == 14) || (other == 17)
			|| (other == 20) || (other == 21) || (other == 22) || (other == 23) || (other == 24) || (other == 25)
			|| (other == 26) || (other == 27) || (other == 28) || (other == 30));
	}

	private byte getSpace(
		final int x,
		final int y,
		final int z)
	{
		byte[][] space1;
		byte[] space2;
		byte space3;

		space1 = space[x];
		space2 = space1[y];
		space3 = space2[z];

		return space3;
	}

}

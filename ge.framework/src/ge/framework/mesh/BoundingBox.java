package ge.framework.mesh;

public class BoundingBox
{
	private float minx;
	private float maxx;
	private float miny;
	private float maxy;
	private float minz;
	private float maxz;

	public BoundingBox()
	{
		// Call super constructor
		super();

		reset();
	}

	public float getMinx()
	{
		return minx;
	}

	public float getMaxx()
	{
		return maxx;
	}

	public float getMiny()
	{
		return miny;
	}

	public float getMaxy()
	{
		return maxy;
	}

	public float getMinz()
	{
		return minz;
	}

	public float getMaxz()
	{
		return maxz;
	}

	public void reset()
	{
		minx = Float.MAX_VALUE;
		maxx = -Float.MAX_VALUE;
		miny = Float.MAX_VALUE;
		maxy = -Float.MAX_VALUE;
		minz = Float.MAX_VALUE;
		maxz = -Float.MAX_VALUE;
	}

	public void update(
		final float x,
		final float y,
		final float z)
	{
		minx = ((x < minx) ? x : minx);
		maxx = ((x > maxx) ? x : maxx);
		miny = ((y < miny) ? y : miny);
		maxy = ((y > maxy) ? y : maxy);
		minz = ((z < minz) ? z : minz);
		maxz = ((z > maxz) ? z : maxz);
	}

	public void update(
		final BoundingBox b)
	{
		minx = ((b.getMinx() < minx) ? b.getMinx() : minx);
		maxx = ((b.getMaxx() > maxx) ? b.getMaxx() : maxx);
		miny = ((b.getMiny() < miny) ? b.getMiny() : miny);
		maxy = ((b.getMaxy() > maxy) ? b.getMaxy() : maxy);
		minz = ((b.getMinz() < minz) ? b.getMinz() : minz);
		maxz = ((b.getMaxz() > maxz) ? b.getMaxz() : maxz);
	}

}

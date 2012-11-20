package ge.demo.actor;

import org.lwjgl.util.vector.Vector3f;

import ge.framework.mesh.Mesh;

public class PlayBlock extends Actor
{
	private float xmov, ymov, zmov, xrot, yrot, zrot, xsin, ysin, zsin, xamp, yamp, zamp;
	private float xp, yp, zp, xa, ya, za, xsa, ysa, zsa;

	public PlayBlock(
		final float xmov,
		final float ymov,
		final float zmov,
		final float xrot,
		final float yrot,
		final float zrot,
		final float xsin,
		final float ysin,
		final float zsin,
		final float xamp,
		final float yamp,
		final float zamp)
	{
		this.xmov = xmov;
		this.ymov = ymov;
		this.zmov = zmov;
		this.xrot = xrot;
		this.yrot = yrot;
		this.zrot = zrot;
		this.xsin = xsin;
		this.ysin = ysin;
		this.zsin = zsin;
		this.xamp = xamp;
		this.yamp = yamp;
		this.zamp = zamp;
	}

	public void draw(Mesh mesh)
	{
		
	}

	//xmov|ymov|zmov=0.01f
	//xrot|ytor|zrot=0.01f
	//xsin|ysin|zsin=1.0f
	//xamp|yamp|zamp=1.0f
	public void act(long delta)
	{
		xp += xmov;
		yp += ymov;
		zp += zmov;
		xa += xrot;
		ya += yrot;
		za += zrot;
		xsa += xsin;
		ysa += ysin;
		zsa += zsin;

		position.x = xp + xamp * (float) Math.sin(Math.toRadians(xsa));
		position.y = 0.25f + yp + yamp * (float) Math.sin(Math.toRadians(ysa));
		position.z = zp + zamp * (float) Math.sin(Math.toRadians(zsa));

		rotation.x = xa;
		rotation.y = ya;
		rotation.z = za;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;

		xp += position.x;
		yp += position.y;
		zp += position.z;
	}

	public void setRotation(Vector3f rotation)
	{
		this.rotation = rotation;

		xa += rotation.x;
		ya += rotation.y;
		za += rotation.z;
	}

}

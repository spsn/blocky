package ge.demo.actor;

import ge.framework.mesh.Mesh;

public abstract class Actor
{
	private float xa = 0, ya = 0, za = 0, xp = 0, yp = 0, zp = 0;

	public Actor()
	{
		// TODO Auto-generated constructor stub
	}

	public abstract void draw(Mesh mesh);
	
	public abstract void act(int delta);
	
	public float getXa() {
		return xa;
	}

	public void setXa(float xa) {
		this.xa = xa;
	}

	public float getYa() {
		return ya;
	}

	public void setYa(float ya) {
		this.ya = ya;
	}

	public float getZa() {
		return za;
	}

	public void setZa(float za) {
		this.za = za;
	}

	public float getXp() {
		return xp;
	}

	public void setXp(float xp) {
		this.xp = xp;
	}

	public float getYp() {
		return yp;
	}

	public void setYp(float yp) {
		this.yp = yp;
	}

	public float getZp() {
		return zp;
	}

	public void setZp(float zp) {
		this.zp = zp;
	}

}

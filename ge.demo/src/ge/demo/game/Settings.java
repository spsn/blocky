package ge.demo.game;

public class Settings
{
	private int worldSize;
	private int environmentType;
	private int terrainType;
	private int vegetationMode;
	private int blockMode;
	private String worldFileName;
	private String textureFileName;
	private float xmov, ymov, zmov, xrot, yrot, zrot, xsin, ysin, zsin, xamp, yamp, zamp;

	/**
	 * Constructor.
	 */
	public Settings()
	{
		// Call super constructor
		super();
	}

	public int getWorldSize() {
		return worldSize;
	}

	public void setWorldSize(int worldSize) {
		this.worldSize = worldSize;
	}

	public int getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(int environmentType) {
		this.environmentType = environmentType;
	}

	public int getTerrainType() {
		return terrainType;
	}

	public void setTerrainType(int terrainType) {
		this.terrainType = terrainType;
	}

	public int getVegetationMode() {
		return vegetationMode;
	}

	public void setVegetationMode(int vegetationMode) {
		this.vegetationMode = vegetationMode;
	}

	public int getBlockMode() {
		return blockMode;
	}

	public void setBlockMode(int blockMode) {
		this.blockMode = blockMode;
	}

	public float getXmov() {
		return xmov;
	}

	public void setXmov(float xmov) {
		this.xmov = xmov;
	}

	public float getYmov() {
		return ymov;
	}

	public void setYmov(float ymov) {
		this.ymov = ymov;
	}

	public float getZmov() {
		return zmov;
	}

	public void setZmov(float zmov) {
		this.zmov = zmov;
	}

	public float getXrot() {
		return xrot;
	}

	public void setXrot(float xrot) {
		this.xrot = xrot;
	}

	public float getYrot() {
		return yrot;
	}

	public void setYrot(float yrot) {
		this.yrot = yrot;
	}

	public float getZrot() {
		return zrot;
	}

	public void setZrot(float zrot) {
		this.zrot = zrot;
	}

	public float getXsin() {
		return xsin;
	}

	public void setXsin(float xsin) {
		this.xsin = xsin;
	}

	public float getYsin() {
		return ysin;
	}

	public void setYsin(float ysin) {
		this.ysin = ysin;
	}

	public float getZsin() {
		return zsin;
	}

	public void setZsin(float zsin) {
		this.zsin = zsin;
	}

	public float getXamp() {
		return xamp;
	}

	public void setXamp(float xamp) {
		this.xamp = xamp;
	}

	public float getYamp() {
		return yamp;
	}

	public void setYamp(float yamp) {
		this.yamp = yamp;
	}

	public float getZamp() {
		return zamp;
	}

	public void setZamp(float zamp) {
		this.zamp = zamp;
	}

	public String getWorldFileName() {
		return worldFileName;
	}

	public void setWorldFileName(String worldFileName) {
		this.worldFileName = worldFileName;
	}

	public String getTextureFileName() {
		return textureFileName;
	}

	public void setTextureFileName(String textureFileName) {
		this.textureFileName = textureFileName;
	}

}

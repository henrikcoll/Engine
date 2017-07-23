package net.henrikcoll.engine.gfx;

public class Spritesheet extends Image {

	private int tileWidth, tileHeight;

	public Spritesheet(String path, int tileWidth, int tileHeight) {
		super(path);
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
	}

	public Image getSprite(int tileX, int tileY) {
		int[] p = new int[tileWidth * tileHeight];

		for (int y = 0; y < tileHeight; y++) {
			for (int x = 0; x < tileWidth; x++) {
				p[x + y*tileWidth] = this.getPixels()[(x + tileX * tileWidth) + (y + tileY * tileHeight) * this.getWidth()];
			}
		}

		return new Image(p, tileWidth, tileHeight);
	}

	public int getTileWidth() {
		return tileWidth;
	}

	public void setTileWidth(int tileWidth) {
		this.tileWidth = tileWidth;
	}

	public int getTileHeight() {
		return tileHeight;
	}

	public void setTileHeight(int tileHeight) {
		this.tileHeight = tileHeight;
	}

}

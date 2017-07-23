package net.henrikcoll.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	private int width, height;
	private int[] pixels;
	private boolean alpha = false;
	private int lightBlock = Light.NONE;
	
	public Image(String path){
		BufferedImage img = null;
		try {
			img = ImageIO.read(Image.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = img.getWidth();
		height = img.getHeight();
		pixels = img.getRGB(0, 0, width, height, null, 0, width);
		
		img.flush();
	}
	public Image(int[] pixels, int width, int height){
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public boolean isAlpha() {
		return alpha;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}
	public int getLightBlock() {
		return lightBlock;
	}
	public void setLightBlock(int lightBlock) {
		this.lightBlock = lightBlock;
	}
}

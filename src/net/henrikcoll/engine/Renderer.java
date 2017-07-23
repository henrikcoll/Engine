package net.henrikcoll.engine;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.henrikcoll.engine.gfx.Font;
import net.henrikcoll.engine.gfx.Image;
import net.henrikcoll.engine.gfx.ImageRequest;
import net.henrikcoll.engine.gfx.Light;
import net.henrikcoll.engine.gfx.LightRequest;
import net.henrikcoll.engine.gfx.Spritesheet;

public class Renderer {

	private ArrayList<ImageRequest> imageRequest = new ArrayList<ImageRequest>();
	private ArrayList<LightRequest> lightRequest = new ArrayList<LightRequest>();

	private int pWidth, pHeight;
	private int[] pixels;
	private int[] zb;
	private int zDepth = 0;

	private int ambientColor = 0xFF6B6B6B;
	private int[] lightMap;
	private int[] lightBlock;

	private Font font = Font.STANDARD;

	private boolean processing = false;

	public Renderer(Engine e) {
		pWidth = e.getWidth();
		pHeight = e.getHeight();
		pixels = ((DataBufferInt) e.getWindow().getImage().getRaster().getDataBuffer()).getData();
		zb = new int[pixels.length];

		lightMap = new int[pixels.length];
		lightBlock = new int[pixels.length];
	}

	public void clear() {
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = 0;
			zb[i] = 0;
			lightMap[i] = ambientColor;
			lightBlock[i] = 0;
		}
	}

	public void process() {
		processing = true;

		Collections.sort(imageRequest, new Comparator<ImageRequest>() {

			@Override
			public int compare(ImageRequest i1, ImageRequest i2) {
				if (i1.zDepth < i2.zDepth)
					return -1;
				if (i1.zDepth > i2.zDepth)
					return 1;
				return 0;
			}

		});

		for (int i = 0; i < imageRequest.size(); i++) {
			ImageRequest ir = imageRequest.get(i);
			setzDepth(ir.zDepth);
			drawImage(ir.image, ir.xOffset, ir.yOffset);
		}

		for (int i = 0; i < lightRequest.size(); i++) {
			LightRequest lr = lightRequest.get(i);
			drawLight(lr.light, lr.x, lr.y);
		}

		for (int i = 0; i < pixels.length; i++) {
			float r = ((lightMap[i] >> 16) & 0xFF) / 255f;
			float g = ((lightMap[i] >> 8) & 0xFF) / 255f;
			float b = (lightMap[i] & 0xFF) / 255f;

			pixels[i] = ((int) (((pixels[i] >> 16) & 0xFF) * r) << 16 | (int) (((pixels[i] >> 8) & 0xFF) * g) << 8 | (int) ((pixels[i] & 0xFF) * b));
		}

		imageRequest.clear();
		lightRequest.clear();
		processing = false;
	}

	public void setPixel(int x, int y, int value) {
		int alpha = ((value >> 24) & 0xFF);
		if ((x < 0 || x >= pWidth || y < 0 || y >= pHeight) || alpha == 0) {
			return;
		}

		int index = x + y * pWidth;

		if (zb[index] > zDepth)
			return;

		zb[index] = zDepth;

		if (alpha == 255) {
			pixels[index] = value;
		} else {

			int pixelColor = pixels[index];
			int newRed = ((pixelColor >> 16) & 0xFF) - (int) ((((pixelColor >> 16) & 0xFF) - ((value >> 16) & 0xFF)) * (alpha / 255f));
			int newGreen = ((pixelColor >> 8) & 0xFF) - (int) ((((pixelColor >> 8) & 0xFF) - ((value >> 8) & 0xFF)) * (alpha / 255f));
			int newBlue = (pixelColor & 0xFF) - (int) (((pixelColor & 0xFF) - (value & 0xFF)) * (alpha / 255f));

			pixels[index] = (newRed << 16 | newGreen << 8 | newBlue);
		}
	}

	public void setLightMap(int x, int y, int value) {

		if ((x < 0 || x >= pWidth || y < 0 || y >= pHeight)) {
			return;
		}

		int baseColor = lightMap[x + y * pWidth];

		int maxRed = Math.max((baseColor >> 16) & 0xFF, (value >> 16) & 0xFF);
		int maxGreen = Math.max((baseColor >> 8) & 0xFF, (value >> 8) & 0xFF);
		int maxBlue = Math.max(baseColor & 0xFF, value & 0xFF);

		lightMap[x + y * pWidth] = (maxRed << 16 | maxGreen << 8 | maxBlue);
	}

	public void setLightBlock(int x, int y, int value) {

		if ((x < 0 || x >= pWidth || y < 0 || y >= pHeight)) {
			return;
		}

		if (zb[x + y * pWidth] > zDepth)
			return;

		lightBlock[x + y * pWidth] = value;
	}

	public void drawImage(Image img, int xOffset, int yOffset) {

		if (img.isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(img, zDepth, xOffset, yOffset));
			return;
		}

		int newX = 0;
		int newY = 0;
		int newWidth = img.getWidth();
		int newHeight = img.getHeight();

		//@formatter:off
		if (xOffset < -newWidth) return;
		if (yOffset < -newHeight) return;
		if (xOffset >= pWidth) return;
		if (yOffset >= pHeight) return;

		if (xOffset < 0) newX -= xOffset;
		if (yOffset < 0) newY -= yOffset;
		if (newWidth + xOffset > pWidth) newWidth -= newWidth + xOffset - pWidth;
		if (newHeight + yOffset > pHeight) newHeight -= newHeight + yOffset - pHeight;
		//@formatter:on

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + xOffset, y + yOffset, img.getPixels()[x + y * img.getWidth()]);
				setLightBlock(x + xOffset, y + yOffset, img.getLightBlock());
			}
		}
	}

	public void drawSprite(Spritesheet sheet, int xOffset, int yOffset, int tileX, int tileY) {

		if (sheet.getSprite(tileX, tileY).isAlpha() && !processing) {
			imageRequest.add(new ImageRequest(sheet.getSprite(tileX, tileY), zDepth, xOffset, yOffset));
			return;
		}

		int newX = 0;
		int newY = 0;
		int newWidth = sheet.getTileWidth();
		int newHeight = sheet.getTileHeight();

		//@formatter:off
		if (xOffset < -newWidth) return;
		if (yOffset < -newHeight) return;
		if (xOffset >= pWidth) return;
		if (yOffset >= pHeight) return;

		if (xOffset < 0) newX -= xOffset;
		if (yOffset < 0) newY -= yOffset;
		if (newWidth + xOffset > pWidth) newWidth -= newWidth + xOffset - pWidth;
		if (newHeight + yOffset > pHeight) newHeight -= newHeight + yOffset - pHeight;
		//@formatter:on

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + xOffset, y + yOffset, sheet.getPixels()[(x + tileX * sheet.getTileWidth()) + (y + tileY * sheet.getTileHeight()) * sheet.getWidth()]);
				setLightBlock(x + xOffset, y + yOffset, sheet.getSprite(tileX, tileY).getLightBlock());
			}
		}
	}

	public void drawText(String text, int xOffset, int yOffset, int color) {
		int offset = 0;
		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i);

			for (int y = 0; y < font.getFontImage().getHeight(); y++) {
				for (int x = 0; x < font.getWidths()[unicode]; x++) {
					if (font.getFontImage().getPixels()[(x + font.getOffsets()[unicode]) + y * font.getFontImage().getWidth()] == 0xFFFFFFFF) {
						setPixel(x + xOffset + offset, y + yOffset, color);
						setLightMap(x + xOffset + offset, y + yOffset, color);
					}
				}
			}
			offset += font.getWidths()[unicode];
		}
	}

	public void drawRect(int xOffset, int yOffset, int width, int height, int color, boolean blockLight) {

		for (int y = 0; y <= height; y++) {
			setPixel(xOffset, y + yOffset, color);
			setPixel(xOffset + width, y + yOffset, color);
			if(blockLight){
				setLightBlock(xOffset, y + yOffset, Light.FULL);
				setLightBlock(xOffset + width, y + yOffset, Light.FULL);
			}
		}
		for (int x = 0; x <= width; x++) {
			setPixel(x + xOffset, yOffset, color);
			setPixel(x + xOffset, yOffset + height, color);
			if(blockLight){
				setLightBlock(x + xOffset, yOffset, Light.FULL);
				setLightBlock(x + xOffset, yOffset + height, Light.FULL);
			}
		}
	}

	public void fillRect(int xOffset, int yOffset, int width, int height, int color, boolean blockLight) {
		int newX = 0;
		int newY = 0;
		int newWidth = width;
		int newHeight = height;

		//@formatter:off
		if (xOffset < -newWidth) return;
		if (yOffset < -newHeight) return;
		if (xOffset >= pWidth) return;
		if (yOffset >= pHeight) return;

		if (xOffset < 0) newX -= xOffset;
		if (yOffset < 0) newY -= yOffset;
		if (newWidth + xOffset > pWidth) newWidth -= newWidth + xOffset - pWidth;
		if (newHeight + yOffset > pHeight) newHeight -= newHeight + yOffset - pHeight;
		//@formatter:on

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + xOffset, y + yOffset, color);
				if(blockLight){
					setLightBlock(x + xOffset, y + yOffset, Light.FULL);
				}
			}
		}

	}

	public void drawLight(Light l, int xOffset, int yOffset) {
		if(!processing){
			lightRequest.add(new LightRequest(l, xOffset, yOffset));
			return;
		}
		for (int i = 0; i <= l.getDiameter(); i++) {
			drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, xOffset, yOffset);
			drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), xOffset, yOffset);
			drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, xOffset, yOffset);
			drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, xOffset, yOffset);
			
		}
	}

	private void drawLightLine(Light l, int x0, int y0, int x1, int y1, int xOffset, int yOffset) {
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;

		while (true) {
			int screenX = x0 - l.getRadius() + xOffset;
			int screenY = y0 - l.getRadius() + yOffset;

			if (screenX < 0 || screenX >= pWidth || screenY < 0 || screenY >= pHeight)
				return;

			int lightColor = l.getLightValue(x0, y0);

			if (lightColor == 0) {
				return;
			}

			if (lightBlock[screenX + screenY * pWidth] == Light.FULL) {
				return;
			}

			setLightMap(screenX, screenY, lightColor);
			if (x0 == x1 && y0 == y1)
				break;

			e2 = 2 * err;

			if (e2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}
	}

	public int getzDepth() {
		return zDepth;
	}

	public void setzDepth(int zDepth) {
		this.zDepth = zDepth;
	}

	public int getAmbientColor() {
		return ambientColor;
	}

	public void setAmbientColor(int ambientColor) {
		this.ambientColor = ambientColor;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
}

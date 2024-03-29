package net.henrikcoll.engine.gfx;

public class Light {
	
	public static final int NONE = 0;
	public static final int FULL = 1;
	
	private int radius, diameter, color;
	private int[] lightMap;

	public Light(int r, int c) {
		this.radius = r;
		this.diameter = r * 2;
		this.color = c;
		lightMap = new int[diameter * diameter];

		for (int y = 0; y < diameter; y++) {
			for (int x = 0; x < diameter; x++) {
				double distance = Math.sqrt((x - radius) * (x - radius) + (y - radius) * (y - radius));

				if (distance < radius) {
					double power = 1 - (distance / radius);
					lightMap[x + y * diameter] = ((int) (((color >> 16) & 0xFF) * power) << 16 | (int) (((color >> 8) & 0xFF) * power) << 8 | (int) ((color & 0xFF) * power));
					;
				} else {
					lightMap[x + y * diameter] = 0;
				}
			}
		}
	}

	public int getLightValue(int x, int y) {
		if (x < 0 || x >= diameter || y < 0 || y >= diameter)
			return 0;
		return lightMap[x + y * diameter];
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getDiameter() {
		return diameter;
	}

	public void setDiameter(int diameter) {
		this.diameter = diameter;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int[] getLightMap() {
		return lightMap;
	}

	public void setLightMap(int[] lightMap) {
		this.lightMap = lightMap;
	}
}

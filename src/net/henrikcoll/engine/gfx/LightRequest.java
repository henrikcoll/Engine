package net.henrikcoll.engine.gfx;

public class LightRequest {
	
	public Light light;
	public int x, y;
	
	public LightRequest(Light l, int x, int y) {
		this.light = l;
		this.x = x;
		this.y = y;
	}
}

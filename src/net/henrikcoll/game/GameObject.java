package net.henrikcoll.game;

import net.henrikcoll.engine.Engine;
import net.henrikcoll.engine.Renderer;

public abstract class GameObject {
	private int x, y;
	private int width, height;
	private String title;
	
	public GameObject(int x, int y, int width, int height, String title){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	public abstract void update(Engine e, GameManager gm, float dt);
	public abstract void render(Engine e, Renderer r);

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}


}

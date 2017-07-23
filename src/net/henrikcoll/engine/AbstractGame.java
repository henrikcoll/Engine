package net.henrikcoll.engine;

public abstract class AbstractGame {
	
	public abstract void init(Engine e);
	public abstract void update(Engine e, float dt);
	public abstract void render(Engine e, Renderer r);

}

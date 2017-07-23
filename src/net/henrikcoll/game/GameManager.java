package net.henrikcoll.game;

import net.henrikcoll.engine.AbstractGame;
import net.henrikcoll.engine.Engine;
import net.henrikcoll.engine.Renderer;

public class GameManager extends AbstractGame {
	
	public StateManager stateManager;
	public AbstractGame game;
	
	public GameManager(AbstractGame game) {
		stateManager = new StateManager();
		this.game = game;
	}
	
	public void init(Engine e) {
		game.init(e);
		System.out.println("Hello");
	}

	public void update(Engine e, float dt) {
		game.update(e, dt);
	}
	
	public void render(Engine e, Renderer r) {
		game.render(e, r);
	}
}

package net.henrikcoll.game;

import net.henrikcoll.engine.Engine;
import net.henrikcoll.engine.Renderer;

public class StateManager{
	
	private State state;
	
	public StateManager() {
	}

	public void update(Engine e, GameManager gm, float dt) {
		state.update(e, gm, dt);
	}

	public void render(Engine e, Renderer r) {
		state.render(e, r);
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
}

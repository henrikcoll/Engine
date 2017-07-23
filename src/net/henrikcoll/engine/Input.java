package net.henrikcoll.engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private Engine e;

	private final int NUM_KEYS = 256;
	private boolean[] keys = new boolean[NUM_KEYS];
	private boolean[] keysLast = new boolean[NUM_KEYS];

	private final int NUM_BUTTONS = 5;
	private boolean[] buttons = new boolean[NUM_BUTTONS];
	private boolean[] buttonsLast = new boolean[NUM_BUTTONS];

	private int mouseX, mouseY;
	private int scroll;

	public Input(Engine e) {
		this.e = e;
		mouseX = 0;
		mouseY = 0;

		e.getWindow().getCanvas().addKeyListener(this);
		e.getWindow().getCanvas().addMouseListener(this);
		e.getWindow().getCanvas().addMouseWheelListener(this);
		e.getWindow().getCanvas().addMouseMotionListener(this);
	}

	public void update() {
		scroll = 0;
		for (int i = 0; i < NUM_KEYS; i++) {
			keysLast[i] = keys[i];
		}
		for (int i = 0; i < NUM_BUTTONS; i++) {
			buttonsLast[i] = buttons[i];
		}
	}

	public boolean isKey(int keyCode) {
		return keys[keyCode];
	}

	public boolean isKeyUp(int keyCode) {
		return !keys[keyCode] && keysLast[keyCode];
	}

	public boolean isKeyDown(int keyCode) {
		return keys[keyCode] && !keysLast[keyCode];
	}

	public boolean isButton(int button) {
		return buttons[button];
	}

	public boolean isbuttonUp(int button) {
		return !buttons[button] && buttonsLast[button];
	}

	public boolean isbuttonDown(int button) {
		return buttons[button] && !buttonsLast[button];
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent event) {
		scroll = event.getWheelRotation();
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		mouseX = (int) (event.getX() / e.getScale());
		mouseY = (int) (event.getY() / e.getScale());
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		mouseX = (int) (event.getX() / e.getScale());
		mouseY = (int) (event.getY() / e.getScale());
	}

	@Override
	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void mousePressed(MouseEvent event) {
		buttons[event.getButton()] = true;
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		buttons[event.getButton()] = false;
	}

	@Override
	public void mouseEntered(MouseEvent event) {
	}

	@Override
	public void mouseExited(MouseEvent event) {
	}

	@Override
	public void keyTyped(KeyEvent event) {
	}

	@Override
	public void keyPressed(KeyEvent event) {
		keys[event.getKeyCode()] = true;
	}

	@Override
	public void keyReleased(KeyEvent event) {
		keys[event.getKeyCode()] = false;
	}

	public boolean[] getKeys() {
		return keys;
	}

	public boolean[] getButtons() {
		return buttons;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getScroll() {
		return scroll;
	}

}

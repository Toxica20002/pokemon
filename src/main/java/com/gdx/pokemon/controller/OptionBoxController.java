package com.gdx.pokemon.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.gdx.pokemon.ui.OptionBox;
import com.gdx.pokemon.util.Action;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller that can be coupled to a OptionBox, coupling each option with code for execution.
 * 
 *
 */
public class OptionBoxController extends InputAdapter {
	
	private OptionBox box;
	private List<Action> actions;
	
	public OptionBoxController(OptionBox box) {
		this.box = box;
		this.actions = new ArrayList<Action>(box.getAmount());
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (!box.isVisible()) {
			return false;
		}
		
		if (keycode == Keys.UP) {
			return true;
		} else if (keycode == Keys.DOWN) {
			return true;
		} else if (keycode == Keys.X) {
			// activate
			return true;
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.F12) {
			box.setVisible(!box.isVisible());
			return true;
		}
		
		if (!box.isVisible()) {
			return false;
		}
		
		if (keycode == Keys.UP) {
			box.moveUp();
			return true;
		} else if (keycode == Keys.DOWN) {
			box.moveDown();
			return true;
		} else if (keycode == Keys.X) {		// activate
			actions.get(box.getIndex()).action();
			box.setVisible(false);
			return true;
		}
		return false;
	}
	
	public void addAction(Action a, int index) {
		actions.add(index, a);
	}
	
	public void addAction(Action a, String option) {
		box.addOption(option);
		actions.add(a);
	}
}

package com.gdx.pokemon.battle;

import com.gdx.pokemon.model.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class Trainer {
	
	private List<Pokemon> team;
	
	public Trainer(Pokemon pokemon) {
		team = new ArrayList<Pokemon>();
		team.add(pokemon);
	}
	
	public boolean addPokemon(Pokemon pokemon) {
		if (team.size() >= 6) {
			return false;
		} else {
			team.add(pokemon);
			return true;
		}
	}
	
	public Pokemon getPokemon(int index) {
		return team.get(index);
	}
	
	public int getTeamSize() {
		return team.size();
	}
}

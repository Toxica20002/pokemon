package com.gdx.pokemon.battle;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.pokemon.PokemonGame;
import com.gdx.pokemon.model.Pokemon;

public class PlayerTrainer {
    private static volatile PlayerTrainer instance = null;

    private Trainer playerTrainer;

    private PlayerTrainer(PokemonGame app) {
        Texture bulbasaur = app.getAssetManager().get("res/graphics/pokemon/Bulbasaur.png", Texture.class);
        Texture slowpoke = app.getAssetManager().get("res/graphics/pokemon/Slowpoke.png", Texture.class);
        playerTrainer = new Trainer(Pokemon.generatePokemon("Bulbasaur", bulbasaur, app.getMoveDatabase()));
        playerTrainer.addPokemon(Pokemon.generatePokemon("Slowpoke", slowpoke, app.getMoveDatabase()));
    }

    public static PlayerTrainer createInstance(PokemonGame app) {
        if (instance == null) {
            synchronized (PlayerTrainer.class) {
                if (instance == null) {
                    instance = new PlayerTrainer(app);
                }
            }
        }
        return instance;
    }

    public static PlayerTrainer getInstance() {
        return instance;
    }


    public Trainer getPlayerTrainer() {
        return playerTrainer;
    }

    public void increasePokemonExp(int exp) {
        for (int i = 0; i < playerTrainer.getTeamSize(); i++) {
            playerTrainer.getPokemon(i).increaseExp(exp);
        }
    }

    public void addPokemon(Pokemon pokemon) {
        playerTrainer.addPokemon(pokemon);
    }

}

package com.gdx.pokemon.battle;

import com.badlogic.gdx.graphics.Texture;
import com.gdx.pokemon.PokemonGame;
import com.gdx.pokemon.model.Pokemon;

import java.util.Random;

public class OpponentTrainer {
    private static volatile OpponentTrainer instance = null;

    private Trainer opponentTrainer;

    private PokemonGame app;

    private OpponentTrainer(PokemonGame app) {
        this.app = app;
        randomPokemon();
    }

    public static OpponentTrainer createInstance(PokemonGame app) {
        if (instance == null) {
            synchronized (OpponentTrainer.class) {
                if (instance == null) {
                    instance = new OpponentTrainer(app);
                }
            }
        }
        return instance;
    }

    public static OpponentTrainer getInstance() {
        return instance;
    }

    public void randomPokemon(){
        if (opponentTrainer != null) {
            opponentTrainer.clearTeam();
        }
        Texture bulbasaur = app.getAssetManager().get("res/graphics/pokemon/Bulbasaur.png", Texture.class);
        Texture slowpoke = app.getAssetManager().get("res/graphics/pokemon/Slowpoke.png", Texture.class);
        Random random = new Random();
        int randomInt = random.nextInt(3);
        if (randomInt == 0) {
            opponentTrainer = new Trainer(Pokemon.generatePokemon("Bulba", bulbasaur, app.getMoveDatabase()));
        } else if(randomInt == 1) {
            opponentTrainer = new Trainer(Pokemon.generatePokemon("Slowpoke", slowpoke, app.getMoveDatabase()));
        }
//        } else if(randomInt == 2){
//            opponentTrainer = new Trainer(Pokemon.generatePokemon("Charmander", charmander, app.getMoveDatabase()));
//        }
    }

    public void addPokemon(Pokemon pokemon){
        opponentTrainer.addPokemon(pokemon);
    }

    public void addPokemon(String name){
        String path = "res/graphics/pokemon/" + name + ".png";
        Texture texture = app.getAssetManager().get(path, Texture.class);
        Pokemon pokemon = Pokemon.generatePokemon(name, texture, app.getMoveDatabase());
        opponentTrainer.addPokemon(pokemon);
    }

    public Trainer getPlayerTrainer() {
        return opponentTrainer;
    }

    public int sumAllExp(){
        int sum = 0;
        for (int i = 0; i < opponentTrainer.getTeamSize(); i++) {
            sum += opponentTrainer.getPokemon(i).getExp();
        }
        return sum;
    }

    public void clearTeam(){
        if (opponentTrainer != null)
            opponentTrainer.clearTeam();
    }
}

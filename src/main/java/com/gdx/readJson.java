package com.gdx;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class readJson {
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("./pokemon.json")));
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                int nationalNumber = jsonObject.getInt("national_number");
                String englishName = jsonObject.getString("english_name");
                int hp = jsonObject.getInt("hp");
                int attack = jsonObject.getInt("attack");
                int defense = jsonObject.getInt("defense");
                int spAttack = jsonObject.getInt("sp_attack");
                int spDefense = jsonObject.getInt("sp_defense");
                int speed = jsonObject.getInt("speed");

                System.out.println("National Number: " + nationalNumber);
                System.out.println("English Name: " + englishName);
                System.out.println("HP: " + hp);
                System.out.println("Attack: " + attack);
                System.out.println("Defense: " + defense);
                System.out.println("Special Attack: " + spAttack);
                System.out.println("Special Defense: " + spDefense);
                System.out.println("Speed: " + speed);
                System.out.println("-----------------------------");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
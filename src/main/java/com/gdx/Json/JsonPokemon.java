package com.gdx.Json;

public class JsonPokemon {
    private String name;
    private String Hp;
    private String Atk;
    private String Def;
    private String SpAtk;
    private String SpDef;
    private String Speed;

    public JsonPokemon(String name, String Hp, String Atk, String Def, String SpAtk, String SpDef, String Speed) {
        this.name = name;
        this.Hp = Hp;
        this.Atk = Atk;
        this.Def = Def;
        this.SpAtk = SpAtk;
        this.SpDef = SpDef;
        this.Speed = Speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHp() {
        return Hp;
    }

    public void setHp(String hp) {
        Hp = hp;
    }

    public String getAtk() {
        return Atk;
    }

    public void setAtk(String atk) {
        Atk = atk;
    }

    public String getDef() {
        return Def;
    }

    public void setDef(String def) {
        Def = def;
    }

    public String getSpAtk() {
        return SpAtk;
    }

    public void setSpAtk(String spAtk) {
        SpAtk = spAtk;
    }

    public String getSpDef() {
        return SpDef;
    }

    public void setSpDef(String spDef) {
        SpDef = spDef;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }
}

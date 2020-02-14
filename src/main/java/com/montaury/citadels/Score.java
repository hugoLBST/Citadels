package com.montaury.citadels;

public class Score {
    private int valeur;
    public Score(){
        setValue(0);
    }
    public void setValue(int valeur) {
        this.valeur = valeur;
    }
    public int getValue() {
        return valeur;
    }
}

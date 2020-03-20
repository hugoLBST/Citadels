package com.montaury.citadels.district;

public class DestructibleDistrict {
    public DestructibleDistrict(Card card, int destructionCost) {
        this.card = card;
        this.destructionCost = destructionCost;
    }

    public Card card() {
        return card;
    }

    public int destructionCost() {
        return destructionCost;
    }

    public void setDestructionCost(int dc){
        this.destructionCost = dc;
    }
    private final Card card;
    private int destructionCost;
}

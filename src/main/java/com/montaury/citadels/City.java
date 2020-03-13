package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import com.montaury.citadels.district.DistrictType;
import com.montaury.citadels.player.Player;
import io.vavr.collection.List;

import static com.montaury.citadels.district.District.GREAT_WALL;
import static com.montaury.citadels.district.District.HAUNTED_CITY;
public class City {
    private static final int END_GAME_DISTRICT_NUMBER = 7;
    private static final int SCORE_BONUS_ALL_DISTRICT_TYPES = 3;
    private static final int SCORE_BONUS_FIRST_CITY_FINISHED = 2;
    private static final int SCORE_BONUS_CITY_COMPLETE = 2;

    private final Board board;
    private List<Card> districtCards = List.empty();

    public City(Board board) {
        this.board = board;
    }

    public void buildDistrict(Card card) {
        districtCards = districtCards.append(card);
        if (isComplete()) {
            board.mark(this);
        }
    }

    public boolean isComplete() {
        return districtCards.size() >= END_GAME_DISTRICT_NUMBER;
    }

    public int score(Possession possession) {
        Score score = new Score();
        for(District district : districts()) score.setValue(score.getValue() + district.cost());
        score.setValue(score.getValue() + districtsScoreBonus(possession));
        if (winsAllDistrictTypes()) score.setValue(score.getValue() + SCORE_BONUS_ALL_DISTRICT_TYPES);
        if (board.isFirstCompleteCity(this)) score.setValue(score.getValue() + SCORE_BONUS_FIRST_CITY_FINISHED);
        if (isComplete()) score.setValue(score.getValue() + SCORE_BONUS_CITY_COMPLETE);
        return score.getValue();
    }

    private int districtsScoreBonus(Possession possession) {
        int score = 0;
        for (District d : districts()) {
            if (d == District.DRAGON_GATE) {
                score = score + 2;
            }
            if (d == District.UNIVERSITY) {
                score = score + 2;
            }
            if (d == District.TREASURY) {
                score += score + possession.gold;
            }
            if (d == District.MAP_ROOM) {
                score += possession.hand.size();
            }
        }
        return score;
    }

    private boolean winsAllDistrictTypes() {
        int districtTypes[] = new int[DistrictType.values().length];
        for (District d : districts()) {
            districtTypes[d.districtType().ordinal()]++;
        }
        if (districtTypes[DistrictType.MILITARY.ordinal()] > 0 && districtTypes[DistrictType.NOBLE.ordinal()] > 0 && districtTypes[DistrictType.RELIGIOUS.ordinal()] > 0 && districtTypes[DistrictType.SPECIAL.ordinal()] > 0 && districtTypes[DistrictType.TRADE.ordinal()] > 0)
            return true;

        if (has(HAUNTED_CITY)) {
            int zeros = 0;
            for (int i = 0; i < districtTypes.length; i++) {
                if (districtTypes[i] == 0) {
                    zeros++;
                }
            }
            if (zeros == 1 && districtTypes[DistrictType.SPECIAL.ordinal()] > 1) {
                return true;
            }
            else return false;
        } else return false;
    }

    public boolean has(District district) {
        return districts().contains(district);
    }

    public void destroyDistrict(Card card) {
        districtCards = districtCards.remove(card);
    }

    public List<DestructibleDistrict> districtsDestructibleBy(Player player) {
        return isComplete() ?
                List.empty() :
                districtCards
                        .filter(card -> card.district().isDestructible())
                        .filter(card -> player.canAfford(destructionCost(card)))
                        .map(card -> new DestructibleDistrict(card, destructionCost(card)));
    }

    public void beautifyCard(Card card){
        // à terminer
    }
    private int destructionCost(Card card) {
        return card.district().cost() - (has(GREAT_WALL) && card.district() != GREAT_WALL ? 0 : (1));
    }

    public List<District> districts() {
        return districtCards.map(Card::district);
    }
}

package com.montaury.citadels.player;

import com.montaury.citadels.City;
import com.montaury.citadels.character.Character;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

public class ComputerController implements PlayerController {
    private int random(int maxExcluded) {
        return random.nextInt(maxExcluded);
    }

    @Override
    public Character selectOwnCharacter(List<Character> availableCharacters, List<Character> faceUpRevealedCharacters) {
        return randomAmong(availableCharacters);
    }

    @Override
    public String selectActionAmong(List<String> actions) {
        return randomAmong(actions);
    }

    @Override
    public Card selectCardAmong(Set<Card> cards) {
        return cards.head();
    }

    @Override
    public Character selectCharacterAmong(List<Character> characters){
        int size = characters.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(Character character : characters)
        {
            if (i == item)
                return character;
            i++;
        }
        return null;
    }

    @Override
    public District selectDistrictAmong(List<District> districts){
        int size = districts.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(District district : districts)
        {
            if (i == item)
                return district;
            i++;
        }
        return null;
    }

    @Override
    public City selectCityAmong(List<City> cities){
        int size = cities.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(City city : cities)
        {
            if (i == item)
                return city;
            i++;
        }
        return null;
    }

    @Override
    public Player selectPlayerAmong(List<Player> players) {
        return randomAmong(players);
    }

    @Override
    public Set<Card> selectManyAmong(Set<Card> cards) {
        return HashSet.of(cards.head());
    }

    @Override
    public DestructibleDistrict selectDistrictToDestroyAmong(Map<Player, List<DestructibleDistrict>> playersDistricts) {
        List<DestructibleDistrict> all = List.empty();
        for (List<DestructibleDistrict> ds : playersDistricts.values()) {
            all = all.appendAll(ds);
        }
        return randomAmong(all);
    }

    @Override
    public boolean acceptCard(Card card) {
        return random.nextBoolean();
    }

    private <T> T randomAmong(List<? extends T> list) {
        return list.get(random(list.size()));
    }

    private final Random random = new Random();
}

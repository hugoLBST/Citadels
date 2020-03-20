package com.montaury.citadels.player;

import com.montaury.citadels.City;
import com.montaury.citadels.character.Character;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import sun.security.krb5.internal.crypto.Des;

public interface PlayerController {
    Character selectOwnCharacter(List<Character> availableCharacters, List<Character> faceUpRevealedCharacters);

    String selectActionAmong(List<String> actions);

    Card selectCardAmong(Set<Card> cards);

    District selectDistrictAmong(List<District> districts);

    DestructibleDistrict selectDestructibleDistrictAmong(List<DestructibleDistrict> districts);

    Character selectCharacterAmong(List<Character> characters);

    City selectCityAmong(List<City> cities);

    Player selectPlayerAmong(List<Player> players);

    Set<Card> selectManyAmong(Set<Card> cards);

    DestructibleDistrict selectDistrictToDestroyAmong(Map<Player, List<DestructibleDistrict>> playersDistricts);

    boolean acceptCard(Card card);

}

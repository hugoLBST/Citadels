package com.montaury.citadels.player;

import com.montaury.citadels.City;
import com.montaury.citadels.character.Character;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import io.vavr.Tuple2;
import io.vavr.collection.*;

import java.util.Scanner;

public class HumanController implements PlayerController {

    @Override
    public Character selectOwnCharacter(List<Character> availableCharacters, List<Character> faceUpRevealedCharacters) {
        displayCharactersForSelection(availableCharacters, faceUpRevealedCharacters);
        return availableCharacters.get(scanner.nextInt() - 1);
    }

    @Override
    public String selectActionAmong(List<String> actions) {
        displayForSelection(actions);
        return actions.get(scanner.nextInt() - 1);
    }


    private static <T> void displayForSelection(List<T> T) {
        for (int i = 0; i < T.size(); i++) {
            System.out.println(T.get(i) + "(" + (i + 1) + "), ");
        }
    }

    @Override
    public Card selectCardAmong(Set<Card> cards) {
        List<Card> cardsList = cards.toList();
        displayCardsForSelection(cardsList);
        return cardsList.get(scanner.nextInt() - 1);
    }

    @Override
    public Character selectCharacterAmong(List<Character> characters) {
        displayForSelection(characters);
        return characters.get(scanner.nextInt() - 1);
    }
    @Override
    public District selectDistrictAmong(List<District> districts) {
        displayForSelection(districts);
        return districts.get(scanner.nextInt() - 1);
    }

    @Override
    public DestructibleDistrict selectDestructibleDistrictAmong(List<DestructibleDistrict> districts){
        displayForSelection(districts);
        return districts.get(scanner.nextInt() -1);
    }

    @Override
    public City selectCityAmong(List<City> cities) {
        displayForSelection(cities);
        return cities.get(scanner.nextInt() - 1);
    }

    private void displayCardsForSelection(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            System.out.println(cards.get(i).name() + "(" + (i + 1) + "), ");
        }
    }

    @Override
    public Player selectPlayerAmong(List<Player> players) {
        displayForSelection(players);
        return players.get(scanner.nextInt() - 1);
    }

    @Override
    public Set<Card> selectManyAmong(Set<Card> cards) {
        List<Card> cardsList = cards.toList();
        System.out.println("Choisissez les cartes à échanger (ex: 1,2,3");
        for (int i = 0; i < cardsList.size(); i++) {
            System.out.println(cardsList.get(i).name() + "(" + (i + 1) + "), ");
        }
        String choice = scanner.next();
        String[] cardsPositions = choice.split(",");
        return Stream.of(cardsPositions)
                .map(Integer::valueOf)
                .map(cardsList::get)
                .toSet();
    }

    @Override
    public DestructibleDistrict selectDistrictToDestroyAmong(Map<Player, List<DestructibleDistrict>> playersDistricts) {
        System.out.println("Choisissez le quartier à détruire");
        int i = 0;
        List<DestructibleDistrict> orderedDestructibleDistricts = List.empty();
        for (List<DestructibleDistrict> districts : playersDistricts.values()) {
            orderedDestructibleDistricts = orderedDestructibleDistricts.appendAll(districts);
        }
        for (Tuple2<Player, List<DestructibleDistrict>> jq : playersDistricts) {
            System.out.println(jq._1.name());
            for (DestructibleDistrict destructibleDistrict : jq._2) {
                System.out.println(destructibleDistrict.card() + " (" + i++ + ")");
            }
        }
        return orderedDestructibleDistricts.get(scanner.nextInt());
    }

    @Override
    public boolean acceptCard(Card card) {
        System.out.println("Souhaitez-vous récupérer la carte " + card + " ? (o/n)");
        return scanner.next().equals("o");
    }

    private static void displayCharactersForSelection(List<Character> characters, List<Character> faceUpRevealedCharacters) {
        displayForSelection(characters);
        for (int i = 0; i < faceUpRevealedCharacters.size(); i++) {
            System.out.println("Ecarté: " + faceUpRevealedCharacters.get(i).getName());
        }
    }

    private final Scanner scanner = new Scanner(System.in);
}

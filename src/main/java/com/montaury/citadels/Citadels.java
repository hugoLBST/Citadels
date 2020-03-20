package com.montaury.citadels;

import com.montaury.citadels.Actions.AbbeActions;
import com.montaury.citadels.character.Character;
import com.montaury.citadels.character.RandomCharacterSelector;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.district.DestructibleDistrict;
import com.montaury.citadels.district.District;
import com.montaury.citadels.district.DistrictType;
import com.montaury.citadels.player.ComputerController;
import com.montaury.citadels.player.HumanController;
import com.montaury.citadels.player.Player;
import com.montaury.citadels.round.GameRoundAssociations;
import com.montaury.citadels.round.Group;
import com.montaury.citadels.round.action.DestroyDistrictAction;
import io.vavr.Tuple;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Citadels {
    private static Board board = new Board();

    // Actions des joueurs (à mettre dans une classe à part)
    private static final String DRAW_2_CARDS ="Draw 2 cards and keep 1";
    private static final String RECEIVE_2_COINS ="Receive 2 coins";
    private static final String DRAW_3_CARDS ="Draw 3 cards and keep 1";
    private static final String RECEIVE_INCOME ="Receive income";
    private static final String EXCHANGE_CARDS_WITH_PILE ="Exchange cards with pile";
    private static final String PICK_2_CARDS ="Pick 2 cards";
    private static final String BUILD_DISTRICT ="Build district";
    private static final String DESTROY_DISTRICT ="Destroy district";
    private static final String RECEIVE_1_GOLD_FROM_THE_RICHEST_PLAYER ="Receive 1 gold from the richest player";
    private static final String KILL ="Kill";
    private static final String ROB ="Rob";
    private static final String EXCHANGE_CARDS_WITH_OTHER_PLAYER ="Exchange cards with other player";
    private static final String RECEIVE_1_GOLD ="Receive 1 gold";
    private static final String BEAUTIFY_DISTRICT ="Beautify district";
    private static final String DISCARD_CARD_FOR_2_COINS ="Discard card for 2 coins";
    private static final String DRAW_3_CARDS_FOR_2_COINS ="Draw 3 cards for 2 coins";
    private static final String END_ROUND ="End round";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Hello! Quel est votre nom ? ");
        String playerName = scanner.next();
        System.out.println("Quel est votre age ? ");
        int playerAge = scanner.nextInt();
        Player p = new Player(playerName, playerAge, new City(board), new HumanController());
        p.human = true;
        List<Player> players = List.of(p);

        int nombreJoueurs = setNbPlayers(scanner);
        createComputers(nombreJoueurs, players);

        CardPile pioche = new CardPile(Card.all().toList().shuffle());
        initGoldAndCards(players, pioche);
        Player crown = players.maxBy(Player::age).get();

        List<Group> roundAssociations;
        do {
            java.util.List<Player> list = players.asJavaMutable();
            Collections.rotate(list, -players.indexOf(crown));
            List<Player> playersInOrder = List.ofAll(list);
            RandomCharacterSelector randomCharacterSelector = new RandomCharacterSelector();
            List<Character> availableCharacters = List.of(Character.ASSASSIN, Character.THIEF, Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD, Character.ABBE, Character.ARTIST, Character.ALCHEMIST);

            List<Character> availableCharacters1 = availableCharacters;
            List<Character> discardedCharacters;
            discardedCharacters = getDiscardedCharacters(playersInOrder.size(), randomCharacterSelector, availableCharacters1);
            Character faceDownDiscardedCharacter = discardedCharacters.head();
            availableCharacters = availableCharacters.remove(faceDownDiscardedCharacter);

            List<Character> availableCharacters11 = availableCharacters.remove(Character.KING);
            List<Character> discardedCharacters1;
            discardedCharacters1 = getDiscardedCharacters(randomCharacterSelector, availableCharacters11);
            List<Character> faceUpDiscardedCharacters = discardedCharacters1;
            availableCharacters = availableCharacters.removeAll(faceUpDiscardedCharacters);

            List<Group> associations = getAssociations(playersInOrder, availableCharacters, faceDownDiscardedCharacter, faceUpDiscardedCharacters);
            GameRoundAssociations groups = new GameRoundAssociations(associations);

            playARound(pioche, associations, groups);
            roundAssociations = associations;
            crown = roundAssociations.find(a -> a.character == Character.KING).map(Group::player).getOrElse(crown);
        } while (!players.map(Player::city).exists(City::isComplete));

        // classe les joueurs par leur score
        // si ex-aequo, le premier est celui qui n'est pas assassiné
        // si pas d'assassiné, le gagnant est le joueur ayant eu le personnage avec le numéro d'ordre le plus petit au dernier tour
        System.out.println("Classement: " + roundAssociations.sortBy(a -> Tuple.of(a.player().score(), !a.isMurdered(), a.character))
                .reverse()
                .map(Group::player));
    }

    private static void playARound(CardPile pioche, List<Group> associations, GameRoundAssociations groups) {
        for (int iii = 0; iii < 8; iii++) {
            for (int ii = 0; ii < associations.size(); ii++) {
                if (iii + 1 == associations.get(ii).character.number()) {
                    if (associations.get(ii).isMurdered()) {}else{
                        Group group = associations.get(ii);
                        associations.get(ii).thief().peek(thief -> thief.steal(group.player()));
                        Set<String> baseActions = HashSet.of(DRAW_2_CARDS, RECEIVE_2_COINS);
                        List<District> districts = group.player().city().districts();
                        Set<String> availableActions = baseActions;
                        for (District d : districts) {
                            if (d == District.OBSERVATORY) {
                                availableActions = availableActions.replace(DRAW_2_CARDS, DRAW_3_CARDS);
                            }
                        }
                        // keep only actions that player can realize
                        List<String> possibleActions = List.empty();
                        for (String action : availableActions) {
                            if (action == DRAW_2_CARDS) {
                                if (pioche.canDraw(2))
                                    possibleActions = possibleActions.append(DRAW_2_CARDS);
                            }
                            else if (action == DRAW_3_CARDS) {
                                if (pioche.canDraw(3))
                                    possibleActions = possibleActions.append(DRAW_2_CARDS);
                            }
                            else {
                                possibleActions = possibleActions.append(action);
                            }
                        }
                        String actionType = group.player().controller.selectActionAmong(possibleActions.toList());
                        // execute selected action
                        if (actionType == DRAW_2_CARDS) {
                            Set<Card> cardsDrawn = pioche.draw(2);
                            if (!group.player().city().has(District.LIBRARY)) {
                                Card keptCard = group.player().controller.selectCardAmong(cardsDrawn);
                                pioche.discard(cardsDrawn.remove(keptCard).toList());
                                cardsDrawn = HashSet.of(keptCard);
                            }
                            group.player().add(cardsDrawn);
                        }
                        else if (actionType == RECEIVE_2_COINS) {
                            group.player().add(2);
                        }
                        else if (actionType == DRAW_3_CARDS) {
                            Set<Card> cardsDrawn = pioche.draw(3);
                            if (!group.player().city().has(District.LIBRARY)) {
                                Card keptCard = group.player().controller.selectCardAmong(cardsDrawn);
                                pioche.discard(cardsDrawn.remove(keptCard).toList());
                                cardsDrawn = HashSet.of(keptCard);
                            }
                            group.player().add(cardsDrawn);
                        }
                        printAction(group, actionType, associations);

                        // receive powers from the character
                        List<String> powers = null;
                        if (group.character == Character.ABBE){
                            powers = List.of(RECEIVE_INCOME, RECEIVE_1_GOLD_FROM_THE_RICHEST_PLAYER);
                        }
                        if (group.character == Character.ASSASSIN) {
                            powers = List.of(KILL);
                        }
                        else if (group.character == Character.THIEF) {
                            powers = List.of(ROB);
                        }
                        else if (group.character == Character.MAGICIAN) {
                            powers = List.of(EXCHANGE_CARDS_WITH_OTHER_PLAYER, EXCHANGE_CARDS_WITH_PILE);
                        }
                        else if (group.character == Character.KING) {
                            powers = List.of(RECEIVE_INCOME);
                        }
                        else if (group.character == Character.BISHOP) {
                            powers = List.of(RECEIVE_INCOME);
                        }
                        else if (group.character == Character.MERCHANT) {
                            powers = List.of(RECEIVE_INCOME, RECEIVE_1_GOLD);
                        }
                        else if (group.character == Character.ARCHITECT) {
                            powers = List.of(PICK_2_CARDS, BUILD_DISTRICT, BUILD_DISTRICT);
                        }
                        else if (group.character == Character.WARLORD) {
                            powers = List.of(RECEIVE_INCOME, DESTROY_DISTRICT);
                        }
                        else if (group.character == Character.ARTIST) {
                            powers = List.of(BEAUTIFY_DISTRICT, BEAUTIFY_DISTRICT);
                        }
                        else {
                            System.out.println("Uh oh");
                        }
                        List<String>  extraActions = List.empty();
                        for (District d : group.player().city().districts()) {
                            if (d == District.SMITHY) {
                                extraActions = extraActions.append(DRAW_3_CARDS_FOR_2_COINS);
                            }
                            if (d == District.LABORATORY) {
                                extraActions = extraActions.append(DISCARD_CARD_FOR_2_COINS);
                            }
                        }
                        Set<String> availableActions11 = Group.OPTIONAL_ACTIONS
                                .addAll(powers)
                                .addAll(extraActions);
                        String actionType11;
                        do {
                            Set<String> availableActions1 = availableActions11;
                            // keep only actions that player can realize
                            List<String> possibleActions2 = List.empty();
                            for (String action : availableActions1) {
                                if (action == BUILD_DISTRICT) {
                                    if (!group.player().buildableDistrictsInHand().isEmpty())
                                        possibleActions2 = possibleActions2.append(BUILD_DISTRICT);
                                }
                                else if (action == DESTROY_DISTRICT) {
                                    if (DestroyDistrictAction.districtsDestructibleBy(groups, group.player()).exists(districtsByPlayer -> !districtsByPlayer._2().isEmpty())) {
                                        possibleActions2 = possibleActions2.append(DESTROY_DISTRICT);
                                    }
                                }
                                else if (action == DISCARD_CARD_FOR_2_COINS) {
                                    if (!group.player().cards().isEmpty()) {
                                        possibleActions2 = possibleActions2.append(DISCARD_CARD_FOR_2_COINS);
                                    }
                                }
                                else if (action == DRAW_3_CARDS_FOR_2_COINS) {
                                    if (pioche.canDraw(3) && group.player().canAfford(2))
                                        possibleActions2 = possibleActions2.append(DRAW_3_CARDS_FOR_2_COINS);
                                }
                                else if (action == EXCHANGE_CARDS_WITH_PILE) {
                                    if (!group.player().cards().isEmpty() && pioche.canDraw(1)) {
                                        possibleActions2 = possibleActions2.append(EXCHANGE_CARDS_WITH_PILE);
                                    }
                                }
                                else if (action == PICK_2_CARDS) {
                                    if (pioche.canDraw(2))
                                        possibleActions2 = possibleActions2.append(PICK_2_CARDS);
                                }
                                else
                                    possibleActions2 = possibleActions2.append(action);
                            }
                            String actionType1 = group.player().controller.selectActionAmong(possibleActions2.toList());
                            // execute selected action
                            if (actionType1 == END_ROUND)
                            {} else if (actionType1 == BUILD_DISTRICT) {
                                Card card = group.player().controller.selectCardAmong(group.player().buildableDistrictsInHand());
                                group.player().buildDistrict(card);
                                if (group.character == Character.ALCHEMIST) {
                                    group.player().add(card.district().cost());
                                }
                            }
                            else if (actionType1 == DISCARD_CARD_FOR_2_COINS) {
                                Player player = group.player();
                                Card card = player.controller.selectCardAmong(player.cards());
                                player.cards = player.cards().remove(card);
                                pioche.discard(card);
                                player.add(2);
                            }
                            else if (actionType1 == DRAW_3_CARDS_FOR_2_COINS) {
                                group.player().add(pioche.draw(3));
                                group.player().pay(2);
                            }
                            else if (actionType1 == EXCHANGE_CARDS_WITH_PILE) {
                                Set<Card> cardsToSwap = group.player().controller.selectManyAmong(group.player().cards());
                                group.player().cards = group.player().cards().removeAll(cardsToSwap);
                                group.player().add(pioche.swapWith(cardsToSwap.toList()));
                            }
                            else if (actionType1 == EXCHANGE_CARDS_WITH_OTHER_PLAYER) {
                                Player playerToSwapWith = group.player().controller.selectPlayerAmong(groups.associations.map(Group::player).remove(group.player()));
                                group.player().exchangeHandWith(playerToSwapWith);
                            }
                            else if (actionType1 == KILL) {
                                Character characterToMurder = group.player().controller.selectCharacterAmong(List.of(Character.ASSASSIN, Character.THIEF, Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD, Character.ABBE, Character.ARTIST, Character.ALCHEMIST));
                                groups.associationToCharacter(characterToMurder).peek(Group::murder);
                            }
                            else if (actionType1 == PICK_2_CARDS) {
                                group.player().add(pioche.draw(2));
                            }
                            else if (actionType1 == RECEIVE_2_COINS) {
                                group.player().add(2);
                            }
                            else if (actionType1 == RECEIVE_1_GOLD) {
                                group.player().add(1);
                            }
                            else if (actionType1 == RECEIVE_1_GOLD_FROM_THE_RICHEST_PLAYER){
                                AbbeActions.getOneGoldFromRichest(associations);
                            }
                            else if (actionType1 == RECEIVE_INCOME) {
                                DistrictType type = null;
                                if (group.character == Character.BISHOP || group.character == Character.ABBE) {
                                    type = DistrictType.RELIGIOUS;
                                }
                                else if (group.character == Character.WARLORD) {
                                    type = DistrictType.MILITARY;
                                }
                                else if (group.character == Character.KING) {
                                    type = DistrictType.NOBLE;
                                }
                                else if (group.character == Character.MERCHANT) {
                                    type = DistrictType.TRADE;
                                }
                                if (type != null) {
                                    for (District d : group.player().city().districts()) {
                                        if (d.districtType() == type) {
                                            group.player().add(1);
                                        }
                                        if (d == District.MAGIC_SCHOOL) {
                                            group.player().add(1);
                                        }
                                    }
                                }
                            }
                            else if (actionType1 == DESTROY_DISTRICT) {
                                /* List<Player> players = List.empty();
                                for(Group association : associations){
                                    players.append(association.player());
                                }
                                Player player = group.player().controller.selectPlayerAmong(players); // Choix joueur pour détruire
                                City city = player.city();
                                Set<Card> cards = null;
                                for(DestructibleDistrict district : city.districtsDestructibleBy(player)){
                                    cards.add(district.card());
                                }
                                Card card = group.player().controller.selectCardAmong(cards);
                                player.city().destroyDistrict(card);
                                group.player().pay(card.district().cost()-1); */ // Ne marche pas, renvoie nullPointer ou boucle infinie
                                Map<Player, List<DestructibleDistrict>> districtsDestructible = DestroyDistrictAction.districtsDestructibleBy(groups, group.player());
                                DestructibleDistrict districtToDestruct = group.player().controller.selectDistrictToDestroyAmong(districtsDestructible);
                                group.player().pay(districtToDestruct.destructionCost());
                                Player playerDestructed = null;
                                for(Player p : districtsDestructible.keySet()){
                                    for(Card c : p.cards()){
                                        if(c.equals(districtToDestruct.card())){
                                            playerDestructed = p;
                                        }
                                    }
                                }
                                playerDestructed.city().destroyDistrict(districtToDestruct.card()); // Ne marche pas, renvoie nullPointerException
                            }
                            else if (actionType1 == ROB) {
                                Character character = group.player().controller.selectCharacterAmong(List.of(Character.MAGICIAN, Character.KING, Character.BISHOP, Character.MERCHANT, Character.ARCHITECT, Character.WARLORD, Character.ALCHEMIST, Character.ABBE, Character.ARTIST)
                                        .removeAll(groups.associations.find(Group::isMurdered).map(Group::character)));
                                groups.associationToCharacter(character).peek(association -> association.stolenBy(group.player()));
                            }
                            else if (actionType1 == BEAUTIFY_DISTRICT){
                                DestructibleDistrict district = group.player().controller.selectDestructibleDistrictAmong(group.player().city().districtsDestructibleBy(group.player()));
                                // monter prix de destruction du quartier de 1
                                district.setDestructionCost(district.destructionCost()+1);
                            }
                            printAction(group, actionType1, associations);
                            actionType11 = actionType1;
                            availableActions11 = availableActions11.remove(actionType11);
                        }
                        while (!availableActions11.isEmpty() && actionType11 != END_ROUND);
                    }
                }
            }
        }
    }

    private static List<Group> getAssociations(List<Player> listeJoueurs, List<Character> availableCharacters, Character faceDownDiscardedCharacter, List<Character> faceUpDiscardedCharacters) {
        List<Group> associations1 = List.empty();
        for (Player player : listeJoueurs) {
            System.out.println(player.name() + " doit choisir un personnage");
            availableCharacters = availableCharacters.size() == 1 && listeJoueurs.size() == 7 ? availableCharacters.append(faceDownDiscardedCharacter) : availableCharacters;
            Character selectedCharacter = player.controller.selectOwnCharacter(availableCharacters, faceUpDiscardedCharacters);
            availableCharacters = availableCharacters.remove(selectedCharacter);
            associations1 = associations1.append(new Group(player, selectedCharacter));
        }
        return associations1;
    }

    private static List<Character> getDiscardedCharacters(int tailleListeJoueurs, RandomCharacterSelector randomCharacterSelector, List<Character> availableCharacters) {
        List<Character> discardedCharacters = List.empty();
        for (int i = 0; i < 7 - tailleListeJoueurs - 1; i++) {
            Character discardedCharacter = randomCharacterSelector.among(availableCharacters);
            discardedCharacters = discardedCharacters.append(discardedCharacter);
            availableCharacters = availableCharacters.remove(discardedCharacter);
        }
        return discardedCharacters;
    }

    private static List<Character> getDiscardedCharacters(RandomCharacterSelector randomCharacterSelector, List<Character> availableCharacters){
        return getDiscardedCharacters(5, randomCharacterSelector, availableCharacters);
    }

    private static void initGoldAndCards(List<Player> players, CardPile pioche) {
        players.forEach(player -> {
            player.add(2);
            player.add(pioche.draw(2));
        });
    }


    public static void printAction(Group association, String actionType, List<Group> associations) {
        System.out.println("Player " + association.player().name() + " executed action " + actionType);
        associations.map(Group::player)
                .forEach(Citadels::printStatus);
    }

    private static void printStatus(Player player) {
        System.out.println("  Player " + player.name() + ":");
        System.out.println("    Gold coins: " + player.gold());
        System.out.println("    City: " + printDistricts(player));
        System.out.println("    Hand size: " + player.cards().size());
        if (player.controller instanceof HumanController) {
            System.out.println("    Hand: " + printHand(player));
        }
        System.out.println();
    }

    private static String printDistricts(Player player) {
        List<District> districts = player.city().districts();
        return districts.isEmpty() ? "Empty" : districts.map(Citadels::printDistrict).mkString(", ");
    }

    private static String printDistrict(District district) {
        return district.name() + "(" + district.districtType().name() + ", " + district.cost() + ")";
    }

    private static String printHand(Player player) {
        Set<Card> cards = player.cards();
        return cards.isEmpty() ? "Empty" : cards.map(Citadels::printCard).mkString(", ");
    }

    private static String printCard(Card card) {
        return printDistrict(card.district());
    }

    private static int setNbPlayers(Scanner scanner){
        System.out.println("Saisir le nombre de joueurs total (entre 2 et 8): ");
        int nombreJoueurs;
        do {
            nombreJoueurs = scanner.nextInt();
        } while (nombreJoueurs < 2 || nombreJoueurs > 8);
        return nombreJoueurs;
    }

    private static void createComputers(int nombreOrdinateurs, List<Player> players){
        for (int joueurs = 0; joueurs < nombreOrdinateurs; joueurs ++) {
            Player player = new Player("Computer " + joueurs, 35, new City(board), new ComputerController());
            player.computer = true;
            players = players.append(player);
        }
    }
}

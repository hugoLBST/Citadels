package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import io.vavr.collection.HashSet;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CityTest {
    @Test
    public void test_cout_construction_quartiers(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        int score = city.score(possession);
        assertThat(score).isEqualTo(5);
    }
    @Test
    public void test_quartiers_5_types(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city.buildDistrict(Card.MANOR_5); // +3 score, type : NOBLE
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score, type : MILITARY
        city.buildDistrict(Card.TAVERN_5); // +1 score, type : TRADE
        city.buildDistrict(Card.CHURCH_3); // +2 score, type : RELIGIOUS
        city.buildDistrict(Card.KEEP_1); // +3 score, type : SPECIAL
        int score = city.score(possession);
        assertThat(score).isEqualTo(13); // Normalement : 10 + Bonus 5 types : 3 -> 13 au total
    }
    @Test
    public void test_premier_joueur(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
        int score = city.score(possession);
        assertThat(score).isEqualTo(23); // 19 points + 4 points -> 23 au total

    }
    @Test
    public void test_pas_premier_joueur(){
        Board board = new Board();
        City city1 = new City(board);
        Possession possession1 = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city1.buildDistrict(Card.MANOR_5); // +3 score
        city1.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city1.buildDistrict(Card.TAVERN_5); // +1 score
        city1.buildDistrict(Card.CHURCH_3); // +2 score
        city1.buildDistrict(Card.TRADING_POST_1); // +2 score
        city1.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city1.buildDistrict(Card.TOWN_HALL_2); // +5 score
        City city2 = new City(board);
        Possession possession2 = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city2.buildDistrict(Card.MANOR_5); // +3 score
        city2.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city2.buildDistrict(Card.TAVERN_5); // +1 score
        city2.buildDistrict(Card.CHURCH_3); // +2 score
        city2.buildDistrict(Card.TRADING_POST_1); // +2 score
        city2.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city2.buildDistrict(Card.TOWN_HALL_2); // +5 score
        int score = city2.score(possession2);
        assertThat(score).isEqualTo(21); // 19 points + 2 points -> 21 au total

    }
    @Test
    public void test_bonus_merveilles(){
        Board board = new Board();
        City city = new City(board);
        Possession possession = new Possession(10, HashSet.empty()); // 10 or donc +10 score
        city.buildDistrict(Card.DRAGON_GATE); // 6 + 2 points bonus
        city.buildDistrict(Card.UNIVERSITY); // 6 + 2 points bonus
        city.buildDistrict(Card.TREASURY); // 5 + 10 points vu que 10 pièces d'or
        city.buildDistrict(Card.MAP_ROOM); // 5 + 4 cartes dans la hand
        int score = city.score(possession);
        assertThat(score).isEqualTo(40);
    }
}

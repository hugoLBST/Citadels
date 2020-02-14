package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import io.vavr.collection.HashSet;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class CityTest {

    public Board board;
    public City city;

    @Before
    public void setUp() throws Exception {
       board = new Board();
       city = new City(board);
    }

    @Test
    public void it_should_test_building_district_cost(){
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city.buildDistrict(Card.MANOR_5); // +3 score
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        int score = city.score(possession);
        assertThat(score).isEqualTo(5);
    }
    @Test
    public void it_should_test_5_district_types_bonus(){
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
    public void it_should_test_first_completed_city(){
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
    public void it_should_test_second_completed_city_bonus(){
        Possession possession1 = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        city.buildDistrict(Card.MANOR_5); // +1 score
        city.buildDistrict(Card.WATCHTOWER_2); // +1 score
        city.buildDistrict(Card.TAVERN_5); // +1 score
        city.buildDistrict(Card.CHURCH_3); // +2 score
        city.buildDistrict(Card.TRADING_POST_1); // +2 score
        city.buildDistrict(Card.TOWN_HALL_1); // +5 score
        city.buildDistrict(Card.TOWN_HALL_2); // +5 score
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
    public void it_should_test_Dragon_Gate_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or
        city.buildDistrict(Card.DRAGON_GATE); // 6 + 2 points bonus
        int score = city.score(possession);
        assertThat(score).isEqualTo(8);
    }

    @Test
    public void it_should_test_University_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or
        city.buildDistrict(Card.UNIVERSITY); // 6 + 2 points bonus
        int score = city.score(possession);
        assertThat(score).isEqualTo(8);
    }

    @Test
    public void it_should_test_Treasury_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or donc +10 score
        city.buildDistrict(Card.TREASURY); // 5 + 10 points vu que 10 pièces d'or
        int score = city.score(possession);
        assertThat(score).isEqualTo(15);
    }

    @Test
    public void it_should_test_Map_Room_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or donc +10 score
        city.buildDistrict(Card.DRAGON_GATE); // 6 + 2 points bonus
        city.buildDistrict(Card.UNIVERSITY); // 6 + 2 points bonus
        city.buildDistrict(Card.TREASURY); // 5 + 10 points vu que 10 pièces d'or
        city.buildDistrict(Card.MAP_ROOM); // 5 + 4 cartes dans la hand
        int score = city.score(possession);
        assertThat(score).isEqualTo(40);
    }
}

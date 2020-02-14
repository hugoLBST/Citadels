package com.montaury.citadels;

import com.montaury.citadels.district.Card;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class CityTest {

    public Board board;
    public static City city;

    @Before
    public void setUp() {
       board = new Board();
       city = new City(board);
    }

    @Test
    public void it_should_test_building_district_cost(){
        Possession possession = new Possession(0, HashSet.empty()); // 0 or donc 0 score
        buildDistricts(List.of(Card.MANOR_5, Card.WATCHTOWER_2, Card.TAVERN_5)); // score +1 +1 +3
        int score = city.score(possession);
        assertThat(score).isEqualTo(5);
    }
    @Test
    public void it_should_test_5_district_types_bonus(){
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        buildDistricts(List.of(Card.MANOR_5, Card.WATCHTOWER_2, Card.TAVERN_5, Card.CHURCH_3,Card.KEEP_1)); // score +3 +1 +1 +2 +3
        int score = city.score(possession);
        assertThat(score).isEqualTo(13); // Normalement : 10 + Bonus 5 types : 3 -> 13 au total
    }
    @Test
    public void it_should_test_first_completed_city(){
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        buildDistricts(List.of(Card.MANOR_5, Card.WATCHTOWER_2, Card.TAVERN_5, Card.CHURCH_3,Card.TRADING_POST_1,Card.TOWN_HALL_1,Card.TOWN_HALL_2)); // score +3 +1 +1 +2 +2 +5 +5
        int score = city.score(possession);
        assertThat(score).isEqualTo(23); // 19 points + 4 points -> 23 au total

    }
    @Test
    public void it_should_test_second_completed_city_bonus(){
        Possession possession1 = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        buildDistricts(List.of(Card.MANOR_5, Card.WATCHTOWER_2, Card.TAVERN_5, Card.CHURCH_3,Card.TRADING_POST_1,Card.TOWN_HALL_1,Card.TOWN_HALL_2)); // score +3 +1 +1 +2 +2 +5 +5
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
        buildDistricts(List.of(Card.DRAGON_GATE)); // score 6 +2(bonus)

        int score = city.score(possession);
        assertThat(score).isEqualTo(8);
    }

    @Test
    public void it_should_test_University_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or
        buildDistricts(List.of(Card.UNIVERSITY)); // score 6 +2(bonus)
        int score = city.score(possession);
        assertThat(score).isEqualTo(8);
    }

    @Test
    public void it_should_test_Treasury_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or donc +10 score
        buildDistricts(List.of(Card.TREASURY)); // score 5 + 10(bonus)
        int score = city.score(possession);
        assertThat(score).isEqualTo(15);
    }

    @Test
    public void it_should_test_Map_Room_wonder_bonus(){
        Possession possession = new Possession(10, HashSet.empty()); // 10 or donc +10 score
        buildDistricts(List.of(Card.DRAGON_GATE,Card.UNIVERSITY,Card.TREASURY,Card.MAP_ROOM)); // score 6 +2(bonus) + 6+2(bonus) + 5 +10(bonus) + 5 + 4(bonus)
        int score = city.score(possession);
        assertThat(score).isEqualTo(40);
    }

    @Test
    public void it_should_test_haunted_city_and_other_districts(){
        Possession possession = new Possession(0,HashSet.empty()); // 0 or donc 0 score
        buildDistricts(List.of(Card.TEMPLE_1,Card.WATCHTOWER_1,Card.TAVERN_3,Card.KEEP_1,Card.HAUNTED_CITY)); // score +1 +1 +1 +3 +2
        int score = city.score(possession);
        assertThat(score).isEqualTo(11); // 8 points + 3 (bonus)

    }

    public static void buildDistricts(List<Card> cardList){

        for(int i = 0; i<cardList.size(); i++)
        {
            city.buildDistrict(cardList.get(i));
        }
    }
}

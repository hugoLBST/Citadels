package com.montaury.citadels;

import com.montaury.citadels.character.Character;
import com.montaury.citadels.district.Card;
import com.montaury.citadels.player.HumanController;
import com.montaury.citadels.player.Player;
import com.montaury.citadels.round.Group;
import io.vavr.collection.List;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CitadelsTest {
    public Board board;
    public static City city;

    @Before
    public void setUp() {
        board = new Board();
        city = new City(board);
    }
    @Test
    public void if_I_destroy_a_district_it_shouldnt_appear_in_list(){
        city.buildDistrict(Card.MONASTERY_1);
        city.destroyDistrict(Card.MONASTERY_1);
        assertThat(city.districtsDestructibleBy(new Player("test", 99, city, new HumanController()))).isEqualTo(List.empty());
    }
    @Test
    public void keep_shouldnt_be_destroyable(){
        city.buildDistrict(Card.KEEP_1);
        assertThat(city.districtsDestructibleBy(new Player("test", 99, city, new HumanController()))).isEqualTo(List.empty());
    }
    @Test
    public void is_should_cost_0_to_destroy_tavern(){
        city.buildDistrict(Card.TAVERN_1);
        assertThat(city.districtsDestructibleBy(new Player("test", 99, city, new HumanController()))).isNotEqualTo(List.empty()); // peut détruire car coute 0
    }
    @Test
    public void the_player_shouldnt_have_enough_money_to_destroy_monastery(){
        city.buildDistrict(Card.MONASTERY_1);
        assertThat(city.districtsDestructibleBy(new Player("test", 99, city, new HumanController()))).isEqualTo(List.empty()); // ne peut pas détruire car le joueur n'a pas d'argent
    }
    @Test
    public void two_gold_should_be_taken_when_destroying(){
        Player player1 = new Player("test", 99, city, new HumanController());
        player1.add(2); // on donne 2 gold au joueur
        player1.city().buildDistrict(Card.MANOR_5); // cout de construction = 3 / cout de destruction = 2
        player1.city().destroyDistrict(Card.MANOR_5);
        player1.pay(Card.MANOR_5.district().cost()-1);
        assertThat(player1.gold()).isEqualTo(0);
    }
    @Test
    public void only_the_warlord_can_destroy(){
        Player player1 = new Player("test", 99, city, new HumanController());
        Player player2 = new Player("test", 99, city, new HumanController());
        Group group1 = new Group(player1, Character.WARLORD); // peut détruire
        Group group2 = new Group(player2, Character.KING); // ne peut pas détruire
        group1.player().city().buildDistrict(Card.MANOR_5);
        assertThat(group1.player().city().districtsDestructibleBy(group1.player())).isNotEqualTo(List.empty());
        group2.player().city().buildDistrict(Card.MANOR_5);
        assertThat(group2.player().city().districtsDestructibleBy(group2.player())).isEqualTo(List.empty());
    }
}

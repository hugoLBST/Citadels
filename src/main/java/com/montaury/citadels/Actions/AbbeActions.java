package com.montaury.citadels.Actions;

import com.montaury.citadels.character.Character;
import com.montaury.citadels.player.Player;
import com.montaury.citadels.round.Group;
import io.vavr.collection.List;

public class AbbeActions {
    public static int getOneGoldFromRichest(List<Group> associations){
        Player richestPlayer = null;
        Character richestCharacter;
        boolean moreThanOneRichest = false;
        boolean characterIsAbbe = false;
        int oldMaxGold = 0;
        for(Group groupPlayer : associations) {
            if (groupPlayer.player().gold() > oldMaxGold) {

                richestCharacter = groupPlayer.character();
                richestPlayer = groupPlayer.player();

                oldMaxGold = groupPlayer.player().gold();

                if (richestCharacter == Character.ABBE) {
                    characterIsAbbe = true;
                }

            } else if (groupPlayer.player().gold() == oldMaxGold) {
                moreThanOneRichest = true;
            }
        }
            if(!characterIsAbbe && !moreThanOneRichest){
                if(richestPlayer != null)
                    richestPlayer.pay(1);
                return 1;
            } else {
                return 0;
            }
    }
}

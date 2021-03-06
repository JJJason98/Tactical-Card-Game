package structures.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.units.unitBlazeHound;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardBlazeHound extends Card{
    public void UseCard(ActorRef out, GameState gameState, Tile tile) {
        BasicCommands.addPlayer1Notification(out, "summoned  Blaze Hound on tile " + "[" + (tile.getTilex() + 1) + ", " + (tile.getTiley() + 1) + " ].", 3);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, summon, tile);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_blaze_hound, gameState.getNewUnitID(), unitBlazeHound.class));
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(tile);
        BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(4);
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(3);
        BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 4);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 3);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(tile);
        tile.setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setName("Blaze Hound");
        gameState.setNewUnitID(gameState.getNewUnitID()+1);
        //the first turn that the unit summoned, it cannot move or attack
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttacked(true);
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setMoved(true);
        // minus player mana
        gameState.getBotPlayer().setMana(gameState.getBotPlayer().getMana() - this.manacost);
        BasicCommands.setPlayer2Mana(out, gameState.getBotPlayer());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //remove card
        gameState.setCurrentCard(null);
        gameState.getBotHand().remove(this);

        //both player draw 1 card
        if (!gameState.getHumanDeck().isEmpty()){
            gameState.addHumanHand(gameState.getHumanDeck().get(0));
            gameState.getHumanDeck().remove(0);
            BasicCommands.drawCard(out, gameState.getHumanHand().get(gameState.getHumanHand().size()-1), gameState.getHumanHand().size(),0); //show the card on the screen's humanHand part
            try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
            int j = 1;
            for (Card tmpCard : gameState.getHumanHand()){
                BasicCommands.drawCard(out, tmpCard, j++,0); //show the card on the screen's humanHand part
                try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
        if (!gameState.getBotDeck().isEmpty()){
            gameState.addBotHand(gameState.getBotDeck().get(0));
            gameState.getBotDeck().remove(0); //remove the first card from botDeck
        }


        return;
    }

}

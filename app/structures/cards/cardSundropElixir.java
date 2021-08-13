package structures.cards;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardSundropElixir extends Card {
    public boolean tileInCardRange(Tile tile, GameState gameState){
        if (gameState.getHumanPlayer().getMana() < this.manacost) return false;
        if (tile.getUnit() == null) return false;
        else return true;
    }
    public void UseCard(ActorRef out, GameState gameState, Tile tile){
        BasicCommands.addPlayer1Notification(out, "You spelled Sundrop Exlixir on unit " + "["+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 3);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        //add health
        tile.getUnit().setHealth(tile.getUnit().getHealth() + 5);
        // can't exceed initial health
        int currentHealth = tile.getUnit().getHealth();
        if (tile.getUnit().getName().equals("Comodo Charger") && currentHealth > 3) tile.getUnit().setHealth(3);
        else if (tile.getUnit().getName().equals("Hailstone Golem") && currentHealth > 6) tile.getUnit().setHealth(6);
        else if (tile.getUnit().getName().equals("Pureblade Enforcer") && currentHealth > 4) tile.getUnit().setHealth(4);
        else if (tile.getUnit().getName().equals("Azure Herald") && currentHealth > 4) tile.getUnit().setHealth(4);
        else if (tile.getUnit().getName().equals("Silverguard Knight") && currentHealth > 5) tile.getUnit().setHealth(5);
        else if (tile.getUnit().getName().equals("Azurite Lion") && currentHealth > 3) tile.getUnit().setHealth(3);
        else if (tile.getUnit().getName().equals("Fire Spitter") && currentHealth > 2) tile.getUnit().setHealth(2);
        else if (tile.getUnit().getName().equals("Ironcliff Guardian") && currentHealth > 10) tile.getUnit().setHealth(10);
        else if (tile.getUnit().getName().equals("Planar Scout") && currentHealth > 1) tile.getUnit().setHealth(1);
        else if (tile.getUnit().getName().equals("Rock Pulveriser") && currentHealth > 4) tile.getUnit().setHealth(4);
        else if (tile.getUnit().getName().equals("Pyromancer") && currentHealth > 1) tile.getUnit().setHealth(1);
        else if (tile.getUnit().getName().equals("Bloodshard Golem") && currentHealth > 3) tile.getUnit().setHealth(3);
        else if (tile.getUnit().getName().equals("Blaze Hound") && currentHealth > 3) tile.getUnit().setHealth(3);
        else if (tile.getUnit().getName().equals("Windshrike") && currentHealth > 3) tile.getUnit().setHealth(3);
        else if (tile.getUnit().getName().equals("Serpenti") && currentHealth > 4) tile.getUnit().setHealth(4);
        else if (tile.getUnit().getId() == 0 && currentHealth > 20) tile.getUnit().setHealth(20);
        else if (tile.getUnit().getId() == 1 && currentHealth > 20) tile.getUnit().setHealth(20);
        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tile.getUnit().getId() == 1) {
            gameState.setBotPlayerHealth(tile.getUnit().getHealth());
            BasicCommands.setPlayer2Health(out, gameState.getBotPlayer());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (tile.getUnit().getHealth() <= 0) {
            tile.getUnit().dieUnit(out, gameState);
        }
        // minus player mana
        gameState.getHumanPlayer().setMana(gameState.getHumanPlayer().getMana() - this.manacost);
        BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
        try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        //remove card
        gameState.setCurrentCard(null);
        for (int i = 0; i < gameState.getHumanHand().size(); i++){
            BasicCommands.deleteCard(out, i+ 1);
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        }
        gameState.getHumanHand().remove(this);
        //redraw hand cards
        int j = 1;
        for (Card tmpCard : gameState.getHumanHand()){
            BasicCommands.drawCard(out, tmpCard, j++,0); //show the card on the screen's humanHand part
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
        }
        // cancel all tile highlights
        for (int i = 0; i < 9; i++){
            for (int k = 0; k < 5; k++){
                BasicCommands.drawTile(out, gameState.getBoard()[i][k], 0);
                try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
        return;
    }
}

package structures.cards;
import structures.basic.*;
import structures.basic.Unit;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardTruestrike extends Card{
    int manacost=1;
    public boolean tileInCardRange(Tile tile, GameState gameState){
        if (gameState.getHumanPlayer().getMana() < this.manacost) return false;
        if (tile.getUnit() == null) return false;
        if (tile.getUnit().getOwner().equals("human")) return false;
        else return true;
    }
    public void UseCard(ActorRef out, GameState gameState, Tile tile){
        BasicCommands.addPlayer1Notification(out, "You spelled Truestrike on unit " + "["+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 3);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        EffectAnimation inmolation = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_inmolation);
        BasicCommands.playEffectAnimation(out, inmolation, tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        //minus health
        tile.getUnit().setHealth(tile.getUnit().getHealth() - 2);

        BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tile.getUnit().getId() == 0) {
            gameState.setHumanPlayerHealth(tile.getUnit().getHealth());
            BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

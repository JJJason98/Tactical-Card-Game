package structures.cards;
import structures.basic.*;
import structures.units.*;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardAzureHerald extends Card{
	int manacost = 2;
	
	public void UseCard(ActorRef out, GameState gameState, Tile tile) {
		
		BasicCommands.addPlayer1Notification(out, "You summoned Azure Herald on tile [ "+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 1);
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
		//add 3 health to humanPlayer and human avatar, max to 20
	    if(gameState.getHumanPlayer().getHealth() <= 17){
	    	gameState.getHumanPlayer().setHealth(gameState.getHumanPlayer().getHealth()+3);
            BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

            gameState.getHumanUnits().get(0).setHealth(gameState.getHumanPlayer().getHealth());
            BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(0), gameState.getHumanPlayer().getHealth());
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

	    }else {
	    	gameState.getHumanPlayer().setHealth(20);
            BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
            try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

            gameState.getHumanUnits().get(0).setHealth(20);
            BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(0), 20);
            try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	    }
		
		// summon the unit
        EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, summon, tile);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.addHumanUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_azure_herald, gameState.getNewUnitID(), unitAzureHerald.class));
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setPositionByTile(tile);
        BasicCommands.drawUnit(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttack(1);
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setHealth(4);
        BasicCommands.setUnitAttack(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 1);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 4);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setTile(tile);
        tile.setUnit(gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1));
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setOwner("human");
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setName("Azure Herald");
        gameState.setNewUnitID(gameState.getNewUnitID()+1);

        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, gameState.getHumanUnits().get(0).getTile());
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        
        //the first turn that the unit summoned, it cannot move or attack
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttacked(true);
        gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setMoved(true);
        
        // minus player mana
        gameState.getHumanPlayer().setMana(gameState.getHumanPlayer().getMana() - manacost);
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

package structures.cards;

import structures.basic.*;
import structures.units.*;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.units.unitSerpenti;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardSerpenti extends Card{
	int manacost = 6;
	
	public void UseCard(ActorRef out, GameState gameState, Tile tile) {
		
		BasicCommands.addPlayer1Notification(out, "Enemy summoned  Rock Pulveriser on tile " + "[" + (tile.getTilex() + 1) + ", " + (tile.getTiley() + 1) + " ].", 3);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
        BasicCommands.playEffectAnimation(out, summon, tile);
        try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_serpenti, gameState.getNewUnitID(), unitSerpenti.class));
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(tile);
        BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(7);
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(4);
        BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 7);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 4);
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(tile);
        tile.setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setName("Serpenti");
        gameState.setNewUnitID(gameState.getNewUnitID()+1);
        
        //the first turn that the unit summoned, it cannot move or attack
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttacked(true);
        gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setMoved(true);
        
        // minus player mana
        gameState.getBotPlayer().setMana(gameState.getBotPlayer().getMana() - this.manacost);
        BasicCommands.setPlayer2Mana(out, gameState.getBotPlayer());
        try {Thread.sleep(1); } catch (InterruptedException e) {e.printStackTrace();}
        
        //remove card
        gameState.setCurrentCard(null);
        gameState.getBotHand().remove(this);

        return;
	}
}

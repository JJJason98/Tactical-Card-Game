package structures.cards;

import java.util.ArrayList;

import structures.basic.*;
import structures.units.*;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Tile;
import structures.units.unitAzuriteLion;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

public class cardAzuriteLion extends Card{
	int manacost = 3;
	
	public cardAzuriteLion() {
	}
	
	public void UseCard(ActorRef out, GameState gameState, Tile tile) {
		
		BasicCommands.addPlayer1Notification(out, "You summoned Azurite Lion on tile [ "+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 1);
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		
	    // summon the unit
		EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summon, tile);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
	    gameState.addHumanUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_azurite_lion, gameState.getNewUnitID(), unitAzuriteLion.class));
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setPositionByTile(tile);
	    BasicCommands.drawUnit(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), tile);
	    try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttack(2);
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setHealth(3);
	    BasicCommands.setUnitAttack(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 2);
	    try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	    BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 3);
	    try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setTile(tile);
	    tile.setUnit(gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1));
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setOwner("human");
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setName("Azurite Lion");
	    gameState.setNewUnitID(gameState.getNewUnitID()+1);
	         
	    //the first turn that the unit summoned, it cannot move or attack
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttacked(true);
	    gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setMoved(true);
	         
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

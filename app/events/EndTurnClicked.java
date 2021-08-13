package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.*;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * the end-turn button.
 * 
 * { 
 *   messageType = “endTurnClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class EndTurnClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		//reset all moved and attacked
		for (Unit tmpUnit : gameState.getHumanUnits()){
			tmpUnit.setMoved(false);
			tmpUnit.setAttacked(false);
		}

		// human player turn ends, start bot turn
		gameState.setCurrentPlayer("bot");
		if (gameState.getBotPlayer().getMana()+gameState.getTurnNum()+1 > 9) gameState.getBotPlayer().setMana(9);
		else gameState.getBotPlayer().setMana(gameState.getBotPlayer().getMana()+gameState.getTurnNum()+1 );
		BasicCommands.setPlayer2Mana(out, gameState.getBotPlayer());
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

		if (!gameState.getBotDeck().isEmpty()){
			gameState.addBotHand(gameState.getBotDeck().get(0)); // add the first card to botHand
//			System.out.println("Bot picks " + gameState.getBotDeck().get(0).getCardname());
			gameState.getBotDeck().remove(0); //remove the first card from botDeck
		}

		//  bot operation AI part, implemented in later sprint
		BasicCommands.addPlayer1Notification(out, "Enemy turn starts", 2);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		for (Unit tmpUnit : gameState.getBotUnits()){
			tmpUnit.setMoved(false);
			tmpUnit.setAttacked(false);
		}

		// First use card
		// Always use as much mana as possible, and use card with higher mana first
		while (gameState.getBotHand().size() != 0){
//			for (Card tc : gameState.getBotHand()){
//				System.out.println(tc.getCardname() + " :" +tc.getManacost());
//			}
			Card targetCard = null; // The card to be used, which has most mana cost and less than bot's current mana.
			int maxCost = 0;
			for (Card tmpCard : gameState.getBotHand()){
				//if (tmpCard.getCardname().equals("Entropic Decay") && gameState.getHumanUnits().size() == 1) continue;
				if (tmpCard.getManacost() <= gameState.getBotPlayer().getMana() && tmpCard.getManacost() > maxCost){
					if (tmpCard.getCardname().equals("Entropic Decay") && gameState.getHumanUnits().size() == 1) continue;
					maxCost = tmpCard.getManacost();
					targetCard = tmpCard;
				}
			}
			// if no valid card can be used, break
			if (targetCard == null) break;
//			else System.out.println("Target card: " + targetCard.getCardname()+ " " + targetCard.getManacost());
			// Decide the Tile that the card be used on.
			Tile targetTile = null;
			// For two spell cards
			if (targetCard.getCardname().equals("Staff of Y'Kir")){
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 5; j++) {
						if (gameState.getBoard()[i][j].getUnit() != null && gameState.getBoard()[i][j].getUnit().getId() == 1){
							targetTile = gameState.getBoard()[i][j];
						}
					}
				}
			}
			else if (targetCard.getCardname().equals("Entropic Decay")){
				int maxHealth = 0; // use on the enemy's non avatar unit which has max health
				for (Unit u : gameState.getHumanUnits()){
					if (u.getId() != 0 && u.getHealth() > maxHealth) {
						targetTile = u.getTile();
						maxHealth = u.getHealth();
					}
				}
			}
			// For unit cards, summon the unit where closest to the human avatar
			else {
				int minDistance = 100;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 5; j++) {
						if (Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) * Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) + Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) * Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) < minDistance && targetCard.tileInCardRange(gameState.getBoard()[i][j], gameState)){
							targetTile = gameState.getBoard()[i][j];
							minDistance = Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) * Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) + Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) * Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley());
						}
					}
				}
			}
			//use the card
			targetCard.UseCard(out, gameState, targetTile);
			try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// Do Move, move as close as they can to the human avatar (Pyromancer doesn't move)

		for (Unit tmpUnit : gameState.getBotUnits()){
			if (tmpUnit.getMoved()) continue;
			if (tmpUnit.getName().equals("Pyromancer")) continue;
			Tile targetTile = null;
			int minDistance = Math.abs(tmpUnit.getTile().getTilex() - gameState.getHumanUnits().get(0).getTile().getTilex()) * Math.abs(tmpUnit.getTile().getTilex() - gameState.getHumanUnits().get(0).getTile().getTilex()) + Math.abs(tmpUnit.getTile().getTiley() - gameState.getHumanUnits().get(0).getTile().getTiley()) * Math.abs(tmpUnit.getTile().getTiley() - gameState.getHumanUnits().get(0).getTile().getTiley());
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					if (tmpUnit.tileInMoveRange(gameState.getBoard()[i][j], gameState)){
						if (Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) * Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) + Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) * Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) < minDistance){
							targetTile = gameState.getBoard()[i][j];
							minDistance = Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) * Math.abs(i - gameState.getHumanUnits().get(0).getTile().getTilex()) + Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley()) * Math.abs(j - gameState.getHumanUnits().get(0).getTile().getTiley());
						}
					}
				}
			}
			if (targetTile != null) tmpUnit.moveUnit(out, gameState, targetTile);
		}



		//Attack the human avatar if they can, avatar attack last
		for (int m = 0; m < gameState.getBotUnits().size(); m++){
			Unit tmpUnit = gameState.getBotUnits().get(m);
			if (tmpUnit.getAttacked()) continue;
			Tile targetTile = null;
			if (tmpUnit.getId() != 1){
				boolean flag = false;
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 5; j++) {
						if (tmpUnit.tileInAttackRange(gameState.getBoard()[i][j], gameState)){
							if (gameState.getBoard()[i][j].getUnit().getId() == 0){
								targetTile = gameState.getBoard()[i][j];
								flag = true;
								break;
							}
							if (gameState.getBoard()[i][j].getUnit().getHealth() <= tmpUnit.getAttack()) {
								targetTile = gameState.getBoard()[i][j];
								flag = true;
								break;
							}
							targetTile = gameState.getBoard()[i][j];
						}
					}
					if (flag) break;
				}
				int sizeBeforeAttack = gameState.getBotUnits().size();
				if (targetTile != null) tmpUnit.attackUnit(out, gameState, targetTile);
				// if unit die, m--
				if (gameState.getBotUnits().size() != sizeBeforeAttack) m--;
				else if (tmpUnit.getName().equals("Serpenti")) m--;
			}
		}
		// The avatar only attacks when it can kill the enemy or it can attack human avatar
		Tile targetTile = null;
		boolean flag = false;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (gameState.getBotUnits().get(0).tileInAttackRange(gameState.getBoard()[i][j], gameState)){
					if (gameState.getBoard()[i][j].getUnit().getId() == 0){
						targetTile = gameState.getBoard()[i][j];
						flag = true;
						break;
					}
					if (gameState.getBoard()[i][j].getUnit().getHealth() <= gameState.getBotUnits().get(0).getAttack()) {
						targetTile = gameState.getBoard()[i][j];
					}
				}
			}
			if (flag) break;
		}
		if (targetTile != null) gameState.getBotUnits().get(0).attackUnit(out, gameState, targetTile);

		// bot player turn ends, start player turn
		gameState.setTurnNum(gameState.getTurnNum()+1); // next turn
		gameState.setCurrentPlayer("human");
		BasicCommands.addPlayer1Notification(out, "Enemy turn ends, now it's your turn!", 2);
		try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		if (gameState.getHumanPlayer().getMana()+gameState.getTurnNum()+1 > 9) gameState.getHumanPlayer().setMana(9);
		else gameState.getHumanPlayer().setMana(gameState.getHumanPlayer().getMana()+gameState.getTurnNum()+1 );
		BasicCommands.setPlayer1Mana(out, gameState.getHumanPlayer());
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

		if (!gameState.getHumanDeck().isEmpty()){
			gameState.addHumanHand(gameState.getHumanDeck().get(0)); // add the first card to botHand
			gameState.getHumanDeck().remove(0); //remove the first card from botDeck
			BasicCommands.drawCard(out, gameState.getHumanHand().get(gameState.getHumanHand().size()-1), gameState.getHumanHand().size(),0); //show the card on the screen's humanHand part
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			int j = 1;
			for (Card tmpCard : gameState.getHumanHand()){
				BasicCommands.drawCard(out, tmpCard, j++,0); //show the card on the screen's humanHand part
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}



	}

}

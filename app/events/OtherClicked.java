package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case
 * somewhere that is not on a card tile or the end-turn button.
 * 
 * { 
 *   messageType = “otherClicked”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class OtherClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// cancel all highlights
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 5; j++){
				BasicCommands.drawTile(out, gameState.getBoard()[i][j], 0);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}

		if (gameState.getCurrentCard() != null){
			BasicCommands.drawCard(out, gameState.getCurrentCard(), gameState.getHumanHand().indexOf(gameState.getCurrentCard()) + 1, 0);
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
		}


		// clear all current tile, unit, card
		gameState.setCurrentUnit(null);
		gameState.setCurrentTile(null);
		gameState.setCurrentCard(null);


		
		
	}

}



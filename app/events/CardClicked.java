package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a card.
 * The event returns the position in the player's hand the card resides within.
 * 
 * { 
 *   messageType = “cardClicked”
 *   position = <hand index position [1-6]>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class CardClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		
		int handPosition = message.get("position").asInt();

		// cancel the highlight of tiles and set current unit and tile to null;
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 5; j++){
				BasicCommands.drawTile(out, gameState.getBoard()[i][j], 0);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		gameState.setCurrentUnit(null);
		gameState.setCurrentTile(null);

		// cancel the former highlighted card and tile range
		if (gameState.getCurrentCard() != null && gameState.getHumanHand().indexOf(gameState.getCurrentCard())+1 != handPosition){
			BasicCommands.drawCard(out, gameState.getCurrentCard(), gameState.getHumanHand().indexOf(gameState.getCurrentCard()) + 1, 0);
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 5; j++){
					BasicCommands.drawTile(out, gameState.getBoard()[i][j], 0);
					try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}

		gameState.setCurrentCard(gameState.getHumanHand().get(handPosition - 1));

		// highlight the selected card
		BasicCommands.drawCard(out, gameState.getCurrentCard(), handPosition, 1);
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

		// highlight valid tiles that the card can use
		gameState.getCurrentCard().showCardRange(out, gameState);
	}
}

package events;


import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * Indicates that the user has clicked an object on the game canvas, in this case a tile.
 * The event returns the x (horizontal) and y (vertical) indices of the tile that was
 * clicked. Tile indices start at 1.
 * 
 * { 
 *   messageType = “tileClicked”
 *   tilex = <x index of the tile>
 *   tiley = <y index of the tile>
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class TileClicked implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		gameState.setCurrentTile(gameState.getBoard()[tilex][tiley]); // set currentTile

		// select a humanUnit, cause highlight of move range and attack range (no current card selected)
		// use card
		if (gameState.getCurrentCard() != null){
			// if tile not in card use range, cancel highlight of card and cancel current card
			if (! gameState.getCurrentCard().tileInCardRange(gameState.getCurrentTile(), gameState)){
				BasicCommands.drawCard(out, gameState.getCurrentCard(), gameState.getHumanHand().indexOf(gameState.getCurrentCard()) + 1, 0);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
				gameState.setCurrentCard(null);
			}
			else{
				gameState.getCurrentCard().UseCard(out, gameState, gameState.getCurrentTile());
			}
		}
		if (gameState.getBoard()[tilex][tiley].getUnit() != null && gameState.getBoard()[tilex][tiley].getUnit().getOwner().equals("human")){
			// cancel highlight of former unit range
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 5; j++){
					BasicCommands.drawTile(out, gameState.getBoard()[i][j], 0);
					try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
			gameState.getBoard()[tilex][tiley].getUnit().showMoveRange(out, gameState); // highlight the valid move range
			gameState.getBoard()[tilex][tiley].getUnit().showAttackRange(out, gameState); // highlight the valid attack range
			gameState.setCurrentUnit(gameState.getBoard()[tilex][tiley].getUnit()); // set currentUnit
		}
		// select a target tile in highlighted move range, cause the unit to move to it
		else if (gameState.getCurrentUnit() != null  && gameState.getCurrentUnit().tileInMoveRange(gameState.getCurrentTile(), gameState)){
			gameState.getCurrentUnit().moveUnit(out, gameState, gameState.getCurrentTile());
		}
		// select a target tile in highlighted attack range, cause the unit to attack the unit on it
		else if (gameState.getCurrentUnit() != null && gameState.getCurrentUnit().tileInAttackRange(gameState.getCurrentTile(), gameState)) {
			gameState.getCurrentUnit().attackUnit(out, gameState, gameState.getCurrentTile());
		}


	}
}

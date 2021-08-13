package structures.basic;


import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;

/**
 * This is the base representation of a Card which is rendered in the player's hand.
 * A card has an id, a name (cardname) and a manacost. A card then has a large and mini
 * version. The mini version is what is rendered at the bottom of the screen. The big
 * version is what is rendered when the player clicks on a card in their hand.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Card {
	
	protected int id;
	
	protected String cardname;
	protected int manacost;
	
	protected MiniCard miniCard;
	protected BigCard bigCard;
	
	public Card() {};
	
	public Card(int id, String cardname, int manacost, MiniCard miniCard, BigCard bigCard) {
		super();
		this.id = id;
		this.cardname = cardname;
		this.manacost = manacost;
		this.miniCard = miniCard;
		this.bigCard = bigCard;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCardname() {
		return cardname;
	}
	public void setCardname(String cardname) {
		this.cardname = cardname;
	}
	public int getManacost() {
		return manacost;
	}
	public void setManacost(int manacost) {
		this.manacost = manacost;
	}
	public MiniCard getMiniCard() {
		return miniCard;
	}
	public void setMiniCard(MiniCard miniCard) {
		this.miniCard = miniCard;
	}
	public BigCard getBigCard() {
		return bigCard;
	}
	public void setBigCard(BigCard bigCard) {
		this.bigCard = bigCard;
	}

	// highlight the tiles where the card can be used. By default, adjacent tiles to friendly units
	public void showCardRange(ActorRef out, GameState gameState){
		if (gameState.getHumanPlayer().getMana() < this.manacost) return;
		for (Unit tmpUnit : gameState.getHumanUnits()){
			for (int i = 0; i < 9; i++){
				for (int j = 0; j < 5; j++){
					if (this.tileInCardRange(gameState.getBoard()[i][j], gameState)){
						BasicCommands.drawTile(out, gameState.getBoard()[i][j], 1);
						try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
		}
	}

	// check whether a tile is in the card's use range
	public boolean tileInCardRange(Tile tile, GameState gameState){
		if (gameState.getCurrentPlayer().equals("human")){
			if (gameState.getHumanPlayer().getMana() < this.manacost) return false;
			for (Unit tmpUnit : gameState.getHumanUnits()){
				if (Math.abs(tmpUnit.position.getTilex() - tile.getTilex()) <= 1 && Math.abs(tmpUnit.position.getTiley() - tile.getTiley()) <= 1 && tile.getUnit() == null){
					return true;
				}
			}
		}
		//for bot player
		else{
			if (gameState.getBotPlayer().getMana() < this.manacost) return false;
			for (Unit tmpUnit : gameState.getBotUnits()){
				if (Math.abs(tmpUnit.position.getTilex() - tile.getTilex()) <= 1 && Math.abs(tmpUnit.position.getTiley() - tile.getTiley()) <= 1 && tile.getUnit() == null){
					return true;
				}
			}
		}

		return false;
	}

	// Use the card, the effect is based on each specific card
	public void UseCard(ActorRef out, GameState gameState, Tile tile){
		if (gameState.getCurrentPlayer().equals("human")){
			BasicCommands.addPlayer1Notification(out, "Your card successfully used on tile " + "["+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 3);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
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
		}
		// for bot
		else{
			BasicCommands.addPlayer1Notification(out, "Enemy card successfully used on tile " + "["+(tile.getTilex()+1) + ", " + (tile.getTiley()+1) + " ].", 3);
			try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
			// minus player mana
			gameState.getBotPlayer().setMana(gameState.getBotPlayer().getMana() - this.manacost);
			BasicCommands.setPlayer2Mana(out, gameState.getBotPlayer());
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			//remove card
			gameState.setCurrentCard(null);
			BasicCommands.deleteCard(out, gameState.getBotHand().indexOf(this) + 1);
			try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getBotHand().remove(this);

		}
		return;
	}

	
}

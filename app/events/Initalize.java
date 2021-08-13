package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import structures.cards.*;
import structures.GameState;
import structures.basic.*;
import commands.BasicCommands;
import structures.units.unitPyromancer;
import structures.units.unitRockPulveriser;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.Collections;

/**
 * Indicates that both the core game loop in the browser is starting, meaning
 * that it is ready to recieve commands from the back-end.
 * 
 * { 
 *   messageType = “initalize”
 * }
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Initalize implements EventProcessor{

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {
		
		//CommandDemo.executeDemo(out); // this executes the command demo, comment out this when implementing your solution


		gameState.setTurnNum(1); // the first turnNum is 1
		Player humanPlayer = gameState.getHumanPlayer();
		Player botPlayer = gameState.getBotPlayer();
		Tile[][] board = gameState.getBoard();
		gameState.setCurrentPlayer("human");

		//initialize humanDeck
		int CardID = 0;
		String[] humanCards = {
				StaticConfFiles.c_azure_herald,
				StaticConfFiles.c_azurite_lion,
				StaticConfFiles.c_comodo_charger,
				StaticConfFiles.c_fire_spitter,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_ironcliff_guardian,
				StaticConfFiles.c_pureblade_enforcer,
				StaticConfFiles.c_silverguard_knight,
				StaticConfFiles.c_sundrop_elixir,
				StaticConfFiles.c_truestrike
		};
		for (int i = 0; i < 2; i++){
			for (String humanCard : humanCards){
				if (humanCard.equals(StaticConfFiles.c_pureblade_enforcer))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardPurebladeEnforcer.class));
				else if (humanCard.equals(StaticConfFiles.c_silverguard_knight))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardSilverguardKnight.class));
				else if (humanCard.equals(StaticConfFiles.c_ironcliff_guardian))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardIroncliffGuardian.class));
				else if (humanCard.equals(StaticConfFiles.c_azure_herald))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardAzureHerald.class));
				else if (humanCard.equals(StaticConfFiles.c_azurite_lion))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardAzuriteLion.class));
				else if (humanCard.equals(StaticConfFiles.c_comodo_charger))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardComodoCharger.class));
				else if (humanCard.equals(StaticConfFiles.c_truestrike))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardTruestrike.class));
				else if (humanCard.equals(StaticConfFiles.c_hailstone_golem))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardHailstoneGolemHuman.class));
				else if (humanCard.equals(StaticConfFiles.c_fire_spitter))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardFireSpitter.class));
				else if (humanCard.equals(StaticConfFiles.c_sundrop_elixir))gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, cardSundropElixir.class));
				else gameState.addHumanDeck(BasicObjectBuilders.loadCard(humanCard, CardID++, Card.class));
			}
		}

		Collections.shuffle(gameState.getHumanDeck()); // randomly shuffle humanDeck

		//initialize botDeck
		String[] botCards = {
				StaticConfFiles.c_blaze_hound,
				StaticConfFiles.c_bloodshard_golem,
				StaticConfFiles.c_entropic_decay,
				StaticConfFiles.c_hailstone_golem,
				StaticConfFiles.c_planar_scout,
				StaticConfFiles.c_pyromancer,
				StaticConfFiles.c_serpenti,
				StaticConfFiles.c_rock_pulveriser,
				StaticConfFiles.c_staff_of_ykir,
				StaticConfFiles.c_windshrike,
		};
		for (int i = 0; i < 2; i++){
			for (String botCard: botCards){
				if (botCard.equals(StaticConfFiles.c_rock_pulveriser))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardRockPulveriser.class));
				else if (botCard.equals(StaticConfFiles.c_serpenti))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardSerpenti.class));
				else if (botCard.equals(StaticConfFiles.c_planar_scout))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardPlanarScout.class));
				else if (botCard.equals(StaticConfFiles.c_windshrike))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardWindshrike.class));
				else if (botCard.equals(StaticConfFiles.c_bloodshard_golem))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardBloodshardGolem.class));
				else if (botCard.equals(StaticConfFiles.c_blaze_hound))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardBlazeHound.class));
				else if (botCard.equals(StaticConfFiles.c_staff_of_ykir))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardStaffOfYKir.class));
				else if (botCard.equals(StaticConfFiles.c_entropic_decay))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardEntropicDecay.class));
				else if (botCard.equals(StaticConfFiles.c_hailstone_golem))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardHailstoneGolemBot.class));
				else if (botCard.equals(StaticConfFiles.c_pyromancer))gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, cardPyromancer.class));
				else gameState.addBotDeck(BasicObjectBuilders.loadCard(botCard, CardID++, Card.class));
			}
		}

		Collections.shuffle(gameState.getBotDeck()); // randomly shuffle botDeck

		// initialize humanHand, 4 initial cards
		for (int i = 0; i < 4; i++){
			gameState.addHumanHand(gameState.getHumanDeck().get(0)); // add the first card to humanHand
			BasicCommands.drawCard(out, gameState.getHumanHand().get(i), i+1,0); //show the card on the screen's humanHand part
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
			gameState.getHumanDeck().remove(0); //remove the first card from humanDeck
		}

		// initialize botHand, 3 initial cards
		for (int i = 0; i < 3; i++){
			gameState.addBotHand(gameState.getBotDeck().get(0)); // add the first card to botHand
			gameState.getBotDeck().remove(0); //remove the first card from botDeck
		}

		// initialize humanPlayer health & mana
		humanPlayer.setHealth(20);
		humanPlayer.setMana(2);
		BasicCommands.setPlayer1Health(out, humanPlayer);
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setPlayer1Mana(out, humanPlayer);
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

		// initialize botPlayer health & mana
		botPlayer.setHealth(20);
		botPlayer.setMana(0);
		BasicCommands.setPlayer2Health(out, botPlayer);
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setPlayer2Mana(out, botPlayer);
		try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}

		// draw the board
		for (int i = 0; i < 9; i++){
			for(int j = 0; j < 5; j++){
				BasicCommands.drawTile(out, board[i][j], 0);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}

		// initialize two avatars
		EffectAnimation summon = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_summon);
		BasicCommands.playEffectAnimation(out, summon, board[1][2]);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.addHumanUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, gameState.getNewUnitID(), Unit.class));
		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setPositionByTile(board[1][2]);
		BasicCommands.drawUnit(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), board[1][2]);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttack(2);
		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setHealth(20);
		BasicCommands.setUnitAttack(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 2);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 20);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setTile(board[1][2]);
		board[1][2].setUnit(gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1));
		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setOwner("human");
		gameState.setNewUnitID(gameState.getNewUnitID()+1);

		BasicCommands.playEffectAnimation(out, summon, board[7][2]);
		try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.getNewUnitID(), Unit.class));
		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(board[7][2]);
		BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), board[7][2]);
		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(2);
		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(20);
		BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 2);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 20);
		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(board[7][2]);
		board[7][2].setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
		gameState.setNewUnitID(gameState.getNewUnitID()+1);

//		// for test
//		gameState.addHumanUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, gameState.getNewUnitID(), Unit.class));
//		gameState.getHumanUnits().get(1).setPositionByTile(board[2][1]);
//		BasicCommands.drawUnit(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), board[2][1]);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setAttack(4);
//		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setHealth(10);
//		BasicCommands.setUnitAttack(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 4);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		BasicCommands.setUnitHealth(out, gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1), 10);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setTile(board[2][1]);
//		board[2][1].setUnit(gameState.getHumanUnits().get(1));
//		gameState.getHumanUnits().get(gameState.getHumanUnits().size()-1).setOwner("human");
//		gameState.setNewUnitID(gameState.getNewUnitID()+1);
//
//		gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, gameState.getNewUnitID(), Unit.class));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(board[1][3]);
//		BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), board[1][3]);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(21);
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(20);
//		BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 21);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 20);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(board[1][3]);
//		board[1][3].setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
//		gameState.setNewUnitID(gameState.getNewUnitID()+1);
//
//		gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_rock_pulveriser, gameState.getNewUnitID(), unitRockPulveriser.class));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(board[1][4]);
//		BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), board[1][4]);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(1);
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(20);
//		BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 1);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 20);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(board[1][4]);
//		board[1][4].setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setName("Rock Pulveriser");
//		gameState.setNewUnitID(gameState.getNewUnitID()+1);
//
//		gameState.addBotUnits(BasicObjectBuilders.loadUnit(StaticConfFiles.u_pyromancer, gameState.getNewUnitID(), unitPyromancer.class));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setPositionByTile(board[4][4]);
//		BasicCommands.drawUnit(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), board[4][4]);
//		try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setAttack(2);
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setHealth(2);
//		BasicCommands.setUnitAttack(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 2);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		BasicCommands.setUnitHealth(out, gameState.getBotUnits().get(gameState.getBotUnits().size()-1), 2);
//		try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setTile(board[4][4]);
//		board[4][4].setUnit(gameState.getBotUnits().get(gameState.getBotUnits().size()-1));
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setOwner("bot");
//		gameState.getBotUnits().get(gameState.getBotUnits().size()-1).setName("Pyromance");
//		gameState.setNewUnitID(gameState.getNewUnitID()+1);

	}

}



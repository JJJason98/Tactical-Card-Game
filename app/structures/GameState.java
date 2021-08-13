package structures;
import structures.basic.*;
import utils.BasicObjectBuilders;

import java.util.ArrayList;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class GameState {

    private Player humanPlayer;
    private Player botPlayer;
    private ArrayList<Card> humanHand;
    private ArrayList<Card> botHand;
    private ArrayList<Card> humanDeck;
    private ArrayList<Card> botDeck;
    private ArrayList<Unit> humanUnits;
    private ArrayList<Unit> botUnits;
    private Tile [][] board;
    private String lastEvent; //The name of the last executed event except for HeartBeat.
    private int turnNum; //The current turn number of the game.
    private String currentPlayer; //String indicating the player who is currently playing.
    private Card currentCard;
    private Tile currentTile;
    private Unit currentUnit;
    private int newUnitID;

    public GameState(){
        this.humanPlayer = new Player();
        this.botPlayer = new Player();
        this.humanHand = new ArrayList<Card>();
        this.botHand = new ArrayList<Card>();
        this.humanDeck = new ArrayList<Card>();
        this.botDeck = new ArrayList<Card>();
        this.humanUnits = new ArrayList<Unit>();
        this.botUnits = new ArrayList<Unit>();
        this.board = new Tile[9][5];
        //initialize all tiles on the board
        for (int i = 0; i < 9; i++){
            for(int j = 0; j < 5; j++){
                board[i][j] = BasicObjectBuilders.loadTile(i, j);
            }
        }
        this.lastEvent = "";
        this.turnNum = 0;
        this.currentPlayer = "human";
        this.currentCard = null;
        this.currentTile = null;
        this.currentUnit = null;
        this.newUnitID = 0;
    }

    public Player getHumanPlayer() {
        return humanPlayer;
    }

    public Player getBotPlayer() {
        return botPlayer;
    }

    public ArrayList<Card> getHumanHand() {
        return humanHand;
    }

    public ArrayList<Card> getBotHand() {
        return botHand;
    }

    public ArrayList<Card> getHumanDeck() {
        return humanDeck;
    }

    public ArrayList<Card> getBotDeck() {
        return botDeck;
    }

    public ArrayList<Unit> getHumanUnits() {
        return humanUnits;
    }

    public ArrayList<Unit> getBotUnits() {
        return botUnits;
    }
    public Tile[][] getBoard() {
        return board;
    }

    public String getLastEvent() {
        return lastEvent;
    }

    public int getTurnNum() {
        return turnNum;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public Unit getCurrentUnit() {
        return currentUnit;
    }

    public int getNewUnitID() {
        return newUnitID;
    }

    public void setHumanPlayer(Player humanPlayer) {
        this.humanPlayer = humanPlayer;
    }

    public void setHumanPlayerHealth(int newHealth){
        this.humanPlayer.setHealth(newHealth);
    }

    public void setHumanPlayerMana(int newMana){
        this.humanPlayer.setMana(newMana);
    }

    public void setBotPlayer(Player botPlayer) {
        this.botPlayer = botPlayer;
    }

    public void setBotPlayerHealth(int newHealth){
        this.botPlayer.setHealth(newHealth);
    }

    public void setBotPlayerMana(int newMana){
        this.botPlayer.setMana(newMana);
    }

    public void setHumanHand(ArrayList<Card> humanHand) {
        this.humanHand = humanHand;
    }

    public void addHumanHand(Card newCard){
        this.humanHand.add(newCard);
    }

    public void deleteHumanHand(Card usedCard){
        this.humanHand.remove(usedCard);
    }

    public void setBotHand(ArrayList<Card> botHand) {
        this.botHand = botHand;
    }

    public void addBotHand(Card newCard){
        this.botHand.add(newCard);
    }

    public void deleteBotHand(Card usedCard){
        this.botHand.remove(usedCard);
    }

    public void setHumanDeck(ArrayList<Card> humanDeck) {
        this.humanDeck = humanDeck;
    }

    public void addHumanDeck(Card newCard) {
        this.humanDeck.add(newCard);
    }

    public void deleteHumanDeck(Card pickedCard){
        this.humanDeck.remove(pickedCard);
    }

    public void setBotDeck(ArrayList<Card> botDeck) {
        this.botDeck = botDeck;
    }

    public void addBotDeck(Card newCard){
        this.botDeck.add(newCard);
    }

    public void deleteBotDeck(Card pickedCard){
        this.botDeck.remove(pickedCard);
    }

    public void setHumanUnits(ArrayList<Unit> humanUnits) {
        this.humanUnits = humanUnits;
    }

    public void addHumanUnits(Unit newUnit){
        this.humanUnits.add(newUnit);
    }

    public void deleteHumanUnits(Unit deadUnit){
        this.humanUnits.remove(deadUnit);
    }

    public void setBotUnits(ArrayList<Unit> botUnits) {
        this.botUnits = botUnits;
    }

    public void addBotUnits(Unit newUnit){
        this.botUnits.add(newUnit);
    }

    public void deleteBotUnits(Unit deadUnit){
        this.botUnits.remove(deadUnit);
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

    public void setLastEvent(String lastEvent) {
        this.lastEvent = lastEvent;
    }

    public void setTurnNum(int turnNum) {
        this.turnNum = turnNum;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setCurrentCard(Card currentCard) {
        this.currentCard = currentCard;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public void setCurrentUnit(Unit currentUnit) {
        this.currentUnit = currentUnit;
    }

    public void setNewUnitID(int newUnitID) {
        this.newUnitID = newUnitID;
    }
}


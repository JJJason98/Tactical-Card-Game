package structures.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is a representation of a Unit on the game board.
 * A unit has a unique id (this is used by the front-end.
 * Each unit has a current UnitAnimationType, e.g. move,
 * or attack. The position is the physical position on the
 * board. UnitAnimationSet contains the underlying information
 * about the animation frames, while ImageCorrection has
 * information for centering the unit on the tile. 
 * 
 * @author Dr. Richard McCreadie
 *
 */
public class Unit {

	@JsonIgnore
	protected static ObjectMapper mapper = new ObjectMapper(); // Jackson Java Object Serializer, is used to read java objects from a file

	protected int id;
	@JsonIgnore
	protected int attack;
	@JsonIgnore
	protected int health;
	@JsonIgnore
	protected String owner;
	@JsonIgnore
	protected Tile tile; // The tile that the unit currently on.
	@JsonIgnore
	protected boolean moved = false;
	@JsonIgnore
	protected boolean attacked = false;
	@JsonIgnore
	protected  String name = "";


	protected UnitAnimationType animation;
	protected Position position;
	protected UnitAnimationSet animations;
	protected ImageCorrection correction;

	public Unit() {
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(0, 0, 0, 0);
		this.correction = correction;
		this.animations = animations;
		this.owner = "";
		this.tile = null;
		this.name = "";
	}

	public Unit(int id, UnitAnimationSet animations, ImageCorrection correction, Tile currentTile) {
		super();
		this.id = id;
		this.animation = UnitAnimationType.idle;

		position = new Position(currentTile.getXpos(), currentTile.getYpos(), currentTile.getTilex(), currentTile.getTiley());
		this.correction = correction;
		this.animations = animations;
		this.owner = "";
		this.tile = null;
		this.name = "";
	}

	public Unit(int id, UnitAnimationType animation, Position position, UnitAnimationSet animations,
				ImageCorrection correction) {
		super();
		this.id = id;
		this.animation = animation;
		this.position = position;
		this.animations = animations;
		this.correction = correction;
		this.owner = "";
		this.tile = null;
		this.name = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UnitAnimationType getAnimation() {
		return animation;
	}

	public void setAnimation(UnitAnimationType animation) {
		this.animation = animation;
	}

	public ImageCorrection getCorrection() {
		return correction;
	}

	public void setCorrection(ImageCorrection correction) {
		this.correction = correction;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public UnitAnimationSet getAnimations() {
		return animations;
	}

	public void setAnimations(UnitAnimationSet animations) {
		this.animations = animations;
	}

	/**
	 * This command sets the position of the Unit to a specified
	 * tile.
	 *
	 * @param tile
	 */
	@JsonIgnore
	public void setPositionByTile(Tile tile) {
		position = new Position(tile.getXpos(), tile.getYpos(), tile.getTilex(), tile.getTiley());
	}

	public int getAttack() {
		return attack;
	}

	public int getHealth() {
		return health;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public Tile getTile() {
		return tile;
	}

	public void setTile(Tile tile) {
		this.tile = tile;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean getMoved() {
		return moved;
	}

	public void setMoved(boolean moved) {
		this.moved = moved;
	}

	public boolean getAttacked() {
		return attacked;
	}

	public void setAttacked(boolean attacked) {
		this.attacked = attacked;
	}

	public String getName(){
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// use BFS to get all tiles that the unit can move to
	public ArrayList<Tile> validMoveRange(GameState gameState) {
		String currentPlayer = gameState.getCurrentPlayer();
		boolean[][] visited = new boolean[9][5];
		for (int i = 0; i <9; i++){
			for (int j = 0; j <5; j++){
				visited[i][j] = false;
			}
		}
		Queue<Tile> queue = new LinkedList<Tile>();
		ArrayList<Tile> validMoveTiles = new ArrayList<Tile>();
		queue.offer(this.getTile());
		visited[this.getTile().getTilex()][this.getTile().getTiley()] = true;
		while (!queue.isEmpty()) {
			Tile front = queue.poll();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					if (!visited[i][j]) {
						// four Vertical adjacent tiles of front Tile
						if (Math.abs(i - front.getTilex()) + Math.abs(j - front.getTiley()) == 1) {
							// within the 2 tiles range of current unit and the tile has no enemy unit
							if ((Math.abs(this.position.getTilex() - i) + Math.abs(this.position.getTiley() - j)) <= 2 && (gameState.getBoard()[i][j].getUnit() == null || gameState.getBoard()[i][j].getUnit().getOwner() == currentPlayer)) {
								queue.offer(gameState.getBoard()[i][j]);
								validMoveTiles.add(gameState.getBoard()[i][j]);
								visited[i][j] = true;
							}
						}
					}
				}
			}
		}
		// delete tiles that has friend unit
		for (int i = 0; i < validMoveTiles.size(); i ++) {
			if (validMoveTiles.get(i).getUnit() != null){
				validMoveTiles.remove(i);
				i--;
			}
		}
		return validMoveTiles;
	}

	// Highlight the move range of the unit
	public void showMoveRange(ActorRef out, GameState gameState) {
		if (moved || attacked) return;
//		for (int i = 0; i < 9; i++){
//			for (int j = 0; j < 5; j++){
//				if ((Math.abs(this.position.getTilex() - i)+Math.abs(this.position.getTiley() - j)) <= 2 && gameState.getBoard()[i][j].getUnit() == null){
//					BasicCommands.drawTile(out, gameState.getBoard()[i][j], 1);
//					try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
//				}
//
//			}
//		}
		for (Tile tmpTile : this.validMoveRange(gameState)) {
			BasicCommands.drawTile(out, tmpTile, 1);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// Cancel Highlight of tiles

	public void cancelTileHighlight(ActorRef out, GameState gameState) {
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 5; j++){
					BasicCommands.drawTile(out, gameState.getBoard()[i][j], 0);
				try {Thread.sleep(1);} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}

	// check whether a tile is in the unit's move range
	public boolean tileInMoveRange(Tile tile, GameState gameState) {
		if (moved || attacked) return false;
		if (this.validMoveRange(gameState).contains(tile)) {
			return true;
		}
		return false;
	}

	// move the unit to a valid tile
	public void moveUnit(ActorRef out, GameState gameState, Tile tile) {
		BasicCommands.moveUnitToTile(out, this, tile);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.cancelTileHighlight(out, gameState);
		this.tile.setUnit(null);
		this.tile = tile;
		tile.setUnit(this);
		this.setPositionByTile(tile);
		gameState.setCurrentTile(null);
		gameState.setCurrentUnit(null);
		this.moved = true;
	}

	// Highlight the valid attack target tiles
	public void showAttackRange(ActorRef out, GameState gameState) {
		if (attacked) return;
		// if not moved, can move and attack
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 5; j++) {
				if (tileInAttackRange(gameState.getBoard()[i][j], gameState)) {
					BasicCommands.drawTile(out, gameState.getBoard()[i][j], 2);
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// check whether a tile is in the unit's valid attack target
	public boolean tileInAttackRange(Tile tile, GameState gameState) {
		if (attacked) return false;
		// if adjacent to a provoke unit, return false
		else if (!moved) {
			for (Tile tmpTile : this.validMoveRange(gameState)) {
				boolean flag = false;
				if (Math.abs(tmpTile.getTilex() - tile.getTilex()) <= 1 && Math.abs(tmpTile.getTiley() - tile.getTiley()) <= 1 && tile.getUnit() != null && !tile.getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
					flag = true;
					// if the unit itself on the tile can provoke, return true.
					if (tile.getUnit().getName().equals("Silverguard Knight") || tile.getUnit().getName().equals("Ironcliff Guardian") || tile.getUnit().getName().equals("Rock Pulveriser")){
						return true;
					}
					// if enemy's provoke unit in attack range and can be attacked, res = false.
					for (int i = 0; i < 9; i++) {
						for (int j = 0; j < 5; j++) {
							if (Math.abs(tmpTile.getTilex() - i) <= 1 && Math.abs(tmpTile.getTiley() - j) <= 1 && gameState.getBoard()[i][j].getUnit() != null && !gameState.getBoard()[i][j].getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
								Unit maybeProvokeUnit = gameState.getBoard()[i][j].getUnit();
								if (maybeProvokeUnit.getName().equals("Silverguard Knight") || maybeProvokeUnit.getName().equals("Ironcliff Guardian") || maybeProvokeUnit.getName().equals("Rock Pulveriser")){
									flag = false;
								}
							}
						}
					}
				}
				if (flag) return true;
			}
			if (Math.abs(this.position.getTilex() - tile.getTilex()) <= 1 && Math.abs(this.position.getTiley() - tile.getTiley()) <= 1 && tile.getUnit() != null && !tile.getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
				// if the unit itself on the tile can provoke, return true.
				//System.out.println("moved, check attack " + tile.getTilex() + tile.getTiley() + tile.getUnit().getName());
				if (tile.getUnit().getName().equals("Silverguard Knight") || tile.getUnit().getName().equals("Ironcliff Guardian") || tile.getUnit().getName().equals("Rock Pulveriser")){
					System.out.println("provoke" + tile.getTilex() + tile.getTiley());
					return true;
				}
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 5; j++) {
						if (Math.abs(this.position.getTilex() - i) <= 1 && Math.abs(this.position.getTiley() - j) <= 1 && gameState.getBoard()[i][j].getUnit() != null && !gameState.getBoard()[i][j].getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
							Unit maybeProvokeUnit = gameState.getBoard()[i][j].getUnit();
							if (maybeProvokeUnit.getName().equals("Silverguard Knight") || maybeProvokeUnit.getName().equals("Ironcliff Guardian") || maybeProvokeUnit.getName().equals("Rock Pulveriser")){
								return false;
							}
						}
					}
				}
				return true;
			}
			return false;
		}
		// if moved, can attack adjacent tiles
		else {
			if (Math.abs(this.position.getTilex() - tile.getTilex()) <= 1 && Math.abs(this.position.getTiley() - tile.getTiley()) <= 1 && tile.getUnit() != null && !tile.getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
				// if the unit itself on the tile can provoke, return true.
				//System.out.println("moved, check attack " + tile.getTilex() + tile.getTiley() + tile.getUnit().getName());
				if (tile.getUnit().getName().equals("Silverguard Knight") || tile.getUnit().getName().equals("Ironcliff Guardian") || tile.getUnit().getName().equals("Rock Pulveriser")){
					System.out.println("provoke" + tile.getTilex() + tile.getTiley());
					return true;
				}
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 5; j++) {
						if (Math.abs(this.position.getTilex() - i) <= 1 && Math.abs(this.position.getTiley() - j) <= 1 && gameState.getBoard()[i][j].getUnit() != null && !gameState.getBoard()[i][j].getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
							Unit maybeProvokeUnit = gameState.getBoard()[i][j].getUnit();
							if (maybeProvokeUnit.getName().equals("Silverguard Knight") || maybeProvokeUnit.getName().equals("Ironcliff Guardian") || maybeProvokeUnit.getName().equals("Rock Pulveriser")){
								return false;
							}
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	// let the unit attack the valid unit
	public void attackUnit(ActorRef out, GameState gameState, Tile tile) {
		// if not adjacent, first move to an adjacent tile
		Tile landTile = null; // The tile that the unit first moves to before attack
		int minMoveStep = 100;
		boolean needMove = false;
		for (int i = 0; i < 9; i++){
			for (int j = 0; j < 5; j++){
				if (Math.abs(this.getTile().getTilex() - i) <= 1 && Math.abs(this.getTile().getTiley() - j) <= 1 && gameState.getBoard()[i][j].getUnit() != null && !gameState.getBoard()[i][j].getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
					Unit maybeProvokeUnit = gameState.getBoard()[i][j].getUnit();
					if (maybeProvokeUnit.getName().equals("Silverguard Knight") || maybeProvokeUnit.getName().equals("Ironcliff Guardian") || maybeProvokeUnit.getName().equals("Rock Pulveriser")){
						needMove = true;
						break;
					}
				}
			}
			if (needMove) break;
		}
		if (Math.abs(this.position.getTilex() - tile.getTilex()) > 1 || Math.abs(this.position.getTiley() - tile.getTiley()) > 1 || needMove) {
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 5; j++) {
					//if adjacent tile of the target has enemy provoke, can't move there.
					boolean flag = false;
					if (!(tile.getUnit().getName().equals("Silverguard Knight") || tile.getUnit().getName().equals("Ironcliff Guardian") || tile.getUnit().getName().equals("Rock Pulveriser"))){
						for (int m = 0; m < 9; m++) {
							for (int n = 0; n < 5; n++) {
								if (Math.abs(m - i) <= 1 && Math.abs(n - j) <= 1 && gameState.getBoard()[m][n].getUnit() != null && !gameState.getBoard()[m][n].getUnit().getOwner().equals(gameState.getCurrentPlayer())) {
									Unit maybeProvokeUnit = gameState.getBoard()[m][n].getUnit();
									if (maybeProvokeUnit.getName().equals("Silverguard Knight") || maybeProvokeUnit.getName().equals("Ironcliff Guardian") || maybeProvokeUnit.getName().equals("Rock Pulveriser")){
										flag = true;
										break;
									}
								}
							}
							if (flag) break;
						}
					}
					if (flag) continue;
					if (Math.abs(tile.getTilex() - i) <= 1 && Math.abs(tile.getTiley() - j) <= 1 && this.tileInMoveRange(gameState.getBoard()[i][j], gameState)) {
						int newMoveStep = Math.abs(this.position.getTilex() - i) + Math.abs(this.position.getTiley() - j);
						if (newMoveStep < minMoveStep) {
							landTile = gameState.getBoard()[i][j];
							minMoveStep = newMoveStep;
						}
					}
				}
			}
			this.moveUnit(out, gameState, landTile);
		}
		// attack logic
		tile.getUnit().setHealth(tile.getUnit().getHealth() - this.attack);
		// if the unit is  avatar, change player health
		if (tile.getUnit().id == 0) {
			gameState.setHumanPlayerHealth(tile.getUnit().health);
			BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// human avatar is hurt, trigger Silverguard Knight ability
			for (Unit u : gameState.getHumanUnits()){
				if (u.getName().equals("Silverguard Knight")){
					EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
					BasicCommands.playEffectAnimation(out, buff, u.getTile());
					try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
					u.setAttack(u.getAttack()+2); //Attack plus 2
					BasicCommands.setUnitAttack(out, u, u.getAttack());
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		if (tile.getUnit().id == 1) {
			gameState.setBotPlayerHealth(tile.getUnit().health);
			BasicCommands.setPlayer2Health(out, gameState.getBotPlayer());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		BasicCommands.setUnitHealth(out, tile.getUnit(), tile.getUnit().getHealth());
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.attack);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		EffectAnimation projectile = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_projectiles);
		//BasicCommands.playProjectileAnimation(out, projectile, 0, this.getTile(), tile); //long-range attack animation
		//try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
		// if target die, play die animation and remove target
		if (tile.getUnit().getHealth() <= 0) {
			tile.getUnit().dieUnit(out, gameState);
		}
		// if target not die, counter attack if the current unit is in target's attack range
		else{
			this.health -= tile.getUnit().getAttack();
			// if the unit is  avatar, change player health
			if (this.id == 0) {
				gameState.setHumanPlayerHealth(this.health);
				BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// human avatar is hurt, trigger Silverguard Knight ability
				for (Unit u : gameState.getHumanUnits()){
					if (u.getName().equals("Silverguard Knight")){
						EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
						BasicCommands.playEffectAnimation(out, buff, u.getTile());
						try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
						u.setAttack(u.getAttack()+2); //Attack plus 2
						BasicCommands.setUnitAttack(out, u, u.getAttack());
						try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
					}
				}
			}
			if (this.id == 1) {
				gameState.setBotPlayerHealth(this.health);
				BasicCommands.setPlayer2Health(out, gameState.getBotPlayer());
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			BasicCommands.setUnitHealth(out, this, this.health);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			BasicCommands.playUnitAnimation(out, tile.getUnit(), UnitAnimationType.attack);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//BasicCommands.playProjectileAnimation(out, projectile, 0, tile, this.getTile());// long-range attack animation
			//try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
			if (this.health <= 0) {
				this.dieUnit(out, gameState);
			}
		}

		// cancel attack highlight
		this.cancelTileHighlight(out, gameState);
		this.attacked = true;
	}

	public void dieUnit(ActorRef out, GameState gameState) {
		BasicCommands.playUnitAnimation(out, this, UnitAnimationType.death);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		BasicCommands.deleteUnit(out, this);
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// if human avatar dies
		if (this.id == 0) BasicCommands.addPlayer1Notification(out, "Game Over, you are dead.", 5);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// if bot avatar dies
		if (this.id == 1) BasicCommands.addPlayer1Notification(out, "Congratulations! You are the winner!", 5);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.getTile().setUnit(null);
		if (this.getOwner() == "human") gameState.getHumanUnits().remove(this);
		else gameState.getBotUnits().remove(this);
	}
}





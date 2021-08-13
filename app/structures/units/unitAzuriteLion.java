package structures.units;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.EffectAnimation;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.StaticConfFiles;


public class unitAzuriteLion extends Unit {

	int attackCount = 0;

	//override attackUint method to attack twice per turn
	public void attackUnit(ActorRef out, GameState gameState, Tile tile) {
		// if not adjacent, first move to an adjacent tile
		Tile landTile = null; // The tile that the unit first moves to before attack
		int minMoveStep = 100;
		if (Math.abs(this.position.getTilex() - tile.getTilex()) > 1 || Math.abs(this.position.getTiley() - tile.getTiley()) > 1) {
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
		if (tile.getUnit().getId() == 0) {
			gameState.setHumanPlayerHealth(tile.getUnit().getHealth());
			BasicCommands.setPlayer1Health(out, gameState.getHumanPlayer());
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			// human avatar is hurt, trigger Silverguard Knight ability
			for (Unit u : gameState.getHumanUnits()){
				if (u.getName().equals("Silverguard Knight")){
					u.setAttack(u.getAttack()+2); //Attack plus 2
					BasicCommands.setUnitAttack(out, u, u.getAttack());
					try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
				}
			}
		}
		if (tile.getUnit().getId() == 1) {
			gameState.setBotPlayerHealth(tile.getUnit().getHealth());
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
		if (attackCount == 0) {
			attackCount++;
			moved = true;
		}
		else this.attacked = true;
	}
}

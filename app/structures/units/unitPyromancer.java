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

public class unitPyromancer extends Unit {
    public boolean tileInAttackRange(Tile tile, GameState gameState) {
        if (attacked) return false;
        else {
            if (tile.getUnit() != null && !tile.getUnit().getOwner().equals("bot")){
                if (tile.getUnit().getName().equals("Silverguard Knight") || tile.getUnit().getName().equals("Ironcliff Guardian") || tile.getUnit().getName().equals("Rock Pulveriser")){
                    return true;
                }
                else {
                    for (int i = 0; i < 9; i++) {
                        for (int j = 0; j < 5; j++) {
                            if (gameState.getBoard()[i][j].getUnit() != null && !gameState.getBoard()[i][j].getUnit().getOwner().equals("bot")){
                                if (gameState.getBoard()[i][j].getUnit().getName().equals("Silverguard Knight") || gameState.getBoard()[i][j].getUnit().getName().equals("Ironcliff Guardian") || gameState.getBoard()[i][j].getUnit().getName().equals("Rock Pulveriser")){
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public void attackUnit(ActorRef out, GameState gameState, Tile tile) {
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
        BasicCommands.playProjectileAnimation(out, projectile, 0, this.getTile(), tile); //long-range attack animation
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
        // if target die, play die animation and remove target
        if (tile.getUnit().getHealth() <= 0) {
            tile.getUnit().dieUnit(out, gameState);
        }
        // if target not die, counter attack if the current unit is in target's attack range
        else{
            if (tile.getUnit().getName().equals("Fire Spitter") || (Math.abs(this.getTile().getTilex() - tile.getTilex()) <= 1 && Math.abs(this.getTile().getTiley() - tile.getTiley()) <= 1)){
                this.health -= tile.getUnit().getAttack();
                // if the unit is  avatar, change player health
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
                if (tile.getUnit().getName().equals("Fire Spitter")){
                    BasicCommands.playProjectileAnimation(out, projectile, 0, tile, this.getTile());// long-range attack animation
                    try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
                }
                if (this.health <= 0) {
                    this.dieUnit(out, gameState);
                }
            }
        }

        // cancel attack highlight
        this.cancelTileHighlight(out, gameState);
        this.attacked = true;
    }
}

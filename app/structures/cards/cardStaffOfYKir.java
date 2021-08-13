package structures.cards;

        import akka.actor.ActorRef;
        import commands.BasicCommands;
        import structures.GameState;
        import structures.basic.Card;
        import structures.basic.EffectAnimation;
        import structures.basic.Tile;
        import structures.basic.Unit;
        import utils.BasicObjectBuilders;
        import utils.StaticConfFiles;

public class cardStaffOfYKir extends Card {
    public boolean tileInCardRange(Tile tile, GameState gameState){
        //can only use on bot avatar
        if (gameState.getBotPlayer().getMana() < this.manacost) return false;
        if (tile.getUnit() != null && tile.getUnit().getId() == 1) return true;
        return false;
    }
    public void UseCard(ActorRef out, GameState gameState, Tile tile) {
        BasicCommands.addPlayer1Notification(out, "Enemy spells Staff of Y’Kir ", 3);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Bot uses spell card, trigger Pureblade Enforcer ability
        for (Unit u : gameState.getHumanUnits()){
            if (u.getName().equals("Pureblade Enforcer")){
                EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
                BasicCommands.playEffectAnimation(out, buff, u.getTile());
                try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
                u.setAttack(u.getAttack()+1); //Attack plus 1
                u.setHealth(u.getHealth()+1); //Health plus 1
                BasicCommands.setUnitAttack(out, u, u.getAttack());
                try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
                BasicCommands.setUnitHealth(out, u, u.getHealth());
                try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}
            }
        }
        // Bot uses spell card, add 2 attack to Bot Avatar
        EffectAnimation buff = BasicObjectBuilders.loadEffect(StaticConfFiles.f1_buff);
        BasicCommands.playEffectAnimation(out, buff, tile);
        try {Thread.sleep(1000);} catch (InterruptedException e) {e.printStackTrace();}
        tile.getUnit().setAttack(tile.getUnit().getAttack()+2);
        BasicCommands.setUnitAttack(out, tile.getUnit(), tile.getUnit().getAttack());
        try {Thread.sleep(10);} catch (InterruptedException e) {e.printStackTrace();}

        // minus player mana
        gameState.getBotPlayer().setMana(gameState.getBotPlayer().getMana() - this.manacost);
        BasicCommands.setPlayer2Mana(out, gameState.getBotPlayer());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //remove card
        gameState.setCurrentCard(null);
        gameState.getBotHand().remove(this);

        return;
    }

}

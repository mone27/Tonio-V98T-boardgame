package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

public class TestEventHandler implements EventHandler {

    private final DisplayManager DM;
    private DummySnapshot currentSnap;

    private boolean isP1 = true;        // current player

    public TestEventHandler(DisplayManager displayManager) {
        this.DM = displayManager;
    }

    @Override
    public void newGame(String firstHero, String secondHero) {

        this.currentSnap = new DummySnapshot(firstHero, secondHero);    // create snapshot
        String msg = "Welcome to the game!";

        this.DM.drawSnapshot(this.currentSnap, msg);                    //update UI
    }

    @Override
    public void exitGame() {
        // ??? implementing goodbye msg?
        this.DM.drawHomeScreen();
    }

    /*
        In-game mechanics
     */

    @Override
    public void skipTurn() {
        // invert turns
        this.isP1 = !this.isP1;
        // messages
        String player = "";
        if (this.isP1){player = "P1";}
        else {player = "P2";}
        String msg = "Turn skipped;\n" + player + " now playing";

        // UI
        try {DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
    }


    @Override
    public void requestInformation(int rowIndex, int columnIndex) {

    }

    @Override
    public void selectTile(int rowIndex, int columnIndex) {

    }


    @Override
    public void callReinforcement() {

    }

    
    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {

    }
}

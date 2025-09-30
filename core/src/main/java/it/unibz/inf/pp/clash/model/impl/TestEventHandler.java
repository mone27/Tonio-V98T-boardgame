package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit.UnitColor;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.ZeroVoid;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

import java.util.Optional;

public class TestEventHandler implements EventHandler {

    private final DisplayManager DM;
    private DummySnapshot currentSnap;

    private boolean isP1 = true;            // current player

    private Optional<Unit> selectedUnit = Optional.of(new ZeroVoid());    // current unit
    private int selectedRowIndex;
    private int selectedColumnIndex;

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
        this.isP1 = !this.isP1;             // invert turns

        // messages
        String player = "";
        if (this.isP1){player = "P1";}
        else {player = "P2";}
        String msg = "Turn skipped;\n" + player + " now playing";

        // UI
        try {this.DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
    }


    @Override
    public void requestInformation(int rowIndex, int columnIndex) {
        // retrieve unit and its info
        Optional<Unit> chosenUnit = this.currentSnap.getBoard().getUnit(rowIndex, columnIndex);
        String msg = this.unitInfo(chosenUnit);

        // UI (hover)
        try {this.DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
    }

    @Override
    public void selectTile(int rowIndex, int columnIndex) {
        // retrieve unit and its info
        Optional<Unit> chosenUnit = this.currentSnap.getBoard().getUnit(rowIndex, columnIndex);
        String msg = "Selected " + this.unitInfo(chosenUnit);

        // store unit and its coordinates (for reinforcements and movement)
        this.selectedUnit = chosenUnit;
        this.selectedRowIndex = rowIndex;
        this.selectedColumnIndex = columnIndex;

        // UI (left click)
        try {this.DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
    }


    @Override
    public void callReinforcement() {

    }

    
    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {
        Optional<Unit> chosenUnit = this.currentSnap.getBoard().getUnit(rowIndex, columnIndex);
        String msg = "";
        // if empty tile
        if (chosenUnit.isEmpty()){
            msg = "No unit to be removed";
            try {this.DM.updateMessage(msg);}
            catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
            return;
        }

        // else: unit is present on tile
        msg = "Removed " + this.unitInfo(chosenUnit);
        this.currentSnap.getBoard().removeUnit(rowIndex, columnIndex);
        this.DM.drawSnapshot(this.currentSnap, msg);        // UI
    }

    /*
        Helper methods
     */
    public String unitInfo(Optional<Unit> chosenUnit) {
        String msg = "Unit with: \n{";

        if (chosenUnit.isEmpty()) {
            msg = "Empty tile";
            return msg;
        }

        Unit extractedUnit = chosenUnit.get();
        int health = extractedUnit.getHealth();

        if (extractedUnit instanceof MobileUnit mobile){        // pattern var :)
            UnitColor color = mobile.getColor();
            int atkCountdown = mobile.getAttackCountdown();

            msg = msg + "Color: " + color + ",\n" +
                    "Attacks in: " + atkCountdown + " turns,\n";
        }
        msg = msg + "Health: " + health + "}";
        return msg;
    }

}

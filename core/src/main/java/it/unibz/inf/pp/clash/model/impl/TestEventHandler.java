package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.model.snapshot.impl.dummy.DummySnapshot;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit;
import it.unibz.inf.pp.clash.model.snapshot.units.MobileUnit.UnitColor;
import it.unibz.inf.pp.clash.model.snapshot.units.Unit;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.Fairy;
import it.unibz.inf.pp.clash.model.snapshot.units.impl.ZeroVoid;
import it.unibz.inf.pp.clash.view.DisplayManager;
import it.unibz.inf.pp.clash.view.exceptions.NoGameOnScreenException;

import java.util.Optional;

public class TestEventHandler implements EventHandler {

        private final DisplayManager DM;
        private DummySnapshot currentSnap;

        private boolean isP1 = true;            // current player
        private boolean isMoveSelected = false; // if move button was clicked

        private Optional<Unit> selectedUnit = Optional.of(new ZeroVoid());    // current unit
        private int selectedRowIndex;
        private int selectedColumnIndex;

        private Optional<Unit> movingUnit = Optional.of(new ZeroVoid());
        private int movingRowIndex;
        private int movingColumnIndex;

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
        // retrieve tile(unit) and its info
        Optional<Unit> chosenUnit = this.currentSnap.getBoard().getUnit(rowIndex, columnIndex);

        // TO HELPER METHOD: movement logic
        if (this.isMoveSelected &&
                this.movingUnit.isPresent() &&
                chosenUnit.isEmpty()){

            // move to new tile
            this.currentSnap.getBoard().addUnit(
                    rowIndex,
                    columnIndex,
                    this.movingUnit.get());

            // delete unit from old tile
            this.currentSnap.getBoard().removeUnit(this.movingRowIndex, this.movingColumnIndex);

            // switch movement off
            this.isMoveSelected = false;

            // store info
            this.selectedUnit = this.movingUnit;
            this.selectedRowIndex = rowIndex;
            this.selectedColumnIndex = columnIndex;

            // UI
            String msg = "Moved " + this.unitInfo(Optional.of(this.movingUnit.get())) +
                    "\nfrom cell (" + this.movingRowIndex + ", " + this.movingColumnIndex + ")" +
                    "\nto cell (" + this.selectedRowIndex + ", " + this.selectedColumnIndex + ")";
            this.DM.drawSnapshot(this.currentSnap, msg);

            return;
        }

        // all the following should be moved to a helper method
        // store unit and its coordinates (for reinforcements and movement)
        this.selectedUnit = chosenUnit;
        this.selectedRowIndex = rowIndex;
        this.selectedColumnIndex = columnIndex;

        // UI (left click)
        String msg = "";
        if (this.isMoveSelected && this.movingUnit.isPresent()) {
            msg = "Cannot move: " + this.unitInfo(chosenUnit) + "\non destination tile";
        }
        else if (this.isMoveSelected) {
            msg = "Cannot move: " + this.unitInfo(chosenUnit) + "\nwas deleted";
            this.isMoveSelected = false;
        }
        else {msg = "Selected " + this.unitInfo(chosenUnit);}
        try {this.DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
    }


    @Override
    public void callReinforcement() {
        String msg = "";
        // if unit already on cell: abort
        if (this.selectedUnit.isPresent()){
            msg = "Select an empty cell first";
            try {this.DM.updateMessage(msg);}
            catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
            return;
        }

        // else empty cell: add reinf. unit
        Fairy tmpUnit = new Fairy(UnitColor.ONE);
        this.currentSnap.getBoard().addUnit(this.selectedRowIndex,
                                            this.selectedColumnIndex,
                                            tmpUnit);
        this.selectedUnit = Optional.of(tmpUnit);   // update as last selected cell

        // UI
        msg = "Added " + this.unitInfo(Optional.of(tmpUnit)) +
                "\nat cell (" + this.selectedRowIndex + ", " +
                this.selectedColumnIndex + ")";
        this.DM.drawSnapshot(this.currentSnap, msg);
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
        // HERE: empty tile if it was also the last selected cell: this.selectedUnit = Optional.of(null);
        if (chosenUnit.equals(this.selectedUnit)){this.selectedUnit = Optional.empty();}
        if (chosenUnit.equals(this.movingUnit)){this.movingUnit = Optional.empty();}
        this.DM.drawSnapshot(this.currentSnap, msg);        // UI
    }

    @Override
    public void moveUnit() {
        // if movement button was already selected -> cancel action
        if (this.isMoveSelected){
            this.isMoveSelected = false;
            String msg = "Movement cancelled";
            try {this.DM.updateMessage(msg);}
            catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
            return;
        }

        // if no unit is selected -> exit
        if (this.selectedUnit.isEmpty() ||
                this.selectedUnit.get() instanceof ZeroVoid) {
            String msg = "No unit to be moved";
            try {this.DM.updateMessage(msg);}
            catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
            return;
        }

        // else, turn movement switch on
        this.isMoveSelected = true;
        this.movingUnit = this.selectedUnit;    // store unit for movement
        this.movingRowIndex = this.selectedRowIndex;
        this.movingColumnIndex = this.selectedColumnIndex;
        String msg = "Please select a destination tile";
        try {this.DM.updateMessage(msg);}
        catch (NoGameOnScreenException e) {throw new RuntimeException(e);}
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
        return msg + "Health: " + health + "}";
    }

}

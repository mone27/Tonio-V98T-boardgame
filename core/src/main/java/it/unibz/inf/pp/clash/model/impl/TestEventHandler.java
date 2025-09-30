package it.unibz.inf.pp.clash.model.impl;

import it.unibz.inf.pp.clash.model.EventHandler;
import it.unibz.inf.pp.clash.view.DisplayManager;

public class TestEventHandler implements EventHandler {

    private final DisplayManager displayManager;

    public TestEventHandler(DisplayManager displayManager) {
        this.displayManager = displayManager;
    }

    @Override
    public void newGame(String firstHero, String secondHero) {

    }

    @Override
    public void exitGame() {

    }

    @Override
    public void skipTurn() {

    }

    @Override
    public void callReinforcement() {

    }

    @Override
    public void requestInformation(int rowIndex, int columnIndex) {

    }

    @Override
    public void selectTile(int rowIndex, int columnIndex) {

    }

    @Override
    public void deleteUnit(int rowIndex, int columnIndex) {

    }
}

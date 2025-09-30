package it.unibz.inf.pp.clash.model.snapshot.units.impl;

import it.unibz.inf.pp.clash.model.snapshot.units.Unit;

/**
 * Dummy unit, for representing tiles when no tile has been yet selected by user
 */
public class ZeroVoid extends AbstractUnit implements Unit {
    public ZeroVoid(int health) {
        super(99);
    }
}

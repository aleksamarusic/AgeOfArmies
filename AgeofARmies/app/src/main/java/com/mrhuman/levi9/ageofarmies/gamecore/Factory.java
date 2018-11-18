package com.mrhuman.levi9.ageofarmies.gamecore;

public class Factory extends Building {

	static final int COST = 50;

    public static int getCOST() {
        return COST;
    }

    public static int getResourceGain() {
        return RESOURCE_GAIN;
    }

    public static int getMILISECONDS() {
        return MILISECONDS;
    }

    static final int INITIAL_HEALTH = 100;
	private static final int RESOURCE_GAIN = 10;
	private static final int MILISECONDS = 3000;
	
	private long lastGainTime;
	

	public Factory(GameModel gameModel, int x, int y, int player) {
		super(gameModel, x, y, player, INITIAL_HEALTH, 0);
		lastGainTime = System.currentTimeMillis();
	}


    @Override
    public int getBuldingType() {
        return 1;
    }

    @Override
    public int getInitialHealth() {
        return INITIAL_HEALTH;
    }

    @Override
	public int cost() {
		return COST;
	}

	@Override
	public void heal() {
		health += HEAL_STEP;
		if(health > INITIAL_HEALTH)
			health = INITIAL_HEALTH;
	}


	@Override
	public void step() {
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastGainTime >= MILISECONDS) {
			gameModel.resources[player] += RESOURCE_GAIN;
			lastGainTime = currentTime;
			gameModel.parent.gotResources(x, y, RESOURCE_GAIN);
		}
	}

}

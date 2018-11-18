package com.mrhuman.levi9.ageofarmies.gamecore;


public class Cannon extends Building {

	static final int COST = 50;
	static final int INITIAL_HEALTH = 100;
	private static final int MILISECONDS = 3000;
	
	private long lastShotTime;

	public static int getCOST() {
		return COST;
	}

	public static int getMILISECONDS() {
		return MILISECONDS;
	}

	public Cannon(GameModel gameModel, int x, int y, int player) {
		super(gameModel, x, y, player, INITIAL_HEALTH, 0);
		lastShotTime = System.currentTimeMillis();
	}


	@Override
	public int getBuldingType() {
		return 2;
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
		if(currentTime - lastShotTime >= MILISECONDS) {
			//gameModel.resources[player] += RESOURCE_GAIN;
			double minDistance = 999999999;
			Building target = null;
			for (Building building: gameModel.board.buildings) {
				double distance = Math.pow(x - building.getX(),2) + Math.pow(y - building.getY(),2);
				if (distance < minDistance && building.player != player) {
					minDistance = distance;
					target = building;
				}
			}
			if(target != null) {
				gameModel.bullets.add(new Bullet(x, y, target.getX(), target.getY()));
			}
			lastShotTime = currentTime;
		}
	}

}

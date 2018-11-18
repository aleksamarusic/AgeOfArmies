package com.mrhuman.levi9.ageofarmies.gamecore;

public abstract class Building {
	// number of buildings used for id
	private static int counter = 0;

	protected GameModel gameModel;
	// position
	protected int x;
	protected int y;
	protected double angle;

    public double getAngle() {
        return angle;
    }

    public int getPlayer() {
        return player;
    }

    public int getHealth() {
        return health;
    }

    public int getLevel() {
        return level;
    }

    public int getId() {
        return id;
    }

    protected int player;
	protected int health;
	protected int level;

	protected static final int HEAL_STEP = 10;
	static final int HEAL_COST = 10;

	protected int id;
	
	public Building(GameModel gameModel, int x, int y, int player, int health, int level) {
		this.gameModel = gameModel;
		this.x = x;
		this.y = y;
		this.player = player;
		this.health = health;
		this.level = level;
		id = ++counter;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public abstract int getBuldingType();

	public abstract int getInitialHealth();

	public static int healCost() {
		return HEAL_COST;
	}
	
	// cost required to build a building 
	public abstract int cost();
	
	public void hit(int damage) {
		health -= damage;
		if (health < 0) health = 0;
		gameModel.parent.lostHealth(x, y, damage);
	}
	
	public abstract void heal();
	
	public boolean isDestroyed() {
		return health <= 0;
	}
	
	public int upgradeCost() {
		// FIX
		return 0;
	}
	
	public abstract void step();
	
}

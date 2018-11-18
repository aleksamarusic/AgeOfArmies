package com.mrhuman.levi9.ageofarmies.gamecore;

public interface GameModelParent {
	
	public void gotResources(int x, int y, int amount);
	public void lostHealth(int x, int y, int amount);
	public void gameOver(int player);
}

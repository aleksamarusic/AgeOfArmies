package com.mrhuman.levi9.ageofarmies.gamecore;

public class Bullet {
    static int damage = 5;
    private static float GRAVITATION = 10;
	private int cannonX;
	private int cannonY;
	int targetX;
	int targetY;
	private long creationTime;

    public double getX() {
        return x;
    }

    public static int getDamage() {
        return damage;
    }

    public static float getGRAVITATION() {
        return GRAVITATION;
    }

    public int getCannonX() {
        return cannonX;
    }

    public int getCannonY() {
        return cannonY;
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }

    public double getHeight() {
        return height;
    }

    public double getInitialSpeed() {
        return initialSpeed;
    }

    public double getSpeedX() {
        return speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public double getDistance() {
        return distance;
    }

    public double getY() {
        return y;
    }

    private double x;
	private double y;
	private double height;
    private double initialSpeed;
    private double speedX;
    private double speedY;
    private double distance;
    private long lastIteration;


	public Bullet(int cannonX, int cannonY, int targetX, int targetY) {
	    this.cannonX = cannonX;
	    this.cannonY = cannonY;
	    this.targetX = targetX;
	    this.targetY = targetY;
	    lastIteration = creationTime = System.currentTimeMillis();
	    this.x = cannonX;
	    this.y = cannonY;
	    height = 0;
	    distance = Math.sqrt(Math.pow(cannonX - targetX, 2) + Math.pow(cannonY - targetY, 2));
	    initialSpeed = Math.sqrt(GRAVITATION * distance);
	    speedX = initialSpeed * Math.sqrt(2) / 2;
        speedY = initialSpeed * Math.sqrt(2) / 2;
    }



	public void step() {
	    double t = (System.currentTimeMillis()-creationTime)/1000.0;
        x = cannonX+t* speedX * (targetX - cannonX) / distance;
        y = cannonY+t*speedX * (targetY - cannonY) / distance;
        //height += speedY;
        //speedY =  initialSpeed * Math.sqrt(2) / 2 - t*GRAVITATION;

        /*float timeElapsed = (System.currentTimeMillis()-lastIteration)/1000;
        lastIteration = System.currentTimeMillis();
        x +=speedX * timeElapsed * (targetX - cannonX) / distance;
        y += speedX * timeElapsed * (targetY - cannonY) / distance;
        height += speedY * timeElapsed;
        speedY =  speedY - timeElapsed*GRAVITATION;*/

    }
	
	public boolean isDestroyed() {
	    return (Math.abs(targetX - x) < 0.5) && (Math.abs(targetY - y) < 0.5);
}

}

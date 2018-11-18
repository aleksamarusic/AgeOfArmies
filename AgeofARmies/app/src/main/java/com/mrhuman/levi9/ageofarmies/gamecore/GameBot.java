package com.mrhuman.levi9.ageofarmies.gamecore;

import java.util.Random;

public class GameBot extends Thread {

	private static final int SLEEP_MILIS = 500;

    public static int getSleepMilis() {
        return SLEEP_MILIS;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    private int difficulty = 0;
	GameModel gameModel;
	
	public GameBot(GameModel gameModel) {
		this.gameModel = gameModel;
	}

	public void makeMoveEasy(){
        // 0 build a factory, 1 build a cannon, 2 heal
        Random r = new Random();
        int decision = r.nextInt(3);

        if (decision == 0) {
            for (int x = 0; x < gameModel.board.dimX; x++) {
                for (int y = 0; y < gameModel.board.dimY; y++) {
                    if (gameModel.board.at(x, y) == null) {
                        gameModel.build(1, 1, x, y);
                        y = gameModel.board.dimY;
                        x = gameModel.board.dimX;
                    }
                }
            }
        }
        else if (decision == 1) {
            for (int x = 0; x < gameModel.board.dimX; x++) {
                for (int y = 0; y < gameModel.board.dimY; y++) {
                    if (gameModel.board.at(x, y) == null) {
                        gameModel.build(1, 2, x, y);
                        y = gameModel.board.dimY;
                        x = gameModel.board.dimX;
                    }
                }
            }
        }
        else {
            for (int x = 0; x < gameModel.board.dimX; x++) {
                for (int y = 0; y < gameModel.board.dimY; y++) {
                    Building b = gameModel.board.at(x, y);
                    if (b != null) {
                        boolean healed = gameModel.heal(1, x, y);
                        if(healed) {
                            y = gameModel.board.dimY;
                            x = gameModel.board.dimX;
                        }
                    }
                }
            }
        }
    }

    public void makeMoveMedium() {

    }

    public void makeMoveHard() {

    }

	public void run() {
		while (gameModel.isRunning()) {
            synchronized(this) {
                while (gameModel.isLocked())
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
            }
		    gameModel.lock();

            switch (difficulty) {
                case 0:
                    makeMoveEasy();
                    break;
                case 1:
                    makeMoveMedium();
                    break;
                case 2:
                    makeMoveHard();
                    break;
            }

			gameModel.unlock();
			try {
				sleep(SLEEP_MILIS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}


package com.mrhuman.levi9.ageofarmies;

import android.os.AsyncTask;
import android.widget.Button;
import android.widget.TextView;

import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.mrhuman.levi9.ageofarmies.gamecore.Building;
import com.mrhuman.levi9.ageofarmies.gamecore.GameModel;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


class BuildingNode extends Node {

    private Building building;
    private Node buildingVisual;
    private Node buildingHealth;
    private Node buildingOptions;
    private Node resView;
    private Node newBuilding;

    private GUIAdapter parent;
    private int x;
    private int y;

    public BuildingNode(Building building, Renderable buildingRenderable, float scale, GUIAdapter parent, Vector3 position, int x, int y) {
        super();
        this.building = building;
        this.parent = parent;
        this.x = x;
        this.y = y;

        setParent(parent);
        setLocalPosition(position);

        buildingVisual = new Node();
        buildingVisual.setParent(this);
        buildingVisual.setRenderable(buildingRenderable);
        buildingVisual.setLocalScale(new Vector3(scale, scale, scale));

        buildingHealth = new Node();
        buildingHealth.setParent(this);
        buildingHealth.setLocalPosition(new Vector3(0.0f , 0.15f, 0.0f));
        ViewRenderable.builder()
                .setView(parent.getGameActivity(), R.layout.view_health)
                .build()
                .thenAccept(renderable -> buildingHealth.setRenderable(renderable));
        buildingHealth.setEnabled(false);

        buildingOptions = new Node();
        buildingOptions.setParent(this);
        buildingOptions.setLocalPosition(new Vector3(0.0f , 0.25f, 0.0f));
        buildingOptions.setLocalScale(new Vector3(0.2f, 0.2f, 0.2f));
        ViewRenderable.builder()
                .setView(parent.getGameActivity(), R.layout.view_building_options)
                .build()
                .thenAccept(renderable -> {
                    buildingOptions.setRenderable(renderable);
                    Button repairBtn = renderable.getView().findViewById(R.id.button_repair);
                    repairBtn.setOnClickListener(v -> {
                    });
                });
        buildingOptions.setEnabled(false);

        newBuilding = new Node();
        newBuilding.setParent(this);
        newBuilding.setLocalPosition(new Vector3(0.0f , 0.25f, 0.0f));
        newBuilding.setLocalScale(new Vector3(0.7f, 0.7f, 0.7f));
        ViewRenderable.builder()
                .setView(parent.getGameActivity(), R.layout.view_new_building)
                .build()
                .thenAccept(renderable -> {
                    newBuilding.setRenderable(renderable);
                    Button farmBtn = renderable.getView().findViewById(R.id.button_farm);
                    farmBtn.setOnClickListener(v -> {
                        parent.getGameModel().lock();
                        parent.getGameModel().build(0, 1, x, y);
                        parent.getGameModel().unlock();
                    });
                    Button cannonBtn = renderable.getView().findViewById(R.id.button_cannon);
                    cannonBtn.setOnClickListener(v -> {
                        parent.getGameModel().build(0, 2, x, y);
                    });
                });
        newBuilding.setEnabled(false);

        resView = new Node();
        resView.setParent(this);
        resView.setLocalPosition(new Vector3(0.0f , 0.25f, 0.0f));
        resView.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
        ViewRenderable.builder()
                .setView(parent.getGameActivity(), R.layout.view_castle)
                .build()
                .thenAccept(renderable -> {
                    resView.setRenderable(renderable);
                    TextView res = renderable.getView().findViewById(R.id.res_tv);
                    res.setText(Integer.toString(parent.getGameModel().getResources()[0]));
                });
        resView.setEnabled(false);

        buildingVisual.setOnTapListener((hitTestResult, motionEvent) -> {
            parent.hidePopups(x, y);

            if (building != null) {
                buildingHealth.setEnabled(!buildingHealth.isEnabled());
                buildingOptions.setEnabled(!buildingOptions.isEnabled());
            } else {
                newBuilding.setEnabled(!newBuilding.isEnabled());
            }
        });
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;

        class UpdateGUI extends AsyncTask<Void, Void, Void> {
            protected Void doInBackground(Void... voids) {return null;}
            protected void onProgressUpdate(Void... progress) {}
            protected void onPostExecute(Void result) {
                buildingOptions.setEnabled(false);
                newBuilding.setEnabled(false);
                if (building != null) {
                    buildingHealth.setEnabled(true);
                    if (building.getBuldingType() == 0) {
                        resView.setEnabled(true);
                    }
                } else {
                    buildingHealth.setEnabled(false);
                }
                setRenderable();
            }
        }
        new UpdateGUI().execute();
        setAngle();
    }

    public void setAngle() {
        if (building != null) {
            buildingVisual.setLocalRotation(new Quaternion(Vector3.up(), 0.0f)); // building.getAngle()
        }
    }

    public void setRenderable() {
        if (building == null) {
            buildingVisual.setRenderable(parent.getGameActivity().tileRenderable);
        } else {
            switch (building.getBuldingType()) {
                case 0:
                    buildingVisual.setRenderable(parent.getGameActivity().castle1Renderable);
                    break;
                case 1:
                    buildingVisual.setRenderable(parent.getGameActivity().farm1Renderable);
                    break;
                case 2:
                    buildingVisual.setRenderable(parent.getGameActivity().cannon1Renderable);
                    break;
            }
        }

    }

    public void hidePopup() {
        if (building != null && building.getBuldingType() == 0) return;
        else {
            buildingOptions.setEnabled(false);
            newBuilding.setEnabled(false);
            if (building != null) { buildingHealth.setEnabled(true); }
        }
    }

}

public class GUIAdapter extends Node implements Runnable {

    private GameActivity gameActivity;

    private GameModel gameModel;
    private BuildingNode board[][];
    private ArrayList<Node> bullets;
    private int width;
    private int height;
    private int scale;
    private boolean running;

    public GUIAdapter(int width, int height, int scale, GameActivity gameActivity) {
        this.gameActivity = gameActivity;
        this.width = width;
        this.height = height;
        this.scale = scale;
        gameModel = new GameModel(gameActivity, width, height); // width, height

        board = new BuildingNode[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                board[i][j] = new BuildingNode(
                        null,
                        gameActivity.tileRenderable,
                        1.0f,
                        this,
                        getPositionFromXY(i, j), i, j);
            }
        }

        running = true;
        new Thread(this).start();
    }

    private Vector3 getPositionFromXY(int i, int j) {
        return new Vector3(
                (i - width / 2.0f) / 5.0f,
                (0.0f),
                (j - height / 2.0f) / 5.0f
        );
    }

    public synchronized void finish() {
        running = false;
        notifyAll();
    }

    @Override
    public void run() {
        while (running) {
            synchronized (this) {
                gameModel.lock();
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        Building fromModel = gameModel.getBoard().at(i, j);
                        if (board[i][j].getBuilding() != fromModel) {
                            board[i][j].setBuilding(fromModel);
                        }
                    }
                }
                gameModel.unlock();
            }
            try {
                sleep(1000/30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public GameActivity getGameActivity() {
        return gameActivity;
    }

    public GameModel getGameModel() {
        return gameModel;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BuildingNode[][] getboard() {
        return board;
    }

    public void hidePopups(int x, int y) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (i != x || j != y) {
                    board[i][j].hidePopup();
                }
            }
        }
    }

    public void gotResources() {
        class UpdateGUI extends AsyncTask<Void, Void, Void> {
            protected Void doInBackground(Void... voids) {return null;}
            protected void onProgressUpdate(Void... progress) {}
            protected void onPostExecute(Void result) {
                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        if (board[i][j].getBuilding() != null &&
                                board[i][j].getBuilding().getBuldingType() == 0 &&
                                board[i][j].getBuilding().getPlayer() == 0) {
                            board[i][j].setBuilding(board[i][j].getBuilding());
                        }
                    }
                }
            }
        }
        new UpdateGUI().execute();
    }
}

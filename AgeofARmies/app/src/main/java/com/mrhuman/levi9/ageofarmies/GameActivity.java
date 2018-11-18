package com.mrhuman.levi9.ageofarmies;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Session;
import com.google.ar.core.Trackable;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.mrhuman.levi9.ageofarmies.gamecore.GameModelParent;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity implements GameModelParent {

    public static final String WIDTH_ARG = "WIDTH_ARG";
    public static final String HEIGHT_ARG = "HEIGHT_ARG";
    public static final String SCALE_ARG = "SCALE_ARG";

    private int width;
    private int height;
    private int scale;

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler hideHandler = new Handler();

    private static final int RC_PERMISSIONS = 0x123;
    private boolean installRequested;

    private ArSceneView arSceneView;
    private GUIAdapter guiAdapter;

    // 3D renderable models
    public ModelRenderable bulletRenderable;
    public ModelRenderable cannon1Renderable;
    public ModelRenderable cannon2Renderable;
    public ModelRenderable castle1Renderable;
    public ModelRenderable castle2Renderable;
    public ModelRenderable farm1Renderable;
    public ModelRenderable farm2Renderable;
    public ModelRenderable tileRenderable;

    private boolean hasFinishedLoading = false;
    private boolean hasPlacedBoard = false;

    private GestureDetector gestureDetector;
    private Snackbar loadingMessageSnackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        arSceneView = findViewById(R.id.ar_view);

        Intent intent = getIntent();
        if (!intent.hasExtra(WIDTH_ARG) || !intent.hasExtra(HEIGHT_ARG) || !intent.hasExtra(SCALE_ARG)) {
            return;
        }
        width = intent.getIntExtra(WIDTH_ARG, 1); // get default from GameModel
        height = intent.getIntExtra(HEIGHT_ARG, 1);
        scale = intent.getIntExtra(SCALE_ARG, 1);

        CompletableFuture<ModelRenderable> bulletStage =
                ModelRenderable.builder().setSource(this, Uri.parse("bullet.sfb")).build();
        CompletableFuture<ModelRenderable> cannon1Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("cannon1.sfb")).build();
        CompletableFuture<ModelRenderable> cannon2Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("Cannon2.sfb")).build();
        CompletableFuture<ModelRenderable> castle1Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("castle1.sfb")).build();
        CompletableFuture<ModelRenderable> castle2Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("castle2.sfb")).build();
        CompletableFuture<ModelRenderable> farm1Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("farm1.sfb")).build();
        CompletableFuture<ModelRenderable> farm2Stage =
                ModelRenderable.builder().setSource(this, Uri.parse("farm2.sfb")).build();
        CompletableFuture<ModelRenderable> tileStage =
                ModelRenderable.builder().setSource(this, Uri.parse("tile.sfb")).build();

        CompletableFuture.allOf(
                bulletStage,
                cannon1Stage,
                cannon2Stage,
                castle1Stage,
                castle2Stage,
                farm1Stage,
                farm2Stage,
                tileStage
        ).handle(
                (notUsed, throwable) -> {
                    if (throwable != null) {
                        ARCoreUtils.displayError(this, "Unable to load renderable", throwable);
                        return null;
                    }

                    try {
                        bulletRenderable = bulletStage.get();
                        cannon1Renderable = cannon1Stage.get();
                        cannon2Renderable = cannon2Stage.get();
                        castle1Renderable = castle1Stage.get();
                        castle2Renderable = castle2Stage.get();
                        farm1Renderable = farm1Stage.get();
                        farm2Renderable = farm2Stage.get();
                        tileRenderable = tileStage.get();

                        // Everything finished loading successfully.
                        hasFinishedLoading = true;

                    } catch (InterruptedException | ExecutionException ex) {
                        ARCoreUtils.displayError(this, "Unable to load renderable", ex);
                    }

                    return null;
                }
        );

        gestureDetector =
                new GestureDetector(
                        this,
                        new GestureDetector.SimpleOnGestureListener() {
                            @Override
                            public boolean onSingleTapUp(MotionEvent e) {
                                if (!hasPlacedBoard) {
                                    Frame frame = arSceneView.getArFrame();
                                    if (tryPlaceBoard(e, frame)) {
                                        hasPlacedBoard = true;
                                    }
                                }
                                return true;
                            }

                            @Override
                            public boolean onDown(MotionEvent e) { return true; }
                        });

        arSceneView
                .getScene()
                .setOnTouchListener(
                        (HitTestResult hitTestResult, MotionEvent event) -> {
                            if (!hasFinishedLoading) { return false; }
                            return gestureDetector.onTouchEvent(event);

                        });

        arSceneView
                .getScene()
                .addOnUpdateListener(
                        frameTime -> {
                            if (loadingMessageSnackbar == null) { return; }

                            Frame frame = arSceneView.getArFrame();
                            if (frame == null) { return; }

                            if (frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
                                return;
                            }

                            for (Plane plane : frame.getUpdatedTrackables(Plane.class)) {
                                if (plane.getTrackingState() == TrackingState.TRACKING) {
                                    hideLoadingMessage();
                                }
                            }
                        });

        ARCoreUtils.requestCameraPermission(this, RC_PERMISSIONS);
    }

    private boolean tryPlaceBoard(MotionEvent tap, Frame frame) {
        if (tap != null && frame.getCamera().getTrackingState() == TrackingState.TRACKING) {
            for (HitResult hit : frame.hitTest(tap)) {
                Trackable trackable = hit.getTrackable();
                if (trackable instanceof Plane && ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
                    Anchor anchor = hit.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arSceneView.getScene());
                    guiAdapter = new GUIAdapter(width, height, scale, this);
                    anchorNode.addChild(guiAdapter);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (arSceneView == null) {
            return;
        }

        if (arSceneView.getSession() == null) {
            // If the session wasn't created yet, don't resume rendering.
            // This can happen if ARCore needs to be updated or permissions are not granted yet.
            try {
                Session session = ARCoreUtils.createArSession(this, installRequested);
                if (session == null) {
                    installRequested = ARCoreUtils.hasCameraPermission(this);
                    return;
                } else {
                    arSceneView.setupSession(session);
                }
            } catch (UnavailableException e) {
                ARCoreUtils.handleSessionException(this, e);
            }
        }

        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException ex) {
            ARCoreUtils.displayError(this, "Unable to get camera", ex);
            finish();
            return;
        }

        if (arSceneView.getSession() != null) {
            showLoadingMessage();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        hideHandler.postDelayed(() -> {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }

            hideHandler.postDelayed(() -> arSceneView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION), UI_ANIMATION_DELAY);
        }, 100);
    }

    private void showLoadingMessage() {
        if (loadingMessageSnackbar != null && loadingMessageSnackbar.isShownOrQueued()) {
            return;
        }

        loadingMessageSnackbar =
                Snackbar.make(
                        GameActivity.this.findViewById(android.R.id.content),
                        R.string.plane_finding,
                        Snackbar.LENGTH_INDEFINITE);
        loadingMessageSnackbar.getView().setBackgroundColor(0xbf323232);
        loadingMessageSnackbar.show();
    }

    private void hideLoadingMessage() {
        if (loadingMessageSnackbar == null) {
            return;
        }

        loadingMessageSnackbar.dismiss();
        loadingMessageSnackbar = null;
    }

    @Override
    public void gotResources(int x, int y, int amount) {
        guiAdapter.gotResources();
    }

    @Override
    public void lostHealth(int x, int y, int amount) {

    }

    @Override
    public void gameOver(int player) {
        guiAdapter.finish();
        finish();
    }

}

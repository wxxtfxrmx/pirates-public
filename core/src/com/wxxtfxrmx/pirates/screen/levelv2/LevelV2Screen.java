package com.wxxtfxrmx.pirates.screen.levelv2;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wxxtfxrmx.pirates.screen.levelv2.factory.TileTexturesFactory;
import com.wxxtfxrmx.pirates.screen.levelv2.factory.TileTypeFactory;
import com.wxxtfxrmx.pirates.screen.levelv2.system.DropDownSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.FillUpEmptyEntitySystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.MoveTilesToShipSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.RenderingSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.TouchTileSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.match.HorizontalMatchSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.match.VerticalMatchSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.swap.SwapHorizontalSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.system.swap.SwapVerticalSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.world.BoardWorld;

import java.util.Random;

public class LevelV2Screen extends ScreenAdapter {

    private final PooledEngine engine;
    private final AssetManager assets;
    private final BoardWorld boardWorld;
    private final OrthographicCamera camera = new OrthographicCamera(
            Constants.WIDTH * Constants.UNIT,
            Constants.HEIGHT * Constants.UNIT
    );

    public LevelV2Screen(SpriteBatch batch) {
        engine = new PooledEngine();
        assets = new AssetManager();
        camera.position.set(
                Constants.WIDTH * Constants.UNIT / 2f,
                Constants.HEIGHT * Constants.UNIT / 2f,
                0f
        );
        Random random = new Random(888L);
        TileTypeFactory typeFactory = new TileTypeFactory(random);
        TileTexturesFactory tileTexturesFactory = new TileTexturesFactory();
        boardWorld = new BoardWorld(engine, typeFactory, tileTexturesFactory);

        engine.addSystem(new VerticalMatchSystem());
        engine.addSystem(new HorizontalMatchSystem());
        engine.addSystem(new SwapVerticalSystem());
        engine.addSystem(new SwapHorizontalSystem());
        engine.addSystem(new MoveTilesToShipSystem());
        engine.addSystem(new DropDownSystem());
        engine.addSystem(new FillUpEmptyEntitySystem(tileTexturesFactory, typeFactory));
        engine.addSystem(new TouchTileSystem(camera));
        engine.addSystem(new RenderingSystem(camera, batch));
    }

    @Override
    public void show() {
        boardWorld.create();
    }

    @Override
    public void render(float delta) {
        float newDelta = normalize(delta);
        engine.update(newDelta);
    }

    private float normalize(float delta) {
        return Math.min(delta, 0.1f);
    }

    @Override
    public void resume() {
        updateSystems(true);
    }

    @Override
    public void pause() {
        updateSystems(false);
    }

    private void updateSystems(Boolean processing) {
        for (EntitySystem system : engine.getSystems()) {
            system.setProcessing(processing);
        }
    }
}
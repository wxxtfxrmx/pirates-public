package com.wxxtfxrmx.pirates.screen.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.wxxtfxrmx.pirates.component.TileSize;
import com.wxxtfxrmx.pirates.entity.ParallaxBackground;
import com.wxxtfxrmx.pirates.entity.factory.TextureFactory;
import com.wxxtfxrmx.pirates.entity.factory.TileFactory;
import com.wxxtfxrmx.pirates.navigation.Navigation;
import com.wxxtfxrmx.pirates.screen.BaseScreen;
import com.wxxtfxrmx.pirates.screen.level.board.Board;
import com.wxxtfxrmx.pirates.screen.level.hud.Hud;

import java.util.Random;

public final class LevelScreen extends BaseScreen {

    private final Navigation navigation;
    private final TileFactory tiles;
    private Board board;
    private final ParallaxBackground parallaxBackground;
    private final Hud hud;

    public LevelScreen(TileFactory tiles, TextureFactory images, Navigation navigation) {
        this.tiles = tiles;
        this.navigation = navigation;

        parallaxBackground = new ParallaxBackground(
                images.getTexture("clouds_front.png"),
                images.getTexture("clouds_back.png"),
                images.getTexture("water_sprite.png")
        );

        hud = new Hud();
    }

    @Override
    public void show() {
        super.show();

        final TileSize size = new TileSize(64, 64);

        Random random = new Random(888);
        board = new Board(size, tiles, random);
        board.setSize(scene.getViewport().getWorldWidth(), scene.getViewport().getWorldHeight() * 0.5f);
        parallaxBackground.setBounds(0, 0, scene.getViewport().getWorldWidth(), scene.getViewport().getWorldHeight());
        hud.setSize(scene.getViewport().getWorldWidth(), scene.getViewport().getWorldHeight() - 64);
        scene.addActor(parallaxBackground);

        scene.addActor(board);
        scene.addActor(hud);

        scene.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (board.onTouchDown(x, y)) {
                    return true;
                }

                return super.touchDown(event, x, y, pointer, button);
            }
        });

    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.18f, 0.74f, 1f, 1f);
        super.render(delta);
    }
}

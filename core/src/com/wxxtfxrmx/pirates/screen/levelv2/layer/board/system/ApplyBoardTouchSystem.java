package com.wxxtfxrmx.pirates.screen.levelv2.layer.board.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.wxxtfxrmx.pirates.screen.levelv2.Constants;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.AiComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.CurrentTurnComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.board.component.TouchChainComponent;

public class ApplyBoardTouchSystem extends EntitySystem {

    private static final Family chainFamily = Family.all(TouchChainComponent.class).get();
    private static final Family aiActiveFamily = Family.all(CurrentTurnComponent.class, AiComponent.class).get();

    private final ComponentMapper<TouchChainComponent> chainMapper = ComponentMapper.getFor(TouchChainComponent.class);

    private final Vector3 touch = new Vector3();
    private final OrthographicCamera camera;
    private final PooledEngine pooledEngine;

    public ApplyBoardTouchSystem(OrthographicCamera camera, PooledEngine pooledEngine) {
        this.camera = camera;
        this.pooledEngine = pooledEngine;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (getEngine().getEntitiesFor(aiActiveFamily).size() != 0) return;

        if (Gdx.input.justTouched()) {
            Entity entity = pooledEngine.createEntity();
            TouchChainComponent chainComponent = pooledEngine.createComponent(TouchChainComponent.class);
            entity.add(chainComponent);
            pooledEngine.addEntity(entity);
        }

        if (!Gdx.input.isTouched()) {
            ImmutableArray<Entity> entities = getEngine().getEntitiesFor(chainFamily);
            if (entities.size() != 0) {
                Entity entity = entities.first();
                TouchChainComponent component = chainMapper.get(entity);
                component.chain.clear();
                pooledEngine.removeEntity(entity);
            }

            return;
        }

        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(chainFamily);
        if (entities.size() == 0) return;
        Entity chainEntity = entities.first();

        touch.set(Gdx.input.getX(), Gdx.input.getY(), 0f);
        camera.unproject(touch);
        Vector2 touch2D = normalizedTouch(touch);

        if (!inBoardBounds(touch2D)) return;

        TouchChainComponent component = chainMapper.get(chainEntity);

        if (!alreadyInChain(touch2D, component.chain)) {
            component.chain.add(touch2D);
        } else {
            dropChainTo(touch2D, component.chain);
        }
    }

    private void dropChainTo(Vector2 touch, Array<Vector2> chain) {
        if (chain.size == 1) return;
        int touchIndex = chain.indexOf(touch, false);
        if (touchIndex == chain.size - 1) return;

        chain.removeRange(touchIndex, chain.size - 1);
    }

    private boolean inBoardBounds(Vector2 touch) {
        return touch.y <= Constants.UNIT * (Constants.MIDDLE_ROUNDED_HEIGHT + 1);
    }

    private boolean alreadyInChain(Vector2 touch, Array<Vector2> chain) {
        if (chain.isEmpty()) return false;

        for (Vector2 position : chain) {
            if (position.x == touch.x && position.y == touch.y) return true;
        }

        return false;
    }

    private Vector2 normalizedTouch(Vector3 touch) {
        int touchX = (int) (touch.x / Constants.UNIT) * Constants.UNIT + Constants.UNIT / 2;
        int touchY = (int) (touch.y / Constants.UNIT) * Constants.UNIT + Constants.UNIT / 2;

        return new Vector2(touchX, touchY);
    }
}

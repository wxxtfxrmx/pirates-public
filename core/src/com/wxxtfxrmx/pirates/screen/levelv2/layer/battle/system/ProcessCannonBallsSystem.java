package com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.wxxtfxrmx.pirates.screen.levelv2.Constants;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.AiComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.CannonBallComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.CannonballsComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.PlayerComponent;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.component.ShipComponent;

public class ProcessCannonBallsSystem extends IteratingSystem {

    private final ComponentMapper<CannonballsComponent> cannonballsMapper = ComponentMapper.getFor(CannonballsComponent.class);
    private final ComponentMapper<PlayerComponent> playerMapper = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<ShipComponent> shipMapper = ComponentMapper.getFor(ShipComponent.class);
    private final TextureRegion bombTexture;
    private final PooledEngine engine;

    private final Family aiFamily = Family.all(ShipComponent.class, AiComponent.class).get();
    private final Family playerFamily = Family.all(ShipComponent.class, PlayerComponent.class).get();

    public ProcessCannonBallsSystem(TextureRegion bombTexture, PooledEngine engine) {
        super(Family
                .all(CannonballsComponent.class, ShipComponent.class)
                .one(PlayerComponent.class, AiComponent.class)
                .get()
        );
        this.bombTexture = bombTexture;
        this.engine = engine;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ShipComponent shipComponent = shipMapper.get(entity);

        Entity opposite;
        if (playerMapper.has(entity)) {
            opposite = getEngine().getEntitiesFor(aiFamily).first();
        } else {
            opposite = getEngine().getEntitiesFor(playerFamily).first();
        }

        ShipComponent oppositeShipComponent = shipMapper.get(opposite);
        Vector2 startPosition = new Vector2();
        startPosition = shipComponent.reference.getBounds().getPosition(startPosition);

        Vector2 endPosition = new Vector2();
        endPosition = oppositeShipComponent.reference.getBounds().getPosition(endPosition);

        CannonballsComponent cannonballsComponent = cannonballsMapper.get(entity);

        for (int i = 0; i < cannonballsComponent.hit; i++) {
            Entity hitEntity = engine.createEntity();
            CannonBallComponent hitComponent = engine.createComponent(CannonBallComponent.class);
            hitComponent.currentPoint = startPosition;
            hitComponent.hitPoint = endPosition;
            hitComponent.texture = bombTexture;

            hitEntity.add(hitComponent);
            engine.addEntity(hitEntity);
        }

        for (int i = 0; i < cannonballsComponent.miss; i++) {
            Entity hitEntity = engine.createEntity();
            CannonBallComponent hitComponent = engine.createComponent(CannonBallComponent.class);
            hitComponent.currentPoint = startPosition;
            //TODO REFACTOR IT
            hitComponent.hitPoint = new Vector2(Constants.WIDTH * 0.5f * Constants.UNIT,
                    Constants.MIDDLE_ROUNDED_HEIGHT * Constants.UNIT);

            hitComponent.texture = bombTexture;

            hitEntity.add(hitComponent);
            engine.addEntity(hitEntity);
        }
    }
}

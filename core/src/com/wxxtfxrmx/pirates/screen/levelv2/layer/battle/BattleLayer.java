package com.wxxtfxrmx.pirates.screen.levelv2.layer.battle;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.wxxtfxrmx.pirates.screen.levelv2.Layer;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ApplyCoinsSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ApplyDamageSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ApplyEvasionSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ApplyRepairSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.CountDownTimeSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.MoveCannonBallSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ProcessCannonBallsSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.RenderCannonBallSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ShipIdleAnimationSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ShipRenderingSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.SwitchTurnSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ValidateAiHpSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.system.ValidatePlayerHpSystem;
import com.wxxtfxrmx.pirates.screen.levelv2.layer.battle.world.BattleWorld;

import java.util.Arrays;
import java.util.List;

public class BattleLayer implements Layer {

    private final BattleWorld world;
    private final List<? extends EntitySystem> inputSystems;
    private final List<? extends EntitySystem> logicSystems;
    private final List<? extends EntitySystem> renderingSystems;

    public BattleLayer(PooledEngine engine, SpriteBatch batch, OrthographicCamera camera) {
        world = new BattleWorld(engine);
        inputSystems = Arrays.asList();
        logicSystems = Arrays.asList(
                new ApplyRepairSystem(),
                new ApplyEvasionSystem(),
                new ApplyDamageSystem(engine),
                new ApplyCoinsSystem(),
                new ValidateAiHpSystem(),
                new ValidatePlayerHpSystem(engine),
                new CountDownTimeSystem(),
                new SwitchTurnSystem(engine),
                new ShipIdleAnimationSystem(),
                new ProcessCannonBallsSystem(world.getBombTexture(), engine),
                new MoveCannonBallSystem()
        );
        renderingSystems = Arrays.asList(
                new RenderCannonBallSystem(batch),
                new ShipRenderingSystem(batch, camera)
        );

        for (EntitySystem inputSystem : inputSystems) {
            engine.addSystem(inputSystem);
        }
        for (EntitySystem logicSystem : logicSystems) {
            engine.addSystem(logicSystem);
        }
        for (EntitySystem renderingSystem : renderingSystems) {
            engine.addSystem(renderingSystem);
        }
    }

    @Override
    public void create() {
        world.create();
    }

    @Override
    public void setEnabled(boolean enabled) {
        update(inputSystems, enabled);
        update(logicSystems, enabled);
        update(renderingSystems, enabled);
    }

    private void update(List<? extends EntitySystem> systems, boolean enabled) {
        for (EntitySystem system : systems) {
            system.setProcessing(enabled);
        }
    }
}

package crazypants.enderio.zoo.init;

import crazypants.enderio.zoo.entity.EntityDireWolf;
import crazypants.enderio.zoo.entity.EntityEnderminy;
import crazypants.enderio.zoo.entity.EntityFallenKnight;
import crazypants.enderio.zoo.entity.EntityFallenMount;
import crazypants.enderio.zoo.entity.EntityOwl;
import crazypants.enderio.zoo.entity.EntityWitherCat;
import crazypants.enderio.zoo.entity.EntityWitherWitch;
import crazypants.enderio.zoo.entity.render.RenderDirewolf;
import crazypants.enderio.zoo.entity.render.RenderEnderminy;
import crazypants.enderio.zoo.entity.render.RenderFallenKnight;
import crazypants.enderio.zoo.entity.render.RenderFallenMount;
import crazypants.enderio.zoo.entity.render.RenderOwl;
import crazypants.enderio.zoo.entity.render.RenderWitherCat;
import crazypants.enderio.zoo.entity.render.RenderWitherWitch;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {

  @Override
  public void preInit() {
    RenderingRegistry.registerEntityRenderingHandler(EntityEnderminy.class, RenderEnderminy.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityFallenKnight.class, RenderFallenKnight.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityFallenMount.class, RenderFallenMount.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityWitherWitch.class, RenderWitherWitch.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityWitherCat.class, RenderWitherCat.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityDireWolf.class, RenderDirewolf.FACTORY);
    RenderingRegistry.registerEntityRenderingHandler(EntityOwl.class, RenderOwl.FACTORY);
  }

}

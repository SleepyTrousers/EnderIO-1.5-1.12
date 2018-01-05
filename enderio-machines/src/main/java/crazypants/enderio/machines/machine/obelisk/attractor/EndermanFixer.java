package crazypants.enderio.machines.machine.obelisk.attractor;

import net.minecraft.entity.monster.EntityEnderman;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EndermanFixer {

  @SubscribeEvent
  public void onEndermanTeleport(EnderTeleportEvent event) {
    if (event.getEntityLiving() instanceof EntityEnderman && event.getEntityLiving().getEntityData().getBoolean("EIO:tracked")) {
      event.setCanceled(true);
    }
  }
}

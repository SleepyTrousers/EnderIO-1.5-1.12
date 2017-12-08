package crazypants.enderio.machines.machine.killera;

import java.util.Set;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.Sets;

import crazypants.enderio.machines.EnderIOMachines;
import net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOMachines.MOD_NAME)
public class ZombieCache {

  static final @Nonnull Set<UUID> cache = Sets.newHashSet();

  @SubscribeEvent
  public static void onSummonAid(SummonAidEvent event) {
    if (!cache.isEmpty() && cache.remove(event.getSummoner().getUniqueID())) {
      event.setResult(Result.DENY);
    }
  }

}
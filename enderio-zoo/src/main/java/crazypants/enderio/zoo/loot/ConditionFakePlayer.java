package crazypants.enderio.zoo.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.zoo.EnderIOZoo;
import crazypants.enderio.zoo.config.ZooConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIOZoo.MODID)
public class ConditionFakePlayer implements LootCondition {

  @SubscribeEvent
  public static void preInit(EnderIOLifecycleEvent.PreInit event) {
    LootConditionManager.registerCondition(new ConditionFakePlayer.Serializer());
  }

  public ConditionFakePlayer() {
  }

  @Override
  public boolean testCondition(@Nonnull Random rand, @Nonnull LootContext context) {
    if (context.getKillerPlayer() != null) {
      if (context.getKillerPlayer() instanceof FakePlayer) {
        final boolean b = rand.nextFloat() < ZooConfig.lootModifierFakePlayer.get();
        System.out.println(b);
        return b;
      }
      return true;
    }
    return false;
  }

  public static class Serializer extends LootCondition.Serializer<ConditionFakePlayer> {
    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "killed_by_player"), ConditionFakePlayer.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject json, @Nonnull ConditionFakePlayer value, @Nonnull JsonSerializationContext context) {
    }

    @Override
    public @Nonnull ConditionFakePlayer deserialize(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
      return new ConditionFakePlayer();
    }
  }
}
package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.energy.IEnergyStorage;

public class SetRandomEnergy extends LootFunction {

  public SetRandomEnergy(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  public @Nonnull ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
    if (Prep.isValid(stack)) {
      IEnergyStorage capability = PowerHandlerUtil.getCapability(stack);
      if (capability != null) {
        int maxEnergyStored = capability.getMaxEnergyStored();
        int realEnergy = (int) (maxEnergyStored * .1 + maxEnergyStored * .5 * rand.nextFloat());
        while (realEnergy > 0) {
          realEnergy -= Math.max(1, capability.receiveEnergy(realEnergy, false));
        }
      }
    }
    return stack;
  }

  public static class Serializer extends LootFunction.Serializer<SetRandomEnergy> {

    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "set_random_energy"), SetRandomEnergy.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject object, @Nonnull SetRandomEnergy functionClazz, @Nonnull JsonSerializationContext serializationContext) {
    }

    @Override
    public @Nonnull SetRandomEnergy deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
        @Nonnull LootCondition[] conditionsIn) {
      return new SetRandomEnergy(conditionsIn);
    }

  }
}

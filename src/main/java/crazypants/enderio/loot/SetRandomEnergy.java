package crazypants.enderio.loot;

import java.util.Random;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.EnderIO;
import crazypants.enderio.power.IInternalPoweredItem;
import crazypants.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class SetRandomEnergy extends LootFunction {

  public SetRandomEnergy(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
    if (Prep.isValid(stack) && stack.getItem() instanceof IInternalPoweredItem) {
      IInternalPoweredItem item = (IInternalPoweredItem) stack.getItem();
      int maxEnergyStored = item.getMaxEnergyStored(stack);
      int realEnergy = (int) (maxEnergyStored * .1 + maxEnergyStored * .5 * rand.nextFloat());
      item.setEnergyStored(stack, realEnergy);
    } else if (Prep.isValid(stack) && stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
      IEnergyStorage capability = stack.getCapability(CapabilityEnergy.ENERGY, null);
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
    public void serialize(JsonObject object, SetRandomEnergy functionClazz, JsonSerializationContext serializationContext) {
    }

    @Override
    public SetRandomEnergy deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
      return new SetRandomEnergy(conditionsIn);
    }

  }
}

package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class SetRandomUpgrade extends LootFunction {

  public SetRandomUpgrade(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  public @Nonnull ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
    NNList<IDarkSteelUpgrade> upgrades = UpgradeRegistry.getUpgrades();
    return UpgradeRegistry.getUpgradeItem(upgrades.get(rand.nextInt(upgrades.size())), rand.nextBoolean());
  }

  public static class Serializer extends LootFunction.Serializer<SetRandomUpgrade> {

    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "random_dsu"), SetRandomUpgrade.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject object, @Nonnull SetRandomUpgrade functionClazz, @Nonnull JsonSerializationContext serializationContext) {
    }

    @Override
    public @Nonnull SetRandomUpgrade deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
        @Nonnull LootCondition[] conditionsIn) {
      return new SetRandomUpgrade(conditionsIn);
    }

  }
}

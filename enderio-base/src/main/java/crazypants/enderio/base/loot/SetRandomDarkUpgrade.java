package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager;
import crazypants.enderio.base.handler.darksteel.DarkSteelRecipeManager.UpgradePath;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class SetRandomDarkUpgrade extends LootFunction {

  public SetRandomDarkUpgrade(LootCondition[] conditionsIn) {
    super(conditionsIn);
  }

  @Override
  public @Nonnull ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
    if (Prep.isValid(stack)) {
      NNList<UpgradePath> list = DarkSteelRecipeManager.getAllRecipes(new NNList<>(stack));
      if (!list.isEmpty()) {
        return list.get((int) (rand.nextInt(list.size()) * rand.nextFloat())).getOutput();
      }
    }
    return stack;
  }

  public static class Serializer extends LootFunction.Serializer<SetRandomDarkUpgrade> {

    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "set_random_dark_upgrade"), SetRandomDarkUpgrade.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject object, @Nonnull SetRandomDarkUpgrade functionClazz, @Nonnull JsonSerializationContext serializationContext) {
    }

    @Override
    public @Nonnull SetRandomDarkUpgrade deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
        @Nonnull LootCondition[] conditionsIn) {
      return new SetRandomDarkUpgrade(conditionsIn);
    }

  }
}

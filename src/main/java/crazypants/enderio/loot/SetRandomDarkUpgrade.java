package crazypants.enderio.loot;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.tuple.Triple;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.util.Prep;
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
  public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
    if (Prep.isValid(stack)) {
      List<Triple<ItemStack, ItemStack, ItemStack>> list = DarkSteelRecipeManager.getAllRecipes(Collections.singletonList(stack));
      if (!list.isEmpty()) {
        return list.get((int) (rand.nextInt(list.size()) * rand.nextFloat())).getRight();
      }
    }
    return stack;
  }

  public static class Serializer extends LootFunction.Serializer<SetRandomDarkUpgrade> {

    protected Serializer() {
      super(new ResourceLocation("set_random_dark_upgrade"), SetRandomDarkUpgrade.class);
    }

    @Override
    public void serialize(JsonObject object, SetRandomDarkUpgrade functionClazz, JsonSerializationContext serializationContext) {
    }

    @Override
    public SetRandomDarkUpgrade deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
      return new SetRandomDarkUpgrade(conditionsIn);
    }

  }
}

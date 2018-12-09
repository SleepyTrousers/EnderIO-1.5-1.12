package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.stackable.Things;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.base.EnderIO;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class UseThings extends LootFunction {

  private final String name;

  public UseThings(LootCondition[] conditionsIn, String name) {
    super(conditionsIn);
    this.name = name;
  }

  @Override
  public @Nonnull ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
    return new Things(name).getItemStack().copy();
  }

  public static class Serializer extends LootFunction.Serializer<UseThings> {

    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "use_things"), UseThings.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject object, @Nonnull UseThings functionClazz, @Nonnull JsonSerializationContext serializationContext) {
      object.addProperty("name", functionClazz.name);
    }

    @Override
    public @Nonnull UseThings deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
        @Nonnull LootCondition[] conditionsIn) {
      return new UseThings(conditionsIn, JsonUtils.getString(object, "name"));
    }

  }
}

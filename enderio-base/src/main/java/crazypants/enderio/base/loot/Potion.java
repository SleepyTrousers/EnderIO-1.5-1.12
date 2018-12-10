package crazypants.enderio.base.loot;

import java.util.Random;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.potion.PotionUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

public class Potion extends LootFunction {

  private final @Nonnull ResourceLocation name;
  private final boolean splash;

  public Potion(LootCondition[] conditionsIn, @Nonnull String name, boolean splash) {
    super(conditionsIn);
    this.name = new ResourceLocation(name);
    this.splash = splash;
    if (!PotionType.REGISTRY.containsKey(this.name)) {
      throw new RuntimeException(name + " is not a valid potion type");
    }
  }

  @Override
  public @Nonnull ItemStack apply(@Nonnull ItemStack stack, @Nonnull Random rand, @Nonnull LootContext context) {
    PotionType type = PotionType.REGISTRY.getObject(this.name);
    ItemStack potion = PotionUtil.getEmptyPotion(splash);
    PotionUtils.addPotionToItemStack(potion, type);
    return potion;
  }

  public static class Serializer extends LootFunction.Serializer<Potion> {

    protected Serializer() {
      super(new ResourceLocation(EnderIO.DOMAIN, "potion"), Potion.class);
    }

    @Override
    public void serialize(@Nonnull JsonObject object, @Nonnull Potion functionClazz, @Nonnull JsonSerializationContext serializationContext) {
      object.addProperty("name", functionClazz.name.toString());
      object.addProperty("splash", functionClazz.splash);
    }

    @Override
    public @Nonnull Potion deserialize(@Nonnull JsonObject object, @Nonnull JsonDeserializationContext deserializationContext,
        @Nonnull LootCondition[] conditionsIn) {
      return new Potion(conditionsIn, JsonUtils.getString(object, "name"), JsonUtils.getBoolean(object, "splash"));
    }

  }
}

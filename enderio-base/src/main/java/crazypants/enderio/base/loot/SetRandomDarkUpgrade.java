package crazypants.enderio.base.loot;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.handler.darksteel.Rules;
import crazypants.enderio.base.handler.darksteel.UpgradeRegistry;
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
    if (Prep.isValid(stack) && stack.getItem() instanceof IDarkSteelItem) {
      final Predicate<IRule> checker = Rules.makeChecker(stack, (IDarkSteelItem) stack.getItem());
      List<IDarkSteelUpgrade> list = UpgradeRegistry.getUpgrades().stream()
          .filter(upgrade -> upgrade.getRules().stream().filter(Rules::isStatic).allMatch(checker)).collect(Collectors.toList());
      if (!list.isEmpty()) {
        int maxCount = rand.nextInt(4) + 2;
        int count = 0;
        int tries = list.size() * maxCount;
        stack = stack.copy();
        int nextInt = rand.nextInt(list.size());
        while (count < maxCount && tries-- > 0) {
          IDarkSteelUpgrade upgrade = list.get(nextInt);
          if (upgrade.canAddToItem(stack, (IDarkSteelItem) stack.getItem())) {
            upgrade.addToItem(stack, (IDarkSteelItem) stack.getItem());
            count++;
            nextInt = rand.nextInt(list.size());
          } else if (nextInt == 0) {
            nextInt = list.size() - 1;
          } else {
            nextInt--;
          }
        }
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

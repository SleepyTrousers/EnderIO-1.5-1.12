package crazypants.enderio.zoo.potion;

import com.google.common.base.Predicate;

import crazypants.enderio.zoo.EnderZoo;
import crazypants.enderio.zoo.config.Config;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.registries.IForgeRegistry;

public class Potions {

  private static final String WITHERING = "withering";
  private static final String WITHERING_LONG = "witheringLong";

  private static final String CONFUSION = "confusion";
  private static final String CONFUSION_LONG = "confusionLong";

  private static final String FLOATING = "floating";
  private static final String FLOATING_TWO = "floatingTwo";
  private static final String FLOATING_LONG = "floatingLong";

  private PotionType withering;
  private PotionType witheringLong;

  private PotionType confusion;
  private PotionType confusionLong;

  private PotionType floating;
  private PotionType floatingLong;
  private PotionType floatingTwo;

  private FloatingPotion floatingPotion;

  public Potions() {
    if (Config.floatingPotionEnabled) {
      floatingPotion = FloatingPotion.create();
      floating = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDuration)).setRegistryName(EnderZoo.MODID, FLOATING);
      floatingLong = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionDurationLong)).setRegistryName(EnderZoo.MODID,
          FLOATING_TWO);
      floatingTwo = new PotionType(FLOATING, new PotionEffect(floatingPotion, Config.floatingPotionTwoDuration, 1)).setRegistryName(EnderZoo.MODID,
          FLOATING_LONG);
    }

  }

  public void registerPotions(IForgeRegistry<PotionType> reg) {
    // wither potion

    Ingredient redstone = Ingredient.fromItem(Items.REDSTONE);
    Ingredient glowstone = Ingredient.fromItem(Items.GLOWSTONE_DUST);

    // Rising
    if (Config.floatingPotionEnabled) {
      reg.register(floating);
      reg.register(floatingLong);
      reg.register(floatingTwo);

      Ingredient owlEgg = Ingredient.fromItem(EnderZoo.itemOwlEgg);
      registerPotionTypeConversion(PotionTypes.AWKWARD, owlEgg, floating);
      registerPotionTypeConversion(floating, redstone, floatingLong);
      registerPotionTypeConversion(floating, glowstone, floatingTwo);
    }

  }

  /**
   * Registers a conversion from one PotionType to another PotionType, with the given reagent
   */
  private void registerPotionTypeConversion(PotionType input, Ingredient ingredient, PotionType output) {
    PotionHelper.addMix(input, ingredient, output);
  }

  public PotionType getWithering() {
    return withering;
  }

  public PotionType getWitheringLong() {
    return witheringLong;
  }

  public PotionType getConfusion() {
    return confusion;
  }

  public PotionType getConfusionLong() {
    return confusionLong;
  }

  public PotionType getFloating() {
    return floating;
  }

  public PotionType getFloatingLong() {
    return floatingLong;
  }

  public PotionType getFloatingTwo() {
    return floatingTwo;
  }

  public FloatingPotion getFloatingPotion() {
    return floatingPotion;
  }

  static class ItemPredicateInstance implements Predicate<ItemStack> {
    private final Item item;
    private final int meta;

    public ItemPredicateInstance(Item itemIn) {
      this(itemIn, -1);
    }

    public ItemPredicateInstance(Item itemIn, int metaIn) {
      this.item = itemIn;
      this.meta = metaIn;
    }

    public boolean apply(ItemStack p_apply_1_) {
      return p_apply_1_ != null && p_apply_1_.getItem() == this.item && (this.meta == -1 || this.meta == p_apply_1_.getMetadata());
    }
  }

}

package crazypants.enderio.base.handler.darksteel;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade.IRule;
import crazypants.enderio.base.lang.ILang;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public final class Rules {

  public static @Nonnull IRule callbacksFor(final @Nonnull IDarkSteelUpgrade upgrade) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return item.hasUpgradeCallbacks(upgrade) ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule forSlot(final @Nonnull EntityEquipmentSlot slot) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return item.isForSlot(slot) ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  /**
   * Generates a rule that always success but will list the given language element in the "can be applied to" list of the upgrade's tooltips. You need to add
   * additional rules to actually implement the check(s).
   * <p>
   * This is a separate rule because the tooltip list is considered an "or" list, but rules are checked "and".
   * 
   * @param lang
   *          And {@link ILang} to add to the tooltip
   * @return the generated rule
   */
  public static @Nonnull IRule itemTypeTooltip(final @Nonnull ILang lang) {
    return new IRule.ItemType.Static() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return CheckResult.PASS;
      }

      @Override
      @Nonnull
      public ITextComponent getTooltip() {
        return lang.toChatServer();
      }
    };
  }

  public static @Nonnull IRule.StaticRule or(final @Nonnull IRule.StaticRule rule1, final @Nonnull IRule.StaticRule rule2) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return rule1.check(stack, item).passes() || rule2.check(stack, item).passes() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule or(final @Nonnull IRule rule1, final @Nonnull IRule rule2) {
    return new IRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return rule1.check(stack, item).passes() || rule2.check(stack, item).passes() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule WEAPONS = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isWeapon() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static @Nonnull IRule TOOL_BLOCK_BREAKING = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isBlockBreakingTool() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static @Nonnull IRule PICKAXE = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isPickaxe() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static @Nonnull IRule AXE = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isAxe() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

}

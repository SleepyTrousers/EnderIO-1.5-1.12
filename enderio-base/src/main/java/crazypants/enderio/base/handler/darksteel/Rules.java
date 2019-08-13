package crazypants.enderio.base.handler.darksteel;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import crazypants.enderio.api.upgrades.IDarkSteelItem;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade.IRule;
import crazypants.enderio.api.upgrades.IDarkSteelUpgrade.IRule.Prerequisite;
import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public final class Rules {

  public static @Nonnull IRule.StaticRule callbacksFor(final @Nonnull IDarkSteelUpgrade upgrade) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return item.hasUpgradeCallbacks(upgrade) ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule.StaticRule forSlot(final @Nonnull EntityEquipmentSlot slot) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return item.isForSlot(slot) ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule.StaticRule staticCheck(final @Nonnull Function<IDarkSteelItem, Boolean> func) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return func.apply(item) ? CheckResult.PASS : CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule.Prerequisite withPrerequisite(final @Nonnull IDarkSteelUpgrade upgrade) {
    return () -> upgrade;
  }

  /**
   * Creates a rule for multi-level upgrades to depend on the next lower tier upgrade or, for the lowest level, to require no upgrade to be present.
   * 
   * @param level
   *          the level of the upgrade
   * @param upgrades
   *          dependencies for the levels starting at level <em>1</em>. (The first element (index 0) is the dependency for level 1.)
   * @return Either a {@link Prerequisite} on the given upgrade for the given level or a <code>not(or())</code> of all given upgrades.
   */
  @SuppressWarnings("null")
  public static @Nonnull IRule withLevels(int level, IDarkSteelUpgrade... upgrades) {
    if (level == 0 || upgrades[level - 1] == null) {
      return not(or(Stream.of(upgrades).filter(upgrade -> upgrade != null).map(Rules::withPrerequisite).toArray(IRule.Prerequisite[]::new)));
    }
    return withPrerequisite(upgrades[level - 1]);
  }

  @SuppressWarnings("null")
  public static @Nonnull IRule withLevels(int level, List<? extends IDarkSteelUpgrade> upgrades) {
    if (level == 0 || upgrades.get(level - 1) == null) {
      return not(or(upgrades.stream().filter(upgrade -> upgrade != null).map(Rules::withPrerequisite).toArray(IRule.Prerequisite[]::new)));
    }
    return withPrerequisite(upgrades.get(level - 1));
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
  public static @Nonnull IRule.StaticRule itemTypeTooltip(final @Nonnull ILang lang) {
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

  /**
   * Generates a rule that always success but will list the given language element in the "can be applied to" list of the upgrade's tooltips. You need to add
   * additional rules to actually implement the check(s).
   * <p>
   * This is a separate rule because the tooltip list is considered an "or" list, but rules are checked "and".
   * 
   * @param slot
   *          And {@link EntityEquipmentSlot} to add to the tooltip. Note that only the armor values have meaningful results.
   * @return the generated rule
   */
  public static @Nonnull IRule.StaticRule itemTypeTooltip(final @Nonnull EntityEquipmentSlot slot) {
    switch (slot) {
    case CHEST:
      return itemTypeTooltip(Lang.DSU_CLASS_ARMOR_CHEST);
    case FEET:
      return itemTypeTooltip(Lang.DSU_CLASS_ARMOR_FEET);
    case HEAD:
      return itemTypeTooltip(Lang.DSU_CLASS_ARMOR_HEAD);
    case LEGS:
      return itemTypeTooltip(Lang.DSU_CLASS_ARMOR_LEGS);
    case MAINHAND:
      return itemTypeTooltip(Lang.DSU_CLASS_TOOLS);
    case OFFHAND:
    default:
      return itemTypeTooltip(Lang.DSU_CLASS_EVERYTHING);
    }
  }

  public static @Nonnull IRule.StaticRule or(final @Nonnull IRule.StaticRule... rules) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        for (IRule rule : rules) {
          if (rule.check(stack, item).passes()) {
            return CheckResult.PASS;
          }
        }
        return CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule or(final @Nonnull IRule... rules) {
    return new IRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        for (IRule rule : rules) {
          if (rule.check(stack, item).passes()) {
            return CheckResult.PASS;
          }
        }
        return CheckResult.SILENT_FAIL;
      }

    };
  }

  public static @Nonnull IRule.StaticRule not(final @Nonnull IRule.StaticRule rule) {
    return new IRule.StaticRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return rule.check(stack, item).passes() ? CheckResult.SILENT_FAIL : CheckResult.PASS;
      }

    };
  }

  public static @Nonnull IRule not(final @Nonnull IRule rule) {
    return new IRule() {

      @Override
      @Nonnull
      public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
        return rule.check(stack, item).passes() ? CheckResult.SILENT_FAIL : CheckResult.PASS;
      }

    };
  }

  public static final @Nonnull IRule WEAPONS = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isWeapon() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static final @Nonnull IRule TOOL_BLOCK_BREAKING = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isBlockBreakingTool() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static final @Nonnull IRule PICKAXE = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isPickaxe() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

  public static final @Nonnull IRule AXE = new IRule.StaticRule() {

    @Override
    @Nonnull
    public CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return item.isAxe() ? CheckResult.PASS : CheckResult.SILENT_FAIL;
    }

  };

}

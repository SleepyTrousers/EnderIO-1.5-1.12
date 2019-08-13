package crazypants.enderio.api.upgrades;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public interface IRule {

  final class CheckResult {
    public static final @Nonnull IRule.CheckResult PASS = new CheckResult(true);
    public static final @Nonnull IRule.CheckResult SILENT_FAIL = new CheckResult(false);

    private final boolean passed;
    private final @Nullable ITextComponent result;

    public CheckResult(ITextComponent result) {
      this.passed = false;
      this.result = result;
    }

    private CheckResult(boolean passed) {
      this.passed = passed;
      this.result = null;
    }

    public boolean passes() {
      return passed;
    }

    public boolean hasResult() {
      return result != null;
    }

    public @Nonnull ITextComponent getResult() {
      if (result != null) {
        return result;
      }
      throw new NullPointerException();
    }

  }

  /**
   * Checks if this rule applies to the given item.
   * 
   * @param stack
   *          An itemstack to test.
   * @param item
   *          The item of the stack, pre-cast to {@link IDarkSteelItem}
   * @return True if this upgrade can be applied to the given item.
   */
  @Nonnull
  IRule.CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item);

  /**
   * Marks the rule as one that only checks the type of the item, not its current state. Only those rules are used when the GUI has to determine which slots to
   * show.
   *
   */
  interface StaticRule extends IRule {

  }

  /**
   * Marks the rule as one checking for the existence of another upgrade. Those will be shown in the upgrade item tooltip under the "depends on" header.
   * <p>
   * Should <em>not</em>be used together with {@link WithTooltip} or {@link ItemType}.
   * <p>
   * Note: The default implementation of {@link #check(ItemStack, IDarkSteelItem)} should be used whenever possible.
   * <p>
   * Note 2: The Energy Upgrade has a predefined rule for "any energy upgrade".
   *
   */
  interface Prerequisite extends IRule {

    @Override
    @Nonnull
    default IRule.CheckResult check(@Nonnull ItemStack stack, @Nonnull IDarkSteelItem item) {
      return getPrerequisite().hasUpgrade(stack, item) ? CheckResult.PASS
          : new CheckResult( //
              new TextComponentTranslation("enderio.darksteel.upgrades.check.prerequisite.missing", //
                  new TextComponentTranslation(getPrerequisite().getUnlocalizedName() + ".name")));
    }

    @Nonnull
    IDarkSteelUpgrade getPrerequisite();
  }

  /**
   * Marks the rule as one checking the general type of item (weapon, tool, ...). Those will be shown in the upgrade item tooltip under the "can be applied to"
   * header.
   * <p>
   * Note that the tooltip is fetched without querying check() first, as there is no item to check against when showing the tooltip for the upgrade item.
   * <p>
   * Should <em>not</em>be used together with {@link WithTooltip} or {@link Prerequisite}.
   *
   */
  interface ItemType extends IRule {
    @Nonnull
    ITextComponent getTooltip();

    /**
     * Helper interface for anonymous implementations
     *
     */
    interface Static extends IRule.ItemType, IRule.StaticRule {
    }
  }

  /**
   * Marks the rule as having a tooltip for the upgrade item. Those tooltips will be shown at the end of the tooltip.
   * <p>
   * Note that the tooltip is fetched without querying check() first, as there is no item to check against when showing the tooltip for the upgrade item.
   * <p>
   * Should <em>not</em>be used together with {@link ItemType} or {@link Prerequisite}.
   *
   */
  interface WithTooltip extends IRule {
    @Nonnull
    ITextComponent getTooltip();
  }

}

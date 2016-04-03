package crazypants.enderio.conduit.item.filter;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.client.gui.button.CycleButton.ICycleEnum;

import crazypants.enderio.EnderIO;
import crazypants.enderio.gui.IconEIO;

public enum FuzzyMode {
  DISABLED() {
    @Override
    boolean compare(ItemStack stack) {
      return false;
    }
  },
  FUZZY_25() {
    @Override
    boolean compare(ItemStack stack) {
      return compareQuarter(stack, 1);
    }
  },
  FUZZY_50() {
    @Override
    boolean compare(ItemStack stack) {
      return compareQuarter(stack, 2);
    }
  },
  FUZZY_75() {
    @Override
    boolean compare(ItemStack stack) {
      return compareQuarter(stack, 3);
    }
  },
  FUZZY_99() {
    @Override
    boolean compare(ItemStack stack) {
      return stack.getItemDamage() >= 1;
    }
  };

  @SideOnly(Side.CLIENT)
  @SuppressWarnings("hiding")
  public static enum IconHolder implements ICycleEnum {
    DISABLED(FuzzyMode.DISABLED, IconEIO.FILTER_FUZZY_DISABLED),
    FUZZY_25(FuzzyMode.FUZZY_25, IconEIO.FILTER_FUZZY_25),
    FUZZY_50(FuzzyMode.FUZZY_50, IconEIO.FILTER_FUZZY_50),
    FUZZY_75(FuzzyMode.FUZZY_75, IconEIO.FILTER_FUZZY_75),
    FUZZY_99(FuzzyMode.FUZZY_99, IconEIO.FILTER_FUZZY_99);

    private final FuzzyMode mode;
    private final IWidgetIcon icon;

    IconHolder(FuzzyMode mode, IWidgetIcon icon) {
      this.mode = mode;
      this.icon = icon;
    }

    @Override
    public IWidgetIcon getIcon() {
      return icon;
    }

    @Override
    public List<String> getTooltipLines() {
      return Collections.singletonList(EnderIO.lang.localize("gui.conduit.item.fuzzy.".concat(name().toLowerCase(Locale.US))));
    }

    public FuzzyMode getMode() {
      return mode;
    }

    public static IconHolder getFromMode(FuzzyMode mode) {
      for (IconHolder holder : values()) {
        if (holder.mode == mode) {
          return holder;
        }
      }
      return DISABLED;
    }

  }

  abstract boolean compare(ItemStack stack);

  private static boolean compareQuarter(ItemStack stack, int ratio) {
    return stack.getItemDamage() * 4 >= stack.getMaxDamage() * ratio;
  }

}

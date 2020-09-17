package crazypants.enderio.base.filter.redstone;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.CombinedSignal;
import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.base.lang.Lang;
import crazypants.enderio.util.EnumReader;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogicOutputSignalFilter implements IOutputSignalFilter {

  private static final @Nonnull String NBT_SIGNAL_COLORS = "signalColors";
  private static final @Nonnull String NBT_COLOR = "color";
  private static final @Nonnull String NBT_TYPE = "type";
  private static final @Nonnull String NBT_FILTER_TYPE = "filterType";

  @Nonnull
  protected static CombinedSignal invertSignal(CombinedSignal sig) {
    return sig.getStrength() > 0 ? CombinedSignal.NONE : CombinedSignal.MAX;
  }

  public enum EnumSignalFilterType implements ILogicSignalFilterType {
    OR(2, Lang.GUI_REDSTONE_FILTER_OR) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (sigColor != null && bundledSignal.getSignal(sigColor).getStrength() > CombinedSignal.NONE.getStrength()) {
            return CombinedSignal.MAX;
          }
        }
        return CombinedSignal.NONE;
      }

    },
    AND(2, Lang.GUI_REDSTONE_FILTER_AND) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (sigColor != null && !(bundledSignal.getSignal(sigColor).getStrength() > CombinedSignal.NONE.getStrength())) {
            return CombinedSignal.NONE;
          }
        }
        return CombinedSignal.MAX;
      }

    },

    NAND(2, Lang.GUI_REDSTONE_FILTER_NAND) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        return invertSignal(AND.apply(bundledSignal, signalColors));
      }

    },

    NOR(2, Lang.GUI_REDSTONE_FILTER_NOR) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        return invertSignal(OR.apply(bundledSignal, signalColors));
      }

    },

    XOR(2, Lang.GUI_REDSTONE_FILTER_XOR) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        boolean output = false;
        for (DyeColor sigColor : signalColors) {
          if (sigColor != null && bundledSignal.getSignal(sigColor).getStrength() > CombinedSignal.NONE.getStrength()) {
            output = !output;
          }
        }
        return output ? CombinedSignal.MAX : CombinedSignal.NONE;
      }

    },

    XNOR(2, Lang.GUI_REDSTONE_FILTER_XNOR) {

      @Override
      @Nonnull
      public CombinedSignal apply(@Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        return invertSignal(XOR.apply(bundledSignal, signalColors));
      }

    },

    ;

    private final int numButtons;
    private final ILang headingLang;

    EnumSignalFilterType(int numButtons, @Nonnull ILang headingLang) {
      this.numButtons = numButtons;
      this.headingLang = headingLang;
    }

    @Override
    public int getNumButtons() {
      return numButtons;
    }

    @Override
    @Nonnull
    public String getHeading() {
      return headingLang.get();
    }

  }

  private final @Nonnull List<DyeColor> signalColors;
  private @Nonnull EnumSignalFilterType type;

  public LogicOutputSignalFilter() {
    this(EnumSignalFilterType.OR);
  }

  public LogicOutputSignalFilter(@Nonnull EnumSignalFilterType type) {
    this.type = type;
    signalColors = new ArrayList<DyeColor>(type.getNumButtons());
    for (int i = 0; i < type.getNumButtons(); i++) {
      signalColors.add(DyeColor.fromIndex(i));
    }
  }

  public void setColor(int index, @Nonnull DyeColor color) {
    if (index < signalColors.size()) {
      signalColors.set(index, color);
    }
  }

  public int getNumColors() {
    return signalColors.size();
  }

  @Nonnull
  public DyeColor getColor(int index) {
    if (index < signalColors.size()) {
      return NullHelper.first(signalColors.get(index), DyeColor.fromIndex(0));
    }
    return DyeColor.fromIndex(0);
  }

  @Nonnull
  public List<DyeColor> getSignalColors() {
    return signalColors;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    signalColors.clear();

    NBTTagCompound t = nbtRoot.getCompoundTag(NBT_FILTER_TYPE);
    type = EnumReader.get(EnumSignalFilterType.class, t.getInteger(NBT_TYPE));

    NBTTagList tagList = nbtRoot.getTagList(NBT_SIGNAL_COLORS, nbtRoot.getId());
    for (int i = 0; i < tagList.tagCount(); i++) {
      signalColors.add(DyeColor.fromIndex(tagList.getCompoundTagAt(i).getInteger(NBT_COLOR)));
    }
  }

  @Override
  public void writeToNBT(@Nonnull NBTTagCompound nbtRoot) {
    NBTTagList colorList = new NBTTagList();

    int index = 0;
    for (DyeColor color : signalColors) {
      NBTTagCompound root = new NBTTagCompound();
      if (color != null) {
        root.setInteger("index", index);
        root.setInteger(NBT_COLOR, color.ordinal());
        colorList.appendTag(root);
      }
      index++;
    }
    NBTTagCompound t = new NBTTagCompound();
    t.setInteger(NBT_TYPE, type.ordinal());
    nbtRoot.setTag(NBT_FILTER_TYPE, t);
    nbtRoot.setTag(NBT_SIGNAL_COLORS, colorList);
  }

  @Override
  @Nonnull
  public CombinedSignal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return type.apply(bundledSignal, getSignalColors());
  }

  @Nonnull
  public String getHeading() {
    return type.getHeading();
  }

}

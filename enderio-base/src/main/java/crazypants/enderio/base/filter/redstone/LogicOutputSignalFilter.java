package crazypants.enderio.base.filter.redstone;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import crazypants.enderio.base.lang.ILang;
import crazypants.enderio.base.lang.Lang;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogicOutputSignalFilter implements IOutputSignalFilter {

  public enum EnumSignalFilterType implements ILogicSignalFilterType {
    OR(2, Lang.GUI_REDSTONE_FILTER_OR) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength()) {
            return Signal.MAX;
          }
        }
        return Signal.NONE;
      }

    },
    AND(2, Lang.GUI_REDSTONE_FILTER_AND) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (!(bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength())) {
            return Signal.NONE;
          }
        }
        return Signal.MAX;
      }

    },

    NAND(2, Lang.GUI_REDSTONE_FILTER_NAND) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (!(bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength())) {
            return Signal.MAX;
          }
        }
        return Signal.NONE;
      }

    },

    NOR(2, Lang.GUI_REDSTONE_FILTER_NOR) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength()) {
            return Signal.NONE;
          }
        }
        return Signal.MAX;
      }

    },

    XOR(2, Lang.GUI_REDSTONE_FILTER_XOR) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        boolean output = false;
        for (DyeColor sigColor : signalColors) {
          if (bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength()) {
            output = !output;
          }
        }
        return output ? Signal.MAX : Signal.NONE;
      }

    },

    XNOR(2, Lang.GUI_REDSTONE_FILTER_XNOR) {

      @Override
      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        boolean output = false;
        for (DyeColor sigColor : signalColors) {
          if (bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength()) {
            output = !output;
          }
        }
        return !output ? Signal.MAX : Signal.NONE;
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
      return signalColors.get(index);
    }
    return DyeColor.fromIndex(0);
  }

  public List<DyeColor> getSignalColors() {
    return signalColors;
  }

  @Override
  public void readFromNBT(@Nonnull NBTTagCompound nbtRoot) {
    signalColors.clear();

    NBTTagCompound t = nbtRoot.getCompoundTag("filterType");
    type = EnumSignalFilterType.values()[t.getInteger("type")];

    NBTTagList tagList = nbtRoot.getTagList("signalColors", nbtRoot.getId());
    for (int i = 0; i < tagList.tagCount(); i++) {
      signalColors.add(DyeColor.fromIndex(tagList.getCompoundTagAt(i).getInteger("color")));
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
        root.setInteger("color", color.ordinal());
        colorList.appendTag(root);
      }
      index++;
    }
    NBTTagCompound t = new NBTTagCompound();
    t.setInteger("type", type.ordinal());
    nbtRoot.setTag("filterType", t);
    nbtRoot.setTag("signalColors", colorList);
  }

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return type.apply(color, bundledSignal, getSignalColors());
  }

  @Nonnull
  public String getHeading() {
    return type.getHeading();
  }

  @Override
  public boolean isDefault() {
    boolean result = true;
    for (int i = 0; i < getNumColors(); i++) {
      if (signalColors.get(i) != DyeColor.fromIndex(i)) {
        result = false;
      }
    }
    return result;
  }

}

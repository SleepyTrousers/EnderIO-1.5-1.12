package crazypants.enderio.base.filter.redstone;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.common.network.NetworkUtil;
import com.enderio.core.common.util.DyeColor;

import crazypants.enderio.base.conduit.redstone.signals.BundledSignal;
import crazypants.enderio.base.conduit.redstone.signals.Signal;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class LogicOutputSignalFilter implements IOutputSignalFilter {

  public enum EnumSignalFilterType {
    OR(2) {

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
    AND(2) {

      @Nonnull
      public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors) {
        for (DyeColor sigColor : signalColors) {
          if (!(bundledSignal.getSignal(sigColor).getStrength() > Signal.NONE.getStrength())) {
            return Signal.NONE;
          }
        }
        return Signal.MAX;
      }

    }

    ;

    private final int numButtons;

    EnumSignalFilterType(int numButtons) {
      this.numButtons = numButtons;
    }

    public int getNumButtons() {
      return numButtons;
    }

    public abstract Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal, @Nonnull List<DyeColor> signalColors);
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
  public void writeToByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound root = new NBTTagCompound();
    writeToNBT(root);
    NetworkUtil.writeNBTTagCompound(root, buf);
  }

  @Override
  public void readFromByteBuf(@Nonnull ByteBuf buf) {
    NBTTagCompound tag = NetworkUtil.readNBTTagCompound(buf);
    readFromNBT(tag);
  }

  @Override
  @Nonnull
  public Signal apply(@Nonnull DyeColor color, @Nonnull BundledSignal bundledSignal) {
    return type.apply(color, bundledSignal, getSignalColors());
  }

  @Nonnull
  public String getHeading() {
    return "Redstone Filter";
  }

}

package crazypants.enderio.capacitor;

import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.EnderIO;

public class NBTCapacitorData implements ICapacitorData {

  private final String unlocalizedName;
  private final int baselevel;
  private final NBTTagCompound tag;

  public NBTCapacitorData(String unlocalizedName, int baselevel, NBTTagCompound tag) {
    this.unlocalizedName = unlocalizedName;
    this.baselevel = baselevel;
    this.tag = tag;
  }

  @Override
  public String getUnlocalizedName() {
    return unlocalizedName;
  }

  @Override
  public float getUnscaledValue(CapacitorKey key) {
    if (tag.hasKey(key.getName(), 99)) {
      return tag.getFloat(key.getName());
    }
    if (tag.hasKey(key.getOwner().getUnlocalisedName(), 10)) {
      NBTTagCompound subtag = tag.getCompoundTag(key.getOwner().getUnlocalisedName());
      if (subtag.hasKey(key.getValueType().getName(), 99)) {
        return subtag.getFloat(key.getValueType().getName());
      }
    }
    if (tag.hasKey(key.getValueType().getName(), 99)) {
      return tag.getFloat(key.getValueType().getName());
    }
    return baselevel;
  }

  @Override
  public String getLocalizedName() {
    return EnderIO.lang.localizeExact(unlocalizedName + ".name");
  }

}
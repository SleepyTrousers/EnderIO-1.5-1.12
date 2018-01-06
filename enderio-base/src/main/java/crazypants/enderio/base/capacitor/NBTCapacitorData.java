package crazypants.enderio.base.capacitor;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.nbt.NBTTagCompound;

public class NBTCapacitorData implements ICapacitorData {

  private final @Nonnull String unlocalizedName;
  private final float defaultlevel;
  private final @Nonnull NBTTagCompound tag;

  public NBTCapacitorData(@Nonnull String unlocalizedName, float defaultlevel, @Nonnull NBTTagCompound tag) {
    this.unlocalizedName = unlocalizedName;
    this.defaultlevel = defaultlevel;
    this.tag = tag;
  }

  @Override
  public @Nonnull String getUnlocalizedName() {
    return unlocalizedName;
  }

  @Override
  public float getUnscaledValue(@Nonnull ICapacitorKey key) {
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
    return defaultlevel;
  }

  @Override
  public @Nonnull String getLocalizedName() {
    return EnderIO.lang.localizeExact(unlocalizedName + ".name");
  }

}
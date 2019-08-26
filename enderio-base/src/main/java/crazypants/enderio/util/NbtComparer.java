package crazypants.enderio.util;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class NbtComparer {
  private final HashSet<String> invarientTagsKeys;

  public NbtComparer() {
    this.invarientTagsKeys = new HashSet<>();
  }

  public void addInvarientTagKey(String tagKey) {
    this.invarientTagsKeys.add(tagKey);
  }

  public boolean removeInvarientTagKey(String tagKey) {
    return this.invarientTagsKeys.remove(tagKey);
  }

  public boolean compare(NBTTagCompound left, NBTTagCompound right) {
    if (left == right) { // reference equality
      return true;
    }

    // I can't imagine NBT trees being deep enough to cause StackOverflow
    Set<String> leftKeys = left.getKeySet();
    Set<String> rightKeys = right.getKeySet();

    for (String key : leftKeys) {
      if (this.invarientTagsKeys.contains(key) || key == null) {
        continue;
      }

      if (!rightKeys.contains(key)) {
        return false;
      }

      if (!this.compareInternal(left.getTag(key), right.getTag(key))) {
        return false;
      }
    }

    return true;
  }

  private boolean compareInternal(NBTBase left, NBTBase right) {
    if (left instanceof NBTTagCompound && right instanceof NBTTagCompound) {
      return compare((NBTTagCompound) left, (NBTTagCompound) right);
    }

    return left.equals(right);
  }
}

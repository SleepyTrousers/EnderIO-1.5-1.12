package crazypants.enderio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

public interface IModObject {

  @Nonnull
  String getUnlocalisedName();

  @Nullable
  Block getBlock();

  @Nullable
  Item getItem();

}
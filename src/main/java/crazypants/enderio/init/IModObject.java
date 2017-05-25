package crazypants.enderio.init;

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

  public static interface Registerable extends IModObject {

    Class<?> getClazz();

    String getMethodName();

    void setItem(Item obj);

    void setBlock(Block obj);

  }

}
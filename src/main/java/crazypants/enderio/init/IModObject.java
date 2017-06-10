package crazypants.enderio.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public interface IModObject {

  @Nonnull
  String getUnlocalisedName();

  @Nonnull
  ResourceLocation getRegistryName();

  @Nonnull
  <B extends Block> B apply(@Nonnull B block);

  @Nonnull
  <I extends Item> I apply(@Nonnull I item);

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
package crazypants.enderio.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

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

    @Nonnull
    Class<?> getClazz();

    @Nullable
    String getBlockMethodName();

    @Nullable
    String getItemMethodName();

    @Nullable
    Class<? extends TileEntity> getTileClass();

    void setItem(@Nullable Item obj);

    void setBlock(@Nullable Block obj);

  }

  /**
   * Interface to be implemented on block that are created from modObjects. It will be called at the right time to create and register the blockItem. Note that
   * the method shall do the registering itself---the return value is only for storage in the ModObject.
   *
   */
  public static interface WithBlockItem {

    Item createBlockItem(@Nonnull IModObject modObject);

  }

  public static interface LifecycleInit {

    void init(@Nonnull FMLInitializationEvent event);

  }

  public static interface LifecyclePostInit {

    void init(@Nonnull FMLPostInitializationEvent event);

  }

}
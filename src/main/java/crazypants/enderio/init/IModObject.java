package crazypants.enderio.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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

  @Nullable
  Class<? extends TileEntity> getTileClass();

  void preInit(@Nonnull FMLPreInitializationEvent event);

  void init(@Nonnull FMLInitializationEvent event);

  public static interface Registerable extends IModObject {

    Class<?> getClazz();

    String getMethodName();

    void setItem(Item obj);

    void setBlock(Block obj);

  }

}
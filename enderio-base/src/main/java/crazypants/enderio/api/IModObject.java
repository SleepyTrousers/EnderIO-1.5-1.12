package crazypants.enderio.api;

import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.stackable.IProducer;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IModObject extends IProducer, IForgeRegistryEntry<IModObject> {

  @Nonnull
  String getUnlocalisedName();

  @Override
  @Nonnull
  ResourceLocation getRegistryName();

  @Nonnull
  <B extends Block> B apply(@Nonnull B block);

  @Nonnull
  <I extends Item> I apply(@Nonnull I item);

  @Nullable
  Class<? extends TileEntity> getTEClass();

  @Nonnull
  Function<IModObject, Block> getBlockCreator();

  @Nonnull
  BiFunction<IModObject, Block, Item> getItemCreator();

  @Nullable
  IModTileEntity getTileEntity();

  boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer);

  boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side);

  boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side, int param);

  boolean openGui(@Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c);

  boolean openClientGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side, int param);

  boolean openClientGui(@Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c);

  /**
   * Interface to be implemented on blocks that are created from modObjects. It will be called at the right time to create and register the blockItem. Note that
   * the method shall NOT do the registering itself.
   *
   */
  public interface WithBlockItem {

    default @Nullable Item createBlockItem(@Nonnull IModObject modObject) {
      return modObject.apply(new ItemBlock((Block) this));
    };

    @Nonnull
    BiFunction<IModObject, Block, Item> itemCreator = (modobject, block) -> {
      if (modobject == null) {
        throw new NullPointerException();
      }
      if (block instanceof IModObject.WithBlockItem) {
        return ((IModObject.WithBlockItem) block).createBlockItem(modobject);
      }
      return null;
    };

  }

  public interface LifecycleInit {

    void init(@Nonnull IModObject modObject, @Nonnull FMLInitializationEvent event);

  }

  public interface LifecyclePostInit {

    void init(@Nonnull IModObject modObject, @Nonnull FMLPostInitializationEvent event);

  }

}

package crazypants.enderio.base.init;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.api.IModTileEntity;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.gui.handler.GuiHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IModObjectBase extends IModObject {

  @Override
  default IModObject setRegistryName(ResourceLocation name) {
    throw new UnsupportedOperationException();
  }

  @Override
  default @Nonnull ResourceLocation getRegistryName() {
    return new ResourceLocation(EnderIO.DOMAIN, getUnlocalisedName());
  }

  @Override
  default Class<IModObject> getRegistryType() {
    return IModObject.class;
  }

  @Override
  default @Nonnull <B extends Block> B apply(@Nonnull B block) {
    block.setUnlocalizedName(getUnlocalisedName());
    block.setRegistryName(getRegistryName());
    return block;
  }

  @Override
  default @Nonnull <I extends Item> I apply(@Nonnull I item) {
    item.setUnlocalizedName(getUnlocalisedName());
    item.setRegistryName(getRegistryName());
    return item;
  }

  @Override
  default @Nullable Class<? extends TileEntity> getTEClass() {
    IModTileEntity tileEntity = getTileEntity();
    if (tileEntity != null) {
      return tileEntity.getTileEntityClass();
    }
    return null;
  }

  @Override
  default Block getBlock() {
    return ModObjectRegistry.getBlock(this);
  }

  @Override
  default @Nonnull Block getBlockNN() {
    return ModObjectRegistry.getBlockNN(this);
  }

  @Override
  default Item getItem() {
    return ModObjectRegistry.getItem(this);
  }

  @Override
  default @Nonnull Item getItemNN() {
    return ModObjectRegistry.getItemNN(this);
  }

  @Override
  default boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer) {
    return openGui(world, pos, entityPlayer, null, 0);
  }

  @Override
  default boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side) {
    return openGui(world, pos, entityPlayer, side, 0);
  }

  @Override
  default boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side, int param) {
    return GuiHelper.openGui(this, world, pos, entityPlayer, side, param);
  }

  @Override
  default boolean openGui(@Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c) {
    return GuiHelper.openGui(this, world, entityPlayer, a, b, c);
  }

  @Override
  default boolean openClientGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nullable EnumFacing side, int param) {
    return GuiHelper.openClientGui(this, world, pos, entityPlayer, side, param);
  }

  @Override
  default boolean openClientGui(@Nonnull World world, @Nonnull EntityPlayer entityPlayer, int a, int b, int c) {
    return GuiHelper.openClientGui(this, world, entityPlayer, a, b, c);
  }

}

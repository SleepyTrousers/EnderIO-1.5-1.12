package crazypants.util;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3Pool;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IBlockAccessWrapper implements IBlockAccess {

  protected IBlockAccess wrapped;

  public IBlockAccessWrapper(IBlockAccess ba) {
    wrapped = ba;
  }

  @Override
  public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
    return wrapped.isSideSolid(x, y, z, side, _default);
  }

  @Override
  public int isBlockProvidingPowerTo(int var1, int var2, int var3, int var4) {
    return wrapped.isBlockProvidingPowerTo(var1, var2, var3, var4);
  }

  @Override
  public boolean isAirBlock(int var1, int var2, int var3) {
    return wrapped.isAirBlock(var1, var2, var3);
  }

  @Override
  public Vec3Pool getWorldVec3Pool() {
    return wrapped.getWorldVec3Pool();
  }

  @Override
  public TileEntity getTileEntity(int var1, int var2, int var3) {

    return wrapped.getTileEntity(var1, var2, var3);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getLightBrightnessForSkyBlocks(int var1, int var2, int var3, int var4) {
    return 15 << 20 | 15 << 4;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public int getHeight() {
    return wrapped.getHeight();
  }

  @Override
  public int getBlockMetadata(int var1, int var2, int var3) {
    return wrapped.getBlockMetadata(var1, var2, var3);
  }

  @Override
  public Block getBlock(int var1, int var2, int var3) {
    return wrapped.getBlock(var1, var2, var3);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BiomeGenBase getBiomeGenForCoords(int var1, int var2) {

    return wrapped.getBiomeGenForCoords(var1, var2);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public boolean extendedLevelsInChunkCache() {
    return wrapped.extendedLevelsInChunkCache();
  }

}

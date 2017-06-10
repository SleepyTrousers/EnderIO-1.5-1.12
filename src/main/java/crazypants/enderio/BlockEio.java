package crazypants.enderio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.BlockEnder;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public abstract class BlockEio<T extends TileEntityEio> extends BlockEnder<T> {

  protected @Nonnull String permissionNodeWrenching = "(block not initialized)";
  protected final @Nonnull IModObject modobject;

  protected BlockEio(@Nonnull IModObject modObject, @Nullable Class<T> teClass) {
    super(teClass);
    this.modobject = modObject;
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  protected BlockEio(@Nonnull IModObject modObject, @Nullable Class<T> teClass, @Nonnull Material mat) {
    super(teClass, mat);
    this.modobject = modObject;
    modObject.apply(this);
    setCreativeTab(EnderIOTab.tabEnderIOMachines);
  }

  /**
   * Stuff that has to be done in the preInit phase (as opposed to init/postInit)
   */
  public void preInit(@Nonnull FMLPreInitializationEvent event) {
  }

  /**
   * Stuff that has to be done in the init phase (as opposed to preInit/postInit)
   */
  public void init(@Nonnull FMLInitializationEvent event) {
    permissionNodeWrenching = PermissionAPI.registerNode(EnderIO.DOMAIN + ".wrench." + modobject.getUnlocalisedName(), DefaultPermissionLevel.ALL,
        "Permission to wrench-break the block " + modobject.getUnlocalisedName() + " of Ender IO");
  }

  @Override
  public boolean onBlockActivated(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull EntityPlayer entityPlayer,
      @Nonnull EnumHand hand, @Nonnull EnumFacing side, float hitX, float hitY, float hitZ) {
    if (shouldWrench(world, pos, entityPlayer, side) && ToolUtil.breakBlockWithTool(this, world, pos, side, entityPlayer, hand, permissionNodeWrenching)) {
      return true;
    }

    TileEntity te = getTileEntity(world, pos);
    if (te instanceof AbstractMachineEntity) {
      ITool tool = ToolUtil.getEquippedTool(entityPlayer, hand);
      if (tool != null && !entityPlayer.isSneaking() && tool.canUse(entityPlayer.getHeldItem(hand), entityPlayer, pos)) {
        ((AbstractMachineEntity) te).toggleIoModeForFace(side);
        IBlockState bs = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, bs, bs, 3);
        return true;
      }
    }

    return super.onBlockActivated(world, pos, state, entityPlayer, hand, side, hitX, hitY, hitZ);
  }

  public boolean shouldWrench(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    return true;
  }
}

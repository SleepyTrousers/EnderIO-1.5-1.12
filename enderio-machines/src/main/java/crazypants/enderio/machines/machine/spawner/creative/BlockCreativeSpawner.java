package crazypants.enderio.machines.machine.spawner.creative;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.ContainerEnderCap;
import com.enderio.core.common.inventory.EnderInventory;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.machine.base.block.AbstractCapabilityMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BlockCreativeSpawner extends AbstractCapabilityMachineBlock<TileCreativeSpawner>
    implements IPaintable.ISolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint {

  public static BlockCreativeSpawner create(@Nonnull IModObject modObject) {
    BlockCreativeSpawner res = new BlockCreativeSpawner(modObject);
    res.init();
    return res;
  }

  protected BlockCreativeSpawner(@Nonnull IModObject modObject) {
    super(modObject);
    setShape(mkShape(BlockFaceShape.SOLID));
    setBlockUnbreakable();
    setResistance(6000000.0F);
  }

  private static @Nonnull String permissionGUI = "(container not initialized)";

  @Override
  public void init(@Nonnull IModObject modObject, @Nonnull FMLInitializationEvent event) {
    // NO super here, we register the perms with OP default
    permissionGUI = PermissionAPI.registerNode(EnderIO.DOMAIN + ".creative_spawner.edit", DefaultPermissionLevel.OP,
        "Permission to edit the creative spawner's settings.");
    permissionNodeWrenching = PermissionAPI.registerNode(EnderIO.DOMAIN + ".wrench.break." + modObject.getUnlocalisedName(), DefaultPermissionLevel.OP,
        "Permission to wrench-break the block " + modObject.getUnlocalisedName() + " of Ender IO");
    permissionNodeIOWrenching = PermissionAPI.registerNode(EnderIO.DOMAIN + ".wrench.iomode." + modObject.getUnlocalisedName(), DefaultPermissionLevel.OP,
        "Permission to set IO mode by wrench-clicking the block " + modObject.getUnlocalisedName() + " of Ender IO");
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    final boolean hasPermission = PermissionAPI.hasPermission(entityPlayer, permissionGUI);
    if (!hasPermission) {
      TileCreativeSpawner te = getTileEntity(world, pos);
      if (te != null && te.isActive()) {
        // doesn't require payment, no gui needed
        return false;
      }
    }
    return openGui(world, pos, entityPlayer, side, hasPermission ? 0 : 1);
  }

  @Override
  public @Nonnull ContainerEnderCap<EnderInventory, TileCreativeSpawner> getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world,
      @Nonnull BlockPos pos, @Nullable EnumFacing facing, int param1, @Nonnull TileCreativeSpawner te) {
    return param1 == 0 ? new ContainerCreativeSpawner(player.inventory, te) : new ContainerCreativeSpawnerUser(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileCreativeSpawner te) {
    return new GuiCreativeSpawner(player.inventory, te, getServerGuiElement(player, world, pos, facing, param1, te));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER_NO_IO;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER_NO_IO;
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileCreativeSpawner tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  public boolean canEntityDestroy(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull Entity entity) {
    return false;
  }

}

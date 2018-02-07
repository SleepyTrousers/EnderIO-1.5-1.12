package crazypants.enderio.machines.machine.teleport.anchor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.UserIdent;

import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IDefaultRenderers;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.machines.lang.Lang;
import crazypants.enderio.machines.machine.teleport.ContainerTravelAccessable;
import crazypants.enderio.machines.machine.teleport.ContainerTravelAuth;
import crazypants.enderio.machines.machine.teleport.GuiTravelAccessable;
import crazypants.enderio.machines.machine.teleport.GuiTravelAuth;
import crazypants.enderio.machines.machine.teleport.telepad.render.TelePadRenderMapper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTravelAnchor<T extends TileTravelAnchor> extends AbstractMachineBlock<T>
    implements IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IDefaultRenderers, IHaveTESR {

  protected static final int GUI_ID_TRAVEL_ACCESSABLE = 0;

  public static BlockTravelAnchor<TileTravelAnchor> create(@Nonnull IModObject modObject) {
    BlockTravelAnchor<TileTravelAnchor> result = new BlockTravelAnchor<TileTravelAnchor>(modObject);
    result.init();
    return result;
  }

  protected BlockTravelAnchor(@Nonnull IModObject mo) {
    super(mo);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  @Override
  public boolean removedByPlayer(@Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer,
      boolean willHarvest) {
    T te = getTileEntity(world, pos);

    if (te != null) {
      if (te.getOwner().equals(UserIdent.create(entityPlayer.getGameProfile())) || (te.getAccessMode() == ITravelAccessable.AccessMode.PUBLIC)
          || (entityPlayer.isCreative() && !willHarvest)) {
        return super.removedByPlayer(state, world, pos, entityPlayer, willHarvest);
      } else {
        if (!world.isRemote) {
          entityPlayer.sendStatusMessage(Lang.GUI_AUTH_ERROR_HARVEST.toChatServer(te.getOwner().getPlayerName()), true);
        }
      }
    }
    return false;
  }

  @Override
  protected boolean openGui(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer entityPlayer, @Nonnull EnumFacing side) {
    T te = getTileEntity(world, pos);
    if (!world.isRemote && te != null) {
      if (te.canUiBeAccessed(entityPlayer)) {
        return openGui(world, pos, entityPlayer, side, GUI_ID_TRAVEL_ACCESSABLE);
      } else {
        sendPrivateStatusMessage(world, entityPlayer, te.getOwner());
      }
    }
    return true;
  }

  public static void sendPrivateStatusMessage(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull UserIdent owner) {
    if (!world.isRemote && !player.isSneaking()) {
      player.sendStatusMessage(Lang.GUI_AUTH_ERROR_PRIVATE.toChatServer(owner.getPlayerName()), true);
    }
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int ID,
      @Nonnull T te) {
    if (GUI_ID_TRAVEL_ACCESSABLE == ID) {
      return new ContainerTravelAccessable(player.inventory, te, world);
    } else {
      return new ContainerTravelAuth(player.inventory);
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing, int ID,
      @Nonnull T te) {
    if (GUI_ID_TRAVEL_ACCESSABLE == ID) {
      return new GuiTravelAccessable<T>(player.inventory, te, world);
    } else {
      return new GuiTravelAuth(player, te, world);
    }
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer<TileTravelAnchor>());
  }

}

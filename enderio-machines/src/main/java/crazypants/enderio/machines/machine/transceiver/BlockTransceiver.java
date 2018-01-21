package crazypants.enderio.machines.machine.transceiver;

import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.machine.baselegacy.AbstractPoweredTaskBlock;
import crazypants.enderio.base.machine.modes.IoMode;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.base.render.property.IOMode;
import crazypants.enderio.base.render.registry.TextureRegistry;
import crazypants.enderio.base.render.registry.TextureRegistry.TextureSupplier;
import crazypants.enderio.base.transceiver.ConnectionHandler;
import crazypants.enderio.machines.machine.transceiver.gui.ContainerTransceiver;
import crazypants.enderio.machines.machine.transceiver.gui.GuiTransceiver;
import crazypants.enderio.machines.machine.transceiver.render.TransceiverRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTransceiver extends AbstractPoweredTaskBlock<TileTransceiver>
    implements IPaintable.INonSolidBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveTESR {

  public static BlockTransceiver create(@Nonnull IModObject modObject) {

    ConnectionHandler ch = new ConnectionHandler();
    MinecraftForge.EVENT_BUS.register(ch);

    BlockTransceiver res = new BlockTransceiver(modObject);
    res.init();
    return res;
  }

  private TextureSupplier portalIcon = TextureRegistry.registerTexture("blocks/ender_still");

  private BlockTransceiver(@Nonnull IModObject modObject) {
    super(modObject, TileTransceiver.class);
  }

  @SideOnly(Side.CLIENT)
  public TextureAtlasSprite getPortalIcon() {
    return portalIcon.get(TextureAtlasSprite.class);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileTransceiver te) {
    return new ContainerTransceiver(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileTransceiver te) {
    return new GuiTransceiver(player.inventory, te);
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(@Nonnull IBlockState bs, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Random rand) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode, EnumFacing side) {
    switch (mode) {
    case NONE:
      return IOMode.EnumIOMode.NONE;
    case PULL:
      return IOMode.EnumIOMode.TRANSCIEVERPULL;
    case PUSH:
      return IOMode.EnumIOMode.TRANSCIEVERPUSH;
    case PUSH_PULL:
      return IOMode.EnumIOMode.TRANSCIEVERPUSHPULL;
    case DISABLED:
      return IOMode.EnumIOMode.TRANSCIEVERDISABLED;
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileTransceiver tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTransceiver.class, new TransceiverRenderer());
  }

}

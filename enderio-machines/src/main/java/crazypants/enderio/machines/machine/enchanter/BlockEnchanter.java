package crazypants.enderio.machines.machine.enchanter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.machine.base.block.AbstractMachineBlock;
import crazypants.enderio.base.machine.render.RenderMappers;
import crazypants.enderio.base.render.IBlockStateWrapper;
import crazypants.enderio.base.render.IHaveTESR;
import crazypants.enderio.base.render.IRenderMapper;
import crazypants.enderio.base.render.ITESRItemBlock;
import crazypants.enderio.machines.init.MachineObject;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static crazypants.enderio.machines.init.MachineObject.block_enchanter;

public class BlockEnchanter extends AbstractMachineBlock<TileEnchanter> implements IResourceTooltipProvider, ITESRItemBlock, IHaveTESR {

  public static BlockEnchanter create(@Nonnull IModObject modObject) {
    BlockEnchanter res = new BlockEnchanter();
    res.init();
    return res;
  }

  protected BlockEnchanter() {
    super(MachineObject.block_enchanter);
    setLightOpacity(0);
    setShape(mkShape(BlockFaceShape.SOLID, BlockFaceShape.UNDEFINED, BlockFaceShape.UNDEFINED));
  }

  @Override
  public boolean isOpaqueCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public boolean isFullCube(@Nonnull IBlockState bs) {
    return false;
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileEnchanter te) {
    return new ContainerEnchanter(player, player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileEnchanter te) {
    return new GuiEnchanter(player, player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileEnchanter.class, new EnchanterModelRenderer());
    ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(block_enchanter.getBlockNN()), 0, TileEnchanter.class);
  }

  @Override
  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileEnchanter tileEntity) {
    blockStateWrapper.addCacheKey(tileEntity.getFacing());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull IRenderMapper.IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.FRONT_MAPPER;
  }

}

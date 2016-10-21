package crazypants.enderio.machine.vacuum;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.redstone.IRedstoneConnectable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.teleport.telepad.render.TelePadRenderMapper;
import crazypants.util.ClientUtil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockVacuumChest extends BlockEio<TileVacuumChest> implements IGuiHandler, IResourceTooltipProvider, IRedstoneConnectable, ISmartRenderAwareBlock,
    IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveRenderers {

  public static BlockVacuumChest create() {
    PacketHandler.INSTANCE.registerMessage(PacketVaccumChest.class, PacketVaccumChest.class, PacketHandler.nextID(), Side.SERVER);
    BlockVacuumChest res = new BlockVacuumChest();
    res.init();
    return res;
  }

  protected BlockVacuumChest() {
    super(ModObject.blockVacuumChest.getUnlocalisedName(), TileVacuumChest.class);
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  @Override
  public boolean shouldRedstoneConduitConnect(World world, int x, int y, int z, EnumFacing from) {
    return true;
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
    TileEntity ent = world.getTileEntity(pos);
    if (ent instanceof TileVacuumChest) {
      ((TileVacuumChest) ent).onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  protected void init() {
    super.init();
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_VACUUM_CHEST, this);
    registerInSmartModelAttacher();
  }

  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this);
  }

  @Override
  protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, new IProperty[] { EnumRenderMode.RENDER });
  }

  @Override
  public IBlockState getStateFromMeta(int meta) {
    return getDefaultState();
  }

  @Override
  public int getMetaFromState(IBlockState state) {
    return 0;
  }

  @Override
  public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
    return getDefaultState();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public final IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    if (state != null && world != null && pos != null) {
      IBlockStateWrapper blockStateWrapper = createBlockStateWrapper(state, world, pos);
      TileVacuumChest tileEntity = getTileEntitySafe(world, pos);
      if (tileEntity != null) {
        setBlockStateWrapperCache(blockStateWrapper, world, pos, tileEntity);
      }
      blockStateWrapper.bakeModel();
      return blockStateWrapper;
    } else {
      return state;
    }
  }

  protected void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull TileVacuumChest tileEntity) {
    blockStateWrapper.addCacheKey(0);
  }

  protected @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IItemRenderMapper getItemRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return TelePadRenderMapper.instance;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    if (!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_VACUUM_CHEST, world, pos.getX(), pos.getY(), pos.getZ());
    }
    return true;
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable TileVacuumChest te, ItemStack drop) {
    drop.setTagCompound(new NBTTagCompound());
    if (te != null) {
      te.writeContentsToNBT(drop.getTagCompound());
    }
    PainterUtil2.setSourceBlock(drop, getPaintSource(null, world, pos));
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
    if (!world.isRemote) {
      TileEntity te = world.getTileEntity(pos);
      if (stack != null && stack.getTagCompound() != null && te instanceof TileVacuumChest) {
        ((TileVacuumChest) te).readContentsFromNBT(stack.getTagCompound());
        ((TileVacuumChest) te).setPaintSource(PainterUtil2.getSourceBlock(stack));        
        world.notifyBlockUpdate(pos, state, state, 3);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  public boolean isOpaqueCube(IBlockState blockStateIn) {
    return false;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileVacuumChest) {
      return new ContainerVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileVacuumChest) {
      return new GuiVacuumChest(player, player.inventory, (TileVacuumChest) te);
    }
    return null;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  @Override
  public IBlockState getFacade(IBlockAccess world, BlockPos pos, EnumFacing side) {
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : world.getBlockState(pos);
  }

  @Override
  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    TileVacuumChest te = getTileEntity(world, pos);
    if (te != null) {
      te.setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    TileVacuumChest te = getTileEntitySafe(world, pos);
    if (te != null) {
      return te.getPaintSource();
    }
    return null;
  }

  @Override
  public IBlockState getPaintSource(Block block, ItemStack stack) {
    return PainterUtil2.getSourceBlock(stack);
  }

  @Override
  public boolean canRenderInLayer(BlockRenderLayer layer) {
    return true;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager effectRenderer) {
    return PaintHelper.addHitEffects(state, world, target, effectRenderer);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager effectRenderer) {
    return PaintHelper.addDestroyEffects(world, pos, effectRenderer);
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT END
  // ///////////////////////////////////////////////////////////////////////

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(ModObject.blockVacuumChest);
  }

}

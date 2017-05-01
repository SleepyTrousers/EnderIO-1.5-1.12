package crazypants.enderio.teleport.anchor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.ChatUtil;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.ITravelAccessable;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.enderio.render.IHaveTESR;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.IRenderMapper.IItemRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.teleport.ContainerTravelAccessable;
import crazypants.enderio.teleport.ContainerTravelAuth;
import crazypants.enderio.teleport.GuiTravelAccessable;
import crazypants.enderio.teleport.GuiTravelAuth;
import crazypants.enderio.teleport.packet.PacketAccessMode;
import crazypants.enderio.teleport.packet.PacketDrainStaff;
import crazypants.enderio.teleport.packet.PacketLabel;
import crazypants.enderio.teleport.packet.PacketOpenAuthGui;
import crazypants.enderio.teleport.packet.PacketPassword;
import crazypants.enderio.teleport.packet.PacketTravelEvent;
import crazypants.enderio.teleport.telepad.render.TelePadRenderMapper;
import crazypants.util.ClientUtil;
import crazypants.util.UserIdent;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTravelAnchor<T extends TileTravelAnchor> extends BlockEio<T> implements IGuiHandler, ITileEntityProvider, IResourceTooltipProvider,
    ISmartRenderAwareBlock, IPaintable.IBlockPaintableBlock, IPaintable.IWrenchHideablePaint, IHaveRenderers, IHaveTESR {

  public static BlockTravelAnchor<TileTravelAnchor> create() {
    PacketHandler.INSTANCE.registerMessage(PacketAccessMode.class, PacketAccessMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketLabel.class, PacketLabel.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketTravelEvent.class, PacketTravelEvent.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketDrainStaff.class, PacketDrainStaff.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketOpenAuthGui.class, PacketOpenAuthGui.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPassword.Handler.class, PacketPassword.class, PacketHandler.nextID(), Side.SERVER);

    BlockTravelAnchor<TileTravelAnchor> result = new BlockTravelAnchor<TileTravelAnchor>(TileTravelAnchor.class);
    result.init();
    return result;
  }

  private BlockTravelAnchor(Class<T> clz) {
    super(ModObject.blockTravelAnchor.getUnlocalisedName(), clz);
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  public BlockTravelAnchor(@Nonnull String unlocalisedName, Class<T> teClass) {
    super(unlocalisedName, teClass);
    initDefaultState();
  }

  @Override
  protected final void init() {
    super.init();
    registerGuiHandlers();
    registerInSmartModelAttacher();
  }

  protected void registerGuiHandlers() {
    GuiID.registerGuiHandler(GuiID.GUI_ID_TRAVEL_ACCESSABLE, this);
    GuiID.registerGuiHandler(GuiID.GUI_ID_TRAVEL_AUTH, this);
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
      T tileEntity = getTileEntitySafe(world, pos);
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
      @Nonnull T tileEntity) {
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
  public TileEntity createNewTileEntity(World var1, int var2) {
    return new TileTravelAnchor();
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
    if (entity instanceof EntityPlayer) {
      TileEntity te = world.getTileEntity(pos);
      if (te instanceof TileTravelAnchor) {
        TileTravelAnchor ta = (TileTravelAnchor) te;
        ta.setPlacedBy((EntityPlayer) entity);
        IBlockState bs = PainterUtil2.getSourceBlock(stack);
        ta.setPaintSource(bs);
        te.getWorld().notifyBlockUpdate(pos, state, state, 3);
      }
    }
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    TileEntity te = world.getTileEntity(pos);
    if (!world.isRemote && te instanceof ITravelAccessable) {
      ITravelAccessable ta = (ITravelAccessable) te;
      if (ta.canUiBeAccessed(entityPlayer)) {
        GuiID.GUI_ID_TRAVEL_ACCESSABLE.openGui(world, pos, entityPlayer, side);
      } else {
        sendPrivateChatMessage(entityPlayer, ta.getOwner());
      }
    }
    return true;
  }

  public static void sendPrivateChatMessage(EntityPlayer player, UserIdent owner) {
    if (!player.isSneaking()) {
      ChatUtil.sendNoSpam(player, EnderIO.lang.localize("gui.travelAccessable.privateBlock1") + " " + TextFormatting.RED + owner.getPlayerName()
          + TextFormatting.WHITE + " " + EnderIO.lang.localize("gui.travelAccessable.privateBlock2"));
    }
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    @SuppressWarnings("null")
    T te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      if (GuiID.GUI_ID_TRAVEL_ACCESSABLE.is(ID)) {
        return new ContainerTravelAccessable(player.inventory, te, world);
      } else {
        return new ContainerTravelAuth(player.inventory);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    @SuppressWarnings("null")
    T te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      if (GuiID.GUI_ID_TRAVEL_ACCESSABLE.is(ID)) {
        return new GuiTravelAccessable<T>(player.inventory, te, world);
      } else {
        return new GuiTravelAuth(player, te, world);
      }
    }
    return null;
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable TileTravelAnchor anchor, ItemStack drop) {
    PainterUtil2.setSourceBlock(drop, getPaintSource(null, world, pos));
  }

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) {
    return false;
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public boolean isOpaqueCube(IBlockState bs) {
    return false;
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
    T te = getTileEntity(world, pos);
    if (te != null) {
      ((IPaintable.IPaintableTileEntity) te).setPaintSource(paintSource);
    }
  }

  @Override
  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    PainterUtil2.setSourceBlock(stack, paintSource);
  }

  @Override
  public IBlockState getPaintSource(IBlockState state, IBlockAccess world, BlockPos pos) {
    T te = getTileEntitySafe(world, pos);
    if (te != null) {
      return ((IPaintable.IPaintableTileEntity) te).getPaintSource();
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
    ClientUtil.registerDefaultItemRenderer(ModObject.blockTravelAnchor);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void bindTileEntitySpecialRenderer() {
    ClientRegistry.bindTileEntitySpecialRenderer(TileTravelAnchor.class, new TravelEntitySpecialRenderer<TileTravelAnchor>());
  }

}

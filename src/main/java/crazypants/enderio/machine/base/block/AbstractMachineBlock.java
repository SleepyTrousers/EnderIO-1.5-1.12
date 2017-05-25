package crazypants.enderio.machine.base.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.Util;

import crazypants.enderio.BlockEio;
import crazypants.enderio.GuiID;
import crazypants.enderio.init.IModObject;
import crazypants.enderio.init.ModObject;
import crazypants.enderio.integration.waila.IWailaInfoProvider;
import crazypants.enderio.machine.base.te.AbstractMachineEntity;
import crazypants.enderio.machine.baselegacy.AbstractPoweredMachineEntity;
import crazypants.enderio.machine.baselegacy.PacketPowerStorage;
import crazypants.enderio.machine.modes.IoMode;
import crazypants.enderio.machine.modes.PacketIoMode;
import crazypants.enderio.machine.modes.PacketIoMode.Handler;
import crazypants.enderio.machine.render.RenderMappers;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;
import crazypants.enderio.paint.render.PaintHelper;
import crazypants.enderio.render.IBlockStateWrapper;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.pipeline.BlockStateWrapperBase;
import crazypants.enderio.render.property.EnumRenderMode;
import crazypants.enderio.render.property.IOMode;
import crazypants.enderio.render.registry.SmartModelAttacher;
import crazypants.enderio.render.registry.TextureRegistry;
import crazypants.enderio.render.registry.TextureRegistry.TextureSupplier;
import com.enderio.core.common.util.NullHelper;
import crazypants.util.Prep;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockEio<T> implements IGuiHandler, IResourceTooltipProvider,
    IWailaInfoProvider, ISmartRenderAwareBlock {
  
  public static final TextureSupplier selectedFaceIcon = TextureRegistry.registerTexture("blocks/overlays/selectedFace");

  protected final Random random;

  protected final IModObject modObject;

  static {
    PacketHandler.INSTANCE.registerMessage(PacketIoMode.Handler.class, PacketIoMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerStorage.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
  }

  protected AbstractMachineBlock(@Nonnull IModObject mo, @Nullable Class<T> teClass, @Nonnull Material mat) {
    super(mo.getUnlocalisedName(), teClass, mat);
    modObject = mo;
    setHardness(2.0F);
    setSoundType(SoundType.METAL);
    setHarvestLevel("pickaxe", 0);
    random = new Random();    
    initDefaultState();
  }

  protected void initDefaultState() {
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  protected AbstractMachineBlock(@Nonnull IModObject mo, @Nullable Class<T> teClass) {
    this(mo, teClass, new Material(MapColor.IRON));
  }

  @Override
  protected void init() {
    super.init();
    GuiID.registerGuiHandler(getGuiId(), this);
    registerInSmartModelAttacher();
  }
  
  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this);
  }
  
  @Override
  protected @Nonnull BlockStateContainer createBlockState() {
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

  protected abstract void setBlockStateWrapperCache(@Nonnull IBlockStateWrapper blockStateWrapper, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
      @Nonnull T tileEntity);

  protected @Nonnull BlockStateWrapperBase createBlockStateWrapper(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
    return new BlockStateWrapperBase(state, world, pos, getBlockRenderMapper());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nonnull BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.CUTOUT;
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {
    GuiID guiId = getGuiId();
    if (guiId != null) {
      guiId.openGui(world, pos, entityPlayer, side);
    }
    return true;
  }
  
  @Override
  public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
    return false;
  }  

  @Override
  public boolean doNormalDrops(IBlockAccess world, BlockPos pos) { 
    return false;
  }

  @Override
  protected void processDrop(IBlockAccess world, BlockPos pos, @Nullable AbstractMachineEntity te, ItemStack drop) {
    if(te != null) {
      te.writeToItemStack(drop);
    }
  }

  @Override
  public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, pos, state, player, stack);
    if (world != null && pos != null) {
      AbstractMachineEntity te = getTileEntity(world, pos);
      if (te != null) {
        te.readFromItemStack(stack);
        if (player != null) {
          te.setFacing(getFacingForHeading(player));
          if (player instanceof EntityPlayer && !world.isRemote) {
            te.setOwner((EntityPlayer) player);
          }
        }
      }
      if (world.isRemote) {
        return;
      }
      world.notifyBlockUpdate(pos, state, state, 3);
    }
  }

  protected EnumFacing getFacingForHeading(EntityLivingBase player) {
    return Util.getFacingFromEntity(player);
  }

  @Override
  public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    super.onBlockAdded(world, pos,state);
    world.notifyBlockUpdate(pos, state, state, 3);
  }
  
  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
    AbstractMachineEntity te = getTileEntity(world, pos);
    if (te != null) {
      te.onNeighborBlockChange(neighborBlock);
    }
  }

  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer entityPlayer, EnumHand hand, @Nullable ItemStack heldItem,
      EnumFacing side,
      float hitX, float hitY, float hitZ) {
    T tile = getTileEntity(world, pos);
    if (Prep.isValid(heldItem) && tile instanceof AbstractPoweredMachineEntity) {
      AbstractPoweredMachineEntity machine = (AbstractPoweredMachineEntity) tile;
      if (machine.getSlotDefinition().getNumUpgradeSlots() > 0 && heldItem.getItem() == ModObject.itemBasicCapacitor.getItem()) {
        int slot = machine.getSlotDefinition().getMinUpgradeSlot();
        ItemStack toInsert = heldItem.copy();
        toInsert.stackSize = 1;
        ItemStack temp = machine.getStackInSlot(slot);
        if (Prep.isInvalid(temp)) {
          machine.setInventorySlotContents(slot, toInsert);
          toInsert = null;
        } else if (temp.getItemDamage() != toInsert.getItemDamage()) {
          machine.setInventorySlotContents(slot, toInsert);
          toInsert = temp;
        } else {
          return super.onBlockActivated(world, pos, state, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ);
        }
        
        heldItem.stackSize--;
        if (heldItem.stackSize == 0) {
          entityPlayer.setHeldItem(hand, null);
        }
        
        if (toInsert != null) {
          if (!entityPlayer.inventory.addItemStackToInventory(toInsert)) {
            entityPlayer.dropItem(toInsert, true);
          }
        }
        
        return true;
      }
    }
    
    return super.onBlockActivated(world, pos, state, entityPlayer, hand, heldItem, side, hitX, hitY, hitZ);
  }
  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(IBlockState bs, World world, BlockPos pos, Random rand) {
    // If active, randomly throw some smoke around
    if (world != null && pos != null && rand != null) {
      int x = pos.getX();
      int y = pos.getY();
      int z = pos.getZ();
      if (isActive(world, pos)) {
        float startX = x + 1.0F;
        float startY = y + 1.0F;
        float startZ = z + 1.0F;
        for (int i = 0; i < 4; i++) {
          float xOffset = -0.2F - rand.nextFloat() * 0.6F;
          float yOffset = -0.1F + rand.nextFloat() * 0.2F;
          float zOffset = -0.2F - rand.nextFloat() * 0.6F;
          world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
        }
      }
    }
  }

  protected abstract GuiID getGuiId();

  protected boolean isActive(@Nonnull IBlockAccess blockAccess, @Nonnull BlockPos pos) {
    AbstractMachineEntity te = getTileEntitySafe(blockAccess, pos);
    if (te != null) {
      return te.isActive();
    }
    return false;
  }
  
  @Deprecated
  protected boolean isActive(@Nonnull IBlockAccess blockAccess, int x, int y, int z) {
    return isActive(blockAccess, new BlockPos(x,y,z));
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.ALL_BITS;
  }

  @SideOnly(Side.CLIENT)
  public IOMode.EnumIOMode mapIOMode(IoMode mode, EnumFacing side) {
    switch (mode) {
    case NONE:
      return IOMode.EnumIOMode.NONE;
    case PULL:
      return IOMode.EnumIOMode.PULL;
    case PUSH:
      return IOMode.EnumIOMode.PUSH;
    case PUSH_PULL:
      return IOMode.EnumIOMode.PUSHPULL;
    case DISABLED:
      return IOMode.EnumIOMode.DISABLED;
    }
    throw new RuntimeException("Hey, leave our enums alone!");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper.IItemRenderMapper getItemRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  @SideOnly(Side.CLIENT)
  public IRenderMapper.IBlockRenderMapper getBlockRenderMapper() {
    return RenderMappers.BODY_MAPPER;
  }

  // ///////////////////////////////////////////////////////////////////////
  // PAINT START
  // ///////////////////////////////////////////////////////////////////////

  public @Nonnull IBlockState getFacade(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nullable EnumFacing side) {
    NullHelper.notnull(world, "Hey, how should I get you a block state without a world or position?");
    NullHelper.notnull(pos, "Hey, how should I get you a block state without a world or position?");
    IBlockState paintSource = getPaintSource(getDefaultState(), world, pos);
    return paintSource != null ? paintSource : NullHelper.notnullM(world.getBlockState(pos), "world.getBlockState(pos)");
  }

  public void setPaintSource(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable && world != null && pos != null) {
      T te = getTileEntity(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        ((IPaintable.IPaintableTileEntity) te).setPaintSource(paintSource);
      }
    }
  }

  public void setPaintSource(Block block, ItemStack stack, @Nullable IBlockState paintSource) {
    if (this instanceof IPaintable) {
      PainterUtil2.setSourceBlock(stack, paintSource);
    }
  }

  public @Nullable IBlockState getPaintSource(@Nullable IBlockState state, IBlockAccess world, BlockPos pos) {
    if (this instanceof IPaintable && world != null && pos != null) {
      T te = getTileEntitySafe(world, pos);
      if (te instanceof IPaintable.IPaintableTileEntity) {
        return ((IPaintable.IPaintableTileEntity) te).getPaintSource();
      }
    }
    return null;
  }

  public @Nullable IBlockState getPaintSource(Block block, ItemStack stack) {
    if (this instanceof IPaintable) {
      return PainterUtil2.getSourceBlock(stack);
    }
    return null;
  }

  @Override
  public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
    return this instanceof IPaintable ? true : super.canRenderInLayer(state, layer);
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

}

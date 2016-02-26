package crazypants.enderio.machine;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.render.BlockStateWrapper;
import crazypants.enderio.render.EnumRenderMode;
import crazypants.enderio.render.EnumRenderPart;
import crazypants.enderio.render.IOMode;
import crazypants.enderio.render.IRenderMapper;
import crazypants.enderio.render.ISmartRenderAwareBlock;
import crazypants.enderio.render.SmartModelAttacher;
import crazypants.enderio.waila.IWailaInfoProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockEio<T> implements IGuiHandler, IResourceTooltipProvider,
    IWailaInfoProvider, ISmartRenderAwareBlock {

  @SideOnly(Side.CLIENT)
  private static final MachineRenderMapper MACHINE_RENDER_MAPPER = new MachineRenderMapper(EnumRenderPart.BODY);

  @SideOnly(Side.CLIENT)
  public static TextureAtlasSprite selectedFaceIcon;

  protected final Random random;

  protected final ModObject modObject;

  static {
    PacketHandler.INSTANCE.registerMessage(PacketIoMode.class, PacketIoMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketItemBuffer.class, PacketItemBuffer.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerStorage.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
  }

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass, Material mat) {
    super(mo.unlocalisedName, teClass, mat);
    modObject = mo;
    setHardness(2.0F);
    setStepSound(soundTypeMetal);
    setHarvestLevel("pickaxe", 0);
    random = new Random();    
    setDefaultState(this.blockState.getBaseState().withProperty(EnumRenderMode.RENDER, EnumRenderMode.AUTO));
  }

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass) {
    this(mo, teClass, new Material(MapColor.ironColor));
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
    MinecraftForge.EVENT_BUS.register(this); // TODO
    registerInSmartModelAttacher();
  }
  
  protected void registerInSmartModelAttacher() {
    SmartModelAttacher.register(this);
  }
  
  @Override
  protected BlockState createBlockState() {
    return new BlockState(this, new IProperty[] { EnumRenderMode.RENDER });
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
  public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
    return new BlockStateWrapper(state, world, pos);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public EnumWorldBlockLayer getBlockLayer() {
    return EnumWorldBlockLayer.CUTOUT;
  }

  @SideOnly(Side.CLIENT)
  @SubscribeEvent
  public void onIconLoad(TextureStitchEvent.Pre event) {    
    selectedFaceIcon = event.map.registerSprite(new ResourceLocation(EnderIO.MODID, "blocks/overlays/selectedFace"));              
  }

  @Override
  protected boolean openGui(World world, BlockPos pos, EntityPlayer entityPlayer, EnumFacing side) {   
    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, getGuiId(), world, pos.getX(), pos.getY(), pos.getZ());
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
    int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
    AbstractMachineEntity te = (AbstractMachineEntity) world.getTileEntity(pos);
    te.setFacing(getFacingForHeading(heading));
    te.readFromItemStack(stack);
    if(world.isRemote) {
      return;
    }
    world.markBlockForUpdate(pos);
  }

  protected EnumFacing getFacingForHeading(int heading) {
    switch (heading) {
    case 0:
      return EnumFacing.NORTH;
    case 1:
      return EnumFacing.EAST;
    case 2:
      return EnumFacing.SOUTH;
    case 3:
    default:
      return EnumFacing.WEST;
    }
  }

  @Override
  public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
    super.onBlockAdded(world, pos,state);
    world.markBlockForUpdate(pos);
  }
  
  
  
  @Override
  public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {   
    TileEntity ent = world.getTileEntity(pos);
    if(ent instanceof AbstractMachineEntity) {
      AbstractMachineEntity te = (AbstractMachineEntity) ent;
      te.onNeighborBlockChange(neighborBlock);
    }
  }

  
  
  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {  
    // If active, randomly throw some smoke around
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    if(isActive(world, x,y,z)) {
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

  protected abstract int getGuiId();

  protected boolean isActive(IBlockAccess blockAccess, BlockPos pos) {
    TileEntity te = blockAccess.getTileEntity(pos);
    if(te instanceof AbstractMachineEntity) {
      return ((AbstractMachineEntity) te).isActive();
    }
    return false;
  }
  
  protected boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {    
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
  public IOMode.EnumIOMode mapIOMode(IoMode mode) {
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
  public IRenderMapper getRenderMapper(IBlockState state, IBlockAccess world, BlockPos pos) {
    return MACHINE_RENDER_MAPPER;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IRenderMapper getRenderMapper(ItemStack stack) {
    return MACHINE_RENDER_MAPPER;
  }

}

package crazypants.enderio.machine;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.waila.IWailaInfoProvider;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
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
    IWailaInfoProvider {

//  public IIcon overlayIconPull;
//  public IIcon overlayIconPush;
//  public IIcon overlayIconPushPull;
//  public IIcon overlayIconDisabled;
//  public IIcon overlayIconNone;
//  public IIcon overlayIconDirty;

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
  }

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass) {
    this(mo, teClass, new Material(MapColor.ironColor));
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
    MinecraftForge.EVENT_BUS.register(this);
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

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iIconRegister) {
//
//    iconBuffer = new IIcon[2][12];
//    String side = getSideIconKey(false);
//    // first the 6 sides in OFF state
//    iconBuffer[0][0] = iIconRegister.registerIcon(getBottomIconKey(false));
//    iconBuffer[0][1] = iIconRegister.registerIcon(getTopIconKey(false));
//    iconBuffer[0][2] = iIconRegister.registerIcon(getBackIconKey(false));
//    iconBuffer[0][3] = iIconRegister.registerIcon(getMachineFrontIconKey(false));
//    iconBuffer[0][4] = iIconRegister.registerIcon(side);
//    iconBuffer[0][5] = iIconRegister.registerIcon(side);
//
//    side = getSideIconKey(true);
//    iconBuffer[0][6] = iIconRegister.registerIcon(getBottomIconKey(true));
//    iconBuffer[0][7] = iIconRegister.registerIcon(getTopIconKey(true));
//    iconBuffer[0][8] = iIconRegister.registerIcon(getBackIconKey(true));
//    iconBuffer[0][9] = iIconRegister.registerIcon(getMachineFrontIconKey(true));
//    iconBuffer[0][10] = iIconRegister.registerIcon(side);
//    iconBuffer[0][11] = iIconRegister.registerIcon(side);
//    
//    iconBuffer[1][0] = iIconRegister.registerIcon(getModelIconKey(false));
//    iconBuffer[1][1] = iIconRegister.registerIcon(getModelIconKey(true));
//
//    registerOverlayIcons(iIconRegister);
//
//  }
//
//  @SideOnly(Side.CLIENT)
//  protected void registerOverlayIcons(IIconRegister iIconRegister) {
//    overlayIconPull = iIconRegister.registerIcon("enderio:overlays/pull");
//    overlayIconPush = iIconRegister.registerIcon("enderio:overlays/push");
//    overlayIconPushPull = iIconRegister.registerIcon("enderio:overlays/pushPull");
//    overlayIconDisabled = iIconRegister.registerIcon("enderio:overlays/disabled");
//    overlayIconNone = iIconRegister.registerIcon("enderio:overlays/none");
//    selectedFaceIcon = iIconRegister.registerIcon("enderio:overlays/selectedFace");
//    overlayIconDirty = iIconRegister.registerIcon("enderio:overlays/dirt");
//  }

//  @SideOnly(Side.CLIENT)
//  public IIcon getOverlayIconForMode(T tile, ForgeDirection face, IoMode mode) {
//    if(mode == null) {
//      return null;
//    }
//    switch (mode) {
//    case DISABLED:
//      return overlayIconDisabled;
//    case PULL:
//      return overlayIconPull;
//    case PUSH:
//      return overlayIconPush;
//    case PUSH_PULL:
//      return overlayIconPushPull;
//    default:
//      return tile.isDirty ? overlayIconDirty : null;
//    }
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {
//
//    // used to render the block in the world
//    TileEntity te = world.getTileEntity(x, y, z);
//    int facing = 3;
//    if(te instanceof AbstractMachineEntity) {
//      AbstractMachineEntity me = (AbstractMachineEntity) te;
//      facing = me.facing;
//    }
//    if(isActive(world, x, y, z)) {
//      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6];
//    } else {
//      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
//    }
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int blockSide, int blockMeta) {
//    // This is used to render the block as an item
//    return iconBuffer[0][blockSide];
//  }
//
//  public IIcon getModelIcon(IBlockAccess world, int x, int y, int z) {
//    return getModelIcon(((AbstractMachineEntity) world.getTileEntity(x, y, z)).isActive());
//  }
//
//  public IIcon getModelIcon() {
//    return getModelIcon(false);
//  }
//
//  private IIcon getModelIcon(boolean active) {
//    return active ? iconBuffer[1][1] : iconBuffer[1][0];
//  }

 
  
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

  protected abstract String getMachineFrontIconKey(boolean active);

  protected String getSideIconKey(boolean active) {
    return "enderio:machineSide";
  }

  protected String getBackIconKey(boolean active) {
    return "enderio:machineBack";
  }

  protected String getTopIconKey(boolean active) {
    return "enderio:machineTop";
  }

  protected String getBottomIconKey(boolean active) {
    return "enderio:machineBottom";
  }

  protected String getModelIconKey(boolean active) {
    return getSideIconKey(active);
  }

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
}

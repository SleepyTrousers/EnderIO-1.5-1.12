package crazypants.enderio.machine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.gui.IResourceTooltipProvider;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;
import crazypants.enderio.waila.IWailaInfoProvider;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockContainer implements IGuiHandler, IResourceTooltipProvider,
    IWailaInfoProvider {

  public static int renderId;

  public IIcon overlayIconPull;
  public IIcon overlayIconPush;
  public IIcon overlayIconPushPull;
  public IIcon overlayIconDisabled;
  public IIcon overlayIconNone;

  public IIcon selectedFaceIcon;

  @SideOnly(Side.CLIENT)
  protected IIcon[][] iconBuffer;

  protected final Random random;

  protected final ModObject modObject;

  protected final Class<T> teClass;

  static {
    PacketHandler.INSTANCE.registerMessage(PacketIoMode.class, PacketIoMode.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketPowerStorage.class, PacketPowerStorage.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketCurrentTaskProgress.class,PacketCurrentTaskProgress.class,PacketHandler.nextID(),Side.CLIENT);
  }

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass, Material mat) {
    super(mat);
    modObject = mo;
    this.teClass = teClass;
    setHardness(2.0F);
    setStepSound(soundTypeMetal);
    setBlockName(mo.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
    random = new Random();
  }

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass) {
    this(mo, teClass, new Material(MapColor.ironColor));
  }

  protected void init() {
    GameRegistry.registerBlock(this, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
  }

  @Override
  public TileEntity createNewTileEntity(World var1, int var2) {
    try {
      return teClass.newInstance();
    } catch (Exception e) {
      FMLCommonHandler.instance().raiseException(e, "Could not create tile entity from class " + teClass, true);
      return null;
    }
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ToolUtil.breakBlockWithTool(this, world, x, y, z, entityPlayer)) {
      return true;
    }

    ITool tool = ToolUtil.getEquippedTool(entityPlayer);
    if(tool != null && !entityPlayer.isSneaking()) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof AbstractMachineEntity) {
        ((AbstractMachineEntity) te).toggleIoModeForFace(ForgeDirection.getOrientation(side));
        world.markBlockForUpdate(x, y, z);
        return true;
      }
    }

    if(entityPlayer.isSneaking()) {
      return false;
    }
    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, getGuiId(), world, x, y, z);
    }
    return true;
  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
    return false;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister iIconRegister) {

    iconBuffer = new IIcon[1][12];
    String side = getSideIconKey(false);
    // first the 6 sides in OFF state
    iconBuffer[0][0] = iIconRegister.registerIcon(side);
    iconBuffer[0][1] = iIconRegister.registerIcon(getTopIconKey(false));
    iconBuffer[0][2] = iIconRegister.registerIcon(getBackIconKey(false));
    iconBuffer[0][3] = iIconRegister.registerIcon(getMachineFrontIconKey(false));
    iconBuffer[0][4] = iIconRegister.registerIcon(side);
    iconBuffer[0][5] = iIconRegister.registerIcon(side);

    side = getSideIconKey(true);
    iconBuffer[0][6] = iIconRegister.registerIcon(side);
    iconBuffer[0][7] = iIconRegister.registerIcon(getTopIconKey(true));
    iconBuffer[0][8] = iIconRegister.registerIcon(getBackIconKey(true));
    iconBuffer[0][9] = iIconRegister.registerIcon(getMachineFrontIconKey(true));
    iconBuffer[0][10] = iIconRegister.registerIcon(side);
    iconBuffer[0][11] = iIconRegister.registerIcon(side);

    registerOverlayIcons(iIconRegister);

  }

  @SideOnly(Side.CLIENT)
  protected void registerOverlayIcons(IIconRegister iIconRegister) {
    overlayIconPull = iIconRegister.registerIcon("enderio:machineOverlayPull");
    overlayIconPush = iIconRegister.registerIcon("enderio:machineOverlayPush");
    overlayIconPushPull = iIconRegister.registerIcon("enderio:machineOverlayPushPull");
    overlayIconDisabled = iIconRegister.registerIcon("enderio:machineOverlayDisabled");
    overlayIconNone = iIconRegister.registerIcon("enderio:machineOverlayNone");
    selectedFaceIcon = iIconRegister.registerIcon("enderio:machineOverlaySelectedFace");
  }

  @SideOnly(Side.CLIENT)
  public IIcon getOverlayIconForMode(IoMode mode) {
    if(mode == null) {
      return null;
    }
    switch (mode) {
    case DISABLED:
      return overlayIconDisabled;
    case PULL:
      return overlayIconPull;
    case PUSH:
      return overlayIconPush;
    case PUSH_PULL:
      return overlayIconPushPull;
    default:
      return null;
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {

    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);
    int facing = 0;
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      facing = me.facing;
    }
    if(isActive(world, x, y, z)) {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6];
    } else {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    // This is used to render the block as an item
    return iconBuffer[0][blockSide];
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof AbstractMachineEntity) {
        AbstractMachineEntity machineEntity = (AbstractMachineEntity) te;
        ItemStack itemStack = new ItemStack(this);
        machineEntity.writeToItemStack(itemStack);
        ret.add(itemStack);
      }
    }
    return ret;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    if(!world.isRemote && (!player.capabilities.isCreativeMode)) {
      TileEntity te = world.getTileEntity(x, y, z);
      if(te instanceof AbstractMachineEntity) {
        AbstractMachineEntity machineEntity = (AbstractMachineEntity) te;
        int meta = damageDropped(world.getBlockMetadata(x, y, z));
        ItemStack itemStack = new ItemStack(this, 1, meta);
        machineEntity.writeToItemStack(itemStack);

        float f = 0.7F;
        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);

        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);
      }
    }
    return super.removedByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, x, y, z, player, stack);
    int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
    AbstractMachineEntity te = (AbstractMachineEntity) world.getTileEntity(x, y, z);
    te.setFacing(getFacingForHeading(heading));
    te.readFromItemStack(stack);
    if(world.isRemote) {
      return;
    }
    world.markBlockForUpdate(x, y, z);
  }

  protected short getFacingForHeading(int heading) {
    switch (heading) {
    case 0:
      return 2;
    case 1:
      return 5;
    case 2:
      return 3;
    case 3:
    default:
      return 4;
    }
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    super.onBlockAdded(world, x, y, z);
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
    TileEntity ent = world.getTileEntity(x, y, z);
    if(ent instanceof AbstractMachineEntity) {
      AbstractMachineEntity te = (AbstractMachineEntity) ent;
      te.onNeighborBlockChange(blockId);
    }
  }

  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
    // If active, randomly throw some smoke around
    if(isActive(world, x, y, z)) {
      float startX = x + 1.0F;
      float startY = y + 1.0F;
      float startZ = z + 1.0F;
      for (int i = 0; i < 4; i++) {
        float xOffset = -0.2F - rand.nextFloat() * 0.6F;
        float yOffset = -0.1F + rand.nextFloat() * 0.2F;
        float zOffset = -0.2F - rand.nextFloat() * 0.6F;
        world.spawnParticle("smoke", startX + xOffset, startY + yOffset, startZ + zOffset, 0.0D, 0.0D, 0.0D);
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

  protected boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {
    TileEntity te = blockAccess.getTileEntity(x, y, z);
    if(te instanceof AbstractMachineEntity) {
      return ((AbstractMachineEntity) te).isActive();
    }
    return false;
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

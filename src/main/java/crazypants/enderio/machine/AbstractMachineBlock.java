package crazypants.enderio.machine;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.util.Util;

public abstract class AbstractMachineBlock<T extends AbstractMachineEntity> extends BlockContainer implements IGuiHandler {

  @SideOnly(Side.CLIENT)
  protected IIcon[][] iconBuffer;

  protected final Random random;

  protected final ModObject modObject;

  protected final Class<T> teClass;

  protected AbstractMachineBlock(ModObject mo, Class<T> teClass) {
    super(new Material(MapColor.ironColor));
    modObject = mo;
    this.teClass = teClass;
    setHardness(2.0F);
    setStepSound(soundTypeMetal);
    setBlockName(mo.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
    random = new Random();

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
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removedByPlayer(world, entityPlayer, x, y, z);
          if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
            ((IToolWrench) entityPlayer.getCurrentEquippedItem().getItem()).wrenchUsed(entityPlayer, x, y, z);
          }
          return true;
        }
      }
    }

    if(entityPlayer.isSneaking()) {
      return false;
    }
    entityPlayer.openGui(EnderIO.instance, getGuiId(), world, x, y, z);
    return true;
  }

  @Override
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

  }

  @Override
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
  public IIcon getIcon(int blockSide, int blockMeta) {
    // This is used to render the block as an item
    return iconBuffer[0][blockSide];
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      TileEntity ent = world.getTileEntity(x, y, z);
      if(ent != null) {
        if(teClass.isAssignableFrom(ent.getClass())) {
          @SuppressWarnings("unchecked")
          T te = (T) world.getTileEntity(x, y, z);
          Util.dropItems(world, te, x, y, z, true);
        }
      }
    }
    world.removeTileEntity(x, y, z);

  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    if(!world.isRemote && !player.capabilities.isCreativeMode) {
      ItemStack st = new ItemStack(this);
      Util.dropItems(world, st, x, y, z, false);
    }
    return super.removedByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    super.onBlockPlacedBy(world, x, y, z, player, stack);
    int heading = MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
    AbstractMachineEntity te = (AbstractMachineEntity) world.getTileEntity(x, y, z);
    switch (heading) {
    case 0:
      te.setFacing((short) 2);
      break;
    case 1:
      te.setFacing((short) 5);
      break;
    case 2:
      te.setFacing((short) 3);
      break;
    case 3:
      te.setFacing((short) 4);
      break;
    default:
      break;
    }
    world.markBlockForUpdate(x, y, z);
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

}

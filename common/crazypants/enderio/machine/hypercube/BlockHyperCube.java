package crazypants.enderio.machine.hypercube;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.tools.IToolWrench;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.Util;

public class BlockHyperCube extends Block implements ITileEntityProvider, IGuiHandler {

  static final NumberFormat NF = NumberFormat.getIntegerInstance();

  public static BlockHyperCube create() {
    HyperCubePacketHandler pp = new HyperCubePacketHandler();
    PacketHandler.instance.addPacketProcessor(pp);
    NetworkRegistry.instance().registerConnectionHandler(pp);

    BlockHyperCube result = new BlockHyperCube();
    result.init();
    return result;
  }

  private BlockHyperCube() {
    super(ModObject.blockHyperCube.id, Material.ground);
    setHardness(0.5F);
    setStepSound(Block.soundMetalFootstep);
    setUnlocalizedName("enderio." + ModObject.blockHyperCube.name());
    setCreativeTab(EnderIOTab.tabEnderIO);
    setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockHyperCube.unlocalisedName);
    GameRegistry.registerTileEntity(TileHyperCube.class, ModObject.blockHyperCube.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_HYPER_CUBE, this);
  }

  public Icon getPortalIcon() {
    return blockIcon;
  }

  @Override
  public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
    return true;
  }

  @Override
  public void registerIcons(IconRegister iconRegister) {
    blockIcon = iconRegister.registerIcon("enderio:tesseractPortal");
  }

  @Override
  public int getRenderType() {
    return -1;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public TileEntity createNewTileEntity(World world) {
    return null;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileHyperCube();
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    return 8;
  }

  @Override
  public void onBlockAdded(World world, int x, int y, int z) {
    if(world.isRemote) {
      return;
    }
    TileHyperCube tr = (TileHyperCube) world.getBlockTileEntity(x, y, z);
    tr.onBlockAdded();
  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
    if(world.isRemote) {
      return;
    }
    TileHyperCube te = (TileHyperCube) world.getBlockTileEntity(x, y, z);
    te.onNeighborBlockChange();
  }

  @Override
  public ArrayList<ItemStack> getBlockDropped(World world, int x, int y, int z, int metadata, int fortune) {
    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    if(!world.isRemote) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileHyperCube) {
        TileHyperCube hc = (TileHyperCube) te;
        hc.onBreakBlock();
        ItemStack itemStack = new ItemStack(this);
        PowerHandlerUtil.setStoredEnergyForItem(itemStack, hc.getInternalPowerHandler().getEnergyStored());
        setChannelOnItem(hc, itemStack);
        setIoOnItem(hc, itemStack);
        ret.add(itemStack);

        ItemRecieveBuffer rb = hc.getRecieveBuffer();
        for (int i = 0; i < rb.getSizeInventory(); i++) {
          ItemStack stack = rb.getStackInSlot(i);
          if(stack != null) {
            ret.add(stack);
          }
        }
      }
    }
    return ret;
  }

  private void setChannelOnItem(TileHyperCube hc, ItemStack itemStack) {
    Channel chan = hc.getChannel();
    if(chan != null) {
      NBTTagCompound tag = itemStack.getTagCompound();
      if(tag == null) {
        tag = new NBTTagCompound();
        itemStack.setTagCompound(tag);
      }
      tag.setString("channelName", chan.name);
      tag.setBoolean("channelIsPublic", chan.isPublic());
      if(!chan.isPublic()) {
        tag.setString("channelUser", chan.user);
      }
    }
  }

  private void setIoOnItem(TileHyperCube hc, ItemStack itemStack) {
    System.out.println("BlockHyperCube.setIoOnItem: ");
    NBTTagCompound tag = itemStack.getTagCompound();
    if(tag == null) {
      tag = new NBTTagCompound();
      itemStack.setTagCompound(tag);
    }
    for (SubChannel sc : SubChannel.values()) {
      tag.setShort("sendRecieve" + sc.ordinal(), (short) hc.getModeForChannel(sc).ordinal());
      System.out.println("BlockHyperCube.setIoOnItem: sendRecieve" + sc.ordinal());
    }
  }

  private void setIoOnTransciever(TileHyperCube hc, ItemStack itemStack) {
    System.out.println("BlockHyperCube.setIoOnTransciever: ");
    NBTTagCompound tag = itemStack.getTagCompound();
    if(tag == null) {
      return;
    }
    for (SubChannel sc : SubChannel.values()) {
      if(tag.hasKey("sendRecieve" + sc.ordinal())) {
        hc.setModeForChannel(sc, IoMode.values()[tag.getShort("sendRecieve" + sc.ordinal())]);
      }
    }
  }

  private Channel getChannelFromItem(ItemStack itemStack) {
    NBTTagCompound tag = itemStack.getTagCompound();
    if(tag == null) {
      return null;
    }

    String channelName = tag.getString("channelName");
    if(channelName == null || channelName.trim().isEmpty()) {
      return null;
    }

    String user = null;
    if(!tag.getBoolean("channelIsPublic")) {
      user = tag.getString("channelUser");
    }
    return new Channel(channelName, user);

  }

  @Override
  public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    if(!world.isRemote) {
      TileEntity te = world.getBlockTileEntity(x, y, z);
      if(te instanceof TileHyperCube) {
        TileHyperCube hc = (TileHyperCube) te;
        hc.onBreakBlock();
        ItemStack itemStack = new ItemStack(this);
        PowerHandlerUtil.setStoredEnergyForItem(itemStack, hc.getInternalPowerHandler().getEnergyStored());
        setChannelOnItem(hc, itemStack);
        setIoOnItem(hc, itemStack);
        float f = 0.7F;
        double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
        EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
        entityitem.delayBeforeCanPickup = 10;
        world.spawnEntityInWorld(entityitem);

        ItemRecieveBuffer rb = hc.getRecieveBuffer();
        Util.dropItems(world, rb, x, y, z, true);

      }
    }
    return super.removeBlockByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileHyperCube) {
      TileHyperCube cb = (TileHyperCube) te;
      cb.getInternalPowerHandler().setEnergy(PowerHandlerUtil.getStoredEnergyForItem(stack));
      if(player instanceof EntityPlayerMP) {
        cb.setOwner(((EntityPlayerMP) player).username);
      }
      cb.setChannel(getChannelFromItem(stack));
      setIoOnTransciever(cb, stack);
    }
    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public int idDropped(int par1, Random par2Random, int par3) {
    return 0;
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    if(ConduitUtil.isToolEquipped(entityPlayer) && entityPlayer.isSneaking()) {
      if(entityPlayer.getCurrentEquippedItem().getItem() instanceof IToolWrench) {
        IToolWrench wrench = (IToolWrench) entityPlayer.getCurrentEquippedItem().getItem();
        if(wrench.canWrench(entityPlayer, x, y, z)) {
          removeBlockByPlayer(world, entityPlayer, x, y, z);
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
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof TileHyperCube)) {
      return false;
    }
    entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_HYPER_CUBE, world, x, y, z);
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(te instanceof TileHyperCube) {
      TileHyperCube hc = (TileHyperCube) te;
      return new GuiHyperCube(hc);
    }
    return null;
  }

}

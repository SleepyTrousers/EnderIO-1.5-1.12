package crazypants.enderio.rail;

import java.util.List;
import java.util.UUID;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.ConduitUtil;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.util.MetadataUtil;
import crazypants.util.RoundRobinIterator;

public class BlockEnderRail extends BlockRail {

  public static boolean isReverse(int meta) {
    return MetadataUtil.isBitSet(3, meta);
  }

  public static ForgeDirection getDirection(int meta) {
    ForgeDirection result;
    if(isEastWest(meta)) {
      result = ForgeDirection.EAST;
    } else {
      result = ForgeDirection.NORTH;
    }
    if(isReverse(meta)) {
      result = result.getOpposite();
    }
    return result;
  }

  private static boolean isEastWest(int meta) {
    return MetadataUtil.isBitSet(0, meta);
  }

  public static BlockEnderRail create() {
    BlockEnderRail res = new BlockEnderRail();
    res.init();
    return res;
  }

  private IIcon iconEastWest;
  private IIcon iconEastWestTurned;

  private int linkId;

  protected BlockEnderRail() {
    setBlockName(ModObject.blockEnderRail.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    if(Config.enderRailEnabled) {
      setCreativeTab(EnderIOTab.tabEnderIO);
    }
    setBlockTextureName("enderio:blockEnderRail");
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockEnderRail.unlocalisedName);
    GameRegistry.registerTileEntity(TileEnderRail.class, ModObject.blockEnderRail.unlocalisedName + "TileEntity");
  }

  @Override
  public boolean hasTileEntity(int metadata) {
    return true;
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileEnderRail();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerBlockIcons(IIconRegister register) {
    super.registerBlockIcons(register);
    iconEastWest = register.registerIcon("enderio:blockEnderRailEastWest");
    iconEastWestTurned = register.registerIcon("enderio:blockEnderRailEastWest_turned");
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    if(!isEastWest(meta)) {
      return super.getIcon(side, meta);
    } else if(isReverse(meta)) {
      return iconEastWestTurned;
    }
    return iconEastWest;
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
    //    //TODO: Don't let people decide which way the carts come out. Can't figure out how to reliably reverse directions
    //    if(ConduitUtil.isToolEquipped(player)) {
    //      if(!world.isRemote) {
    //        int meta = world.getBlockMetadata(x, y, z);
    //        meta = MetadataUtil.setBit(3, !MetadataUtil.isBitSet(3, meta), meta);
    //        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
    //      }
    //      return true;
    //    }
    return false;
  }

  public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
    //Ignore turning bit, used for receive direction
    return world.getBlockMetadata(x, y, z) & 7;
  }

  @Override
  public boolean canPlaceBlockAt(World world, int x, int y, int z) {
    return world.getBlock(x, y - 1, z) == EnderIO.blockTransceiver;
  }

  @Override
  public boolean canBlockStay(World world, int x, int y, int z) {
    return canPlaceBlockAt(world, x, y, z);
  }

  public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
    if(world.isRemote) {
      return;
    }

    int origMeta = world.getBlockMetadata(x, y, z);
    int newMeta = origMeta;
    if(field_150053_a) {
      newMeta = origMeta & 7;
    }

    if(!canBlockStay(world, x, y, z)) {
      dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
      world.setBlockToAir(x, y, z);
    } else {
      func_150048_a(world, x, y, z, origMeta, newMeta, block);
    }

  }

  public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileEnderRail)) {
      return;
    }
    TileEnderRail ter = (TileEnderRail) te;
    if(ter.isRecievedCart(cart)) {
      return;
    }

    tryTeleport(world, cart, x, y, z);
  }

  private void tryTeleport(World world, EntityMinecart cart, int x, int y, int z) {

    TileEntity te = world.getTileEntity(x, y - 1, z);
    if(!(te instanceof TileTransceiver)) {
      return;
    }
    TileTransceiver sender = (TileTransceiver) te;
    if(!sender.isRedstoneChecksPassed()) {
      return;
    }
    if(!sender.hasPower()) {
      return;
    }
    List<Channel> sendChannels = sender.getSendChannels(ChannelType.RAIL);
    for (Channel channel : sendChannels) {
      RoundRobinIterator<TileTransceiver> iter = ServerChannelRegister.instance.getIterator(channel);
      for (TileTransceiver reciever : iter) {
        if(isValidDestination(sender, channel, reciever)) {
          int requiredPower = getPowerRequired(cart, sender, reciever);
          if(sender.getEnergyStored() >= requiredPower) {
            sender.usePower(requiredPower);
            teleportCart(world, cart, sender, reciever);
            
            return;
          }
        }
      }
    }
  }

  private boolean isValidDestination(TileTransceiver sender, Channel channel, TileTransceiver reciever) {
    if(reciever == sender) {
      return false;
    }
    if(!reciever.getRecieveChannels(ChannelType.RAIL).contains(channel)) {
      return false;
    }
    if(!reciever.isRedstoneChecksPassed() || !sender.isRedstoneChecksPassed()) {
      return false;
    }
    if(!reciever.hasPower()) {
      return false;
    }
    TileEntity te = reciever.getWorldObj().getTileEntity(reciever.xCoord, reciever.yCoord + 1, reciever.zCoord);
    if(!(te instanceof TileEnderRail)) {
      return false;
    }
    TileEnderRail railTE = (TileEnderRail) te;
    return railTE.isClear();
  }

  private int getPowerRequired(EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
    int powerPerCart = getPowerRequiredForSingleCart(sender, reciever);
    //    ILinkageManager linkMan = CartTools.getLinkageManager(sender.getWorldObj());
    //    if(linkMan == null) {
    return powerPerCart;
    //    }
    //    int numInLink = linkMan.countCartsInTrain(cart);
    //    if(numInLink <= 1) {
    //      return powerPerCart;
    //    }
    //    return numInLink * powerPerCart;           
  }

  private int getPowerRequiredForSingleCart(TileTransceiver sender, TileTransceiver reciever) {
    int powerRequired;
    if(sender.getWorldObj().provider.dimensionId != reciever.getWorldObj().provider.dimensionId) {
      powerRequired = Config.enderRailPowerRequireCrossDimensions;
    } else {
      powerRequired = Config.enderRailPowerRequiredBase;
      powerRequired += sender.getLocation().distance(reciever.getLocation()) * Config.enderRailPowerRequiredPerBlock;
    }
    return powerRequired;
  }

  private void teleportCart(World world, EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
    updateCartLinks(world, cart);
    teleportSingleCart(world, cart, sender, reciever);
  }

  private void teleportSingleCart(World world, EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {

    int toDimension = reciever.getWorldObj().provider.dimensionId;
    int toX = reciever.xCoord;
    int toY = reciever.yCoord + 1;
    int toZ = reciever.zCoord;

    
    TileEntity te = sender.getWorldObj().getTileEntity(sender.xCoord, sender.yCoord +1 , sender.zCoord);
    if(!(te instanceof TileEnderRail)) {
      return;
    }
    TileEnderRail senderRail = (TileEnderRail) te;
    
    te = reciever.getWorldObj().getTileEntity(toX, toY, toZ);
    if(!(te instanceof TileEnderRail)) {
      return;
    }
    TileEnderRail destinationRail = (TileEnderRail) te;
    
    

    MinecraftServer minecraftserver = MinecraftServer.getServer();
    int j = cart.dimension;
    WorldServer worldserver = minecraftserver.worldServerForDimension(j);
    WorldServer worldserver1 = minecraftserver.worldServerForDimension(toDimension);
    cart.dimension = toDimension;

    if(j == 1 && toDimension == 1) {
      worldserver1 = minecraftserver.worldServerForDimension(0);
      cart.dimension = 0;
    }

    Entity passenger = cart.riddenByEntity;
    if(passenger != null) {
      worldserver.removeEntity(passenger);
      passenger.isDead = true;
    }

    ItemStack[] stacks = null;
    if(cart instanceof IInventory) {
      IInventory cont = (IInventory) cart;
      stacks = new ItemStack[cont.getSizeInventory()];
      for (int i = 0; i < stacks.length; i++) {
        stacks[i] = cont.getStackInSlot(i);
        cont.setInventorySlotContents(i, null);
      }
    }

    worldserver.removeEntity(cart);
    cart.isDead = false;

    EntityMinecart newCart = (EntityMinecart) EntityList.createEntityByName(EntityList.getEntityString(cart), worldserver1);
    if(newCart != null) {

      NBTTagCompound nbttagcompound = new NBTTagCompound();
      cart.writeToNBT(nbttagcompound);
      newCart.readFromNBT(nbttagcompound);

      newCart.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);

      if(stacks != null && newCart instanceof IInventory) {
        IInventory cont = (IInventory) newCart;
        for (int i = 0; i < stacks.length; i++) {
          cont.setInventorySlotContents(i, stacks[i]);
        }
      }

      if(passenger != null) {
        Entity newPas = EntityList.createEntityByName(EntityList.getEntityString(passenger), worldserver1);
        newPas.copyDataFrom(passenger, true);
        newPas.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
        newCart.riddenByEntity = newPas;
        newPas.ridingEntity = newCart;
        worldserver1.spawnEntityInWorld(newPas);
      }
      
      worldserver1.spawnEntityInWorld(newCart);
      
      destinationRail.onCartRecieved((EntityMinecart) newCart);
      senderRail.onCartSent(cart);
      

    }
    cart.isDead = true;
  }

  public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
    return false;
  }

  //------------ Link Utils

  public static void recreateLink(EntityMinecart existingCart, EntityMinecart newCart) {
    if(existingCart == null || newCart == null) {
      return;
    }
    ILinkageManager linkMan = CartTools.getLinkageManager(existingCart.worldObj);
    if(linkMan == null) {
      return;
    }
    UUID linkA = getLinkA(newCart);
    if(linkA != null && linkA.equals(existingCart.getPersistentID())) {
      if(!linkMan.areLinked(existingCart, newCart)) {
        boolean res = linkMan.createLink(existingCart, newCart);
        System.out.println("BlockEnderRail.recreateLink: A " + res);
      }
      return;
    }
    UUID linkB = getLinkB(newCart);
    if(linkB != null && linkB.equals(existingCart.getPersistentID())) {
      if(!linkMan.areLinked(existingCart, newCart)) {
        boolean res = linkMan.createLink(existingCart, newCart);
        System.out.println("BlockEnderRail.recreateLink: B " + res);
      }
      return;
    }
  }

  public static void updateCartLinks(World world, EntityMinecart cart) {
    ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
    if(linkMan == null || linkMan.countCartsInTrain(cart) <= 1) {
      return;
    }
    Iterable<EntityMinecart> allCarts = linkMan.getCartsInTrain(cart);
    for (EntityMinecart aCart : allCarts) {
      if(aCart != null) {
        updateLink("a", aCart, linkMan.getLinkedCartA(aCart));
        updateLink("b", aCart, linkMan.getLinkedCartB(aCart));
      }
    }
  }

  private static void updateLink(String prefix, EntityMinecart cart, EntityMinecart linkedCart) {
    NBTTagCompound data = cart.getEntityData();
    long lastUpdateTime = -1;
    String timeKey = prefix + "UpdateTime";
    if(data.hasKey(timeKey)) {
      lastUpdateTime = data.getLong(timeKey);
    }
    long curTime = cart.worldObj.getTotalWorldTime();
    if(lastUpdateTime > 0 && curTime - lastUpdateTime < 100) {
      return;
    }
    data.setLong(timeKey, curTime);
    data.setString(prefix + "Link", linkedCart == null ? "null" : linkedCart.getPersistentID().toString());
  }

  public static UUID getLinkA(EntityMinecart cart) {
    return getLink("a", cart);
  }

  public static UUID getLinkB(EntityMinecart cart) {
    return getLink("b", cart);
  }

  private static UUID getLink(String prefix, EntityMinecart cart) {
    NBTTagCompound data = cart.getEntityData();
    String uuidStr = data.getString(prefix + "Link");
    if(uuidStr == null || uuidStr.trim().isEmpty() || "null".equals(uuidStr)) {
      return null;
    }
    return UUID.fromString(uuidStr);
  }

}

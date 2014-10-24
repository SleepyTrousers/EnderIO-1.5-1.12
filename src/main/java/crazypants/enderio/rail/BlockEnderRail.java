package crazypants.enderio.rail;

import java.util.ArrayList;
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
import crazypants.enderio.Log;
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
            if(teleportCart(world, cart, sender, reciever)) {
              sender.usePower(requiredPower);
              return;
            }
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
    int numCarts = TeleportUtil.getNumberOfCartsInTrain(cart);
    return powerPerCart * numCarts;
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

  private boolean teleportCart(World world, EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {

    TileEnderRail destRail = getRailTile(reciever);
    if(destRail == null) {
      return false;
    }

    List<EntityMinecart> allCarts = TeleportUtil.getCartsInTrain(cart);
    if(allCarts.size() > 1) {
      TeleportUtil.updateCartLinks(world, cart);
    }

    List<List<Entity>> toTeleport = new ArrayList<List<Entity>>(allCarts.size());
    List<EntityMinecart> toDespawn = new ArrayList<EntityMinecart>(allCarts.size());
    for (EntityMinecart cartInTrain : allCarts) {
      if(cartInTrain != null) {
        List<Entity> entities = TeleportUtil.createEntitiesForReciever(cartInTrain, sender, reciever);
        if(entities != null) {
          toTeleport.add(entities);
          toDespawn.add(cartInTrain);
        }
      }
    }

    for (EntityMinecart despawnCart : toDespawn) {
      TeleportUtil.despawn(sender.getWorldObj(), despawnCart);
    }

    destRail.onTrainRecieved(toTeleport);
    return true;

  }

  private TileEnderRail getRailTile(TileTransceiver reciever) {
    if(reciever == null || reciever.getWorldObj() == null) {
      return null;
    }
    TileEntity te = reciever.getWorldObj().getTileEntity(reciever.xCoord, reciever.yCoord + 1, reciever.zCoord);
    if(!(te instanceof TileEnderRail)) {
      return null;
    }
    return (TileEnderRail) te;
  }

  public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
    return false;
  }

}

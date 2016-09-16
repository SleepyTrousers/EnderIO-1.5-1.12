package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.MetadataUtil;
import com.enderio.core.common.util.RoundRobinIterator;

import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.tool.ToolUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class BlockEnderRail extends BlockRail implements IResourceTooltipProvider {

  public static boolean isReverse(int meta) {
    return MetadataUtil.isBitSet(3, meta);
  }

  public static EnumFacing getDirection(int meta) {
    EnumFacing result;
    if(isEastWest(meta)) {
      result = EnumFacing.EAST;
    } else {
      result = EnumFacing.SOUTH;
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
    PacketHandler.INSTANCE.registerMessage(PacketTeleportEffects.class, PacketTeleportEffects.class, PacketHandler.nextID(), Side.CLIENT);
    BlockEnderRail res = new BlockEnderRail();
    res.init();

    if(Config.enderRailTeleportPlayers) {
      MinecraftForge.EVENT_BUS.register(PlayerTeleportHandler.instance);
    }
    return res;
  }

//  private IIcon iconEastWest;
//  private IIcon iconEastWestTurned;

  // private int linkId;

  protected BlockEnderRail() {
    setUnlocalizedName(ModObject.blockEnderRail.getUnlocalisedName());
    setRegistryName(ModObject.blockEnderRail.getUnlocalisedName());
    setSoundType(SoundType.METAL);
    if(Config.transceiverEnabled && Config.enderRailEnabled) {
      setCreativeTab(EnderIOTab.tabEnderIO);
    }
//    setBlockTextureName("enderio:blockEnderRail");
    setHardness(0.7F);
  }

  private void init() {
    GameRegistry.register(this);
  }

//  @Override
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister register) {
//    super.registerBlockIcons(register);
//    iconEastWest = register.registerIcon("enderio:blockEnderRailEastWest");
//    iconEastWestTurned = register.registerIcon("enderio:blockEnderRailEastWest_turned");
//  }
//
//  @Override
//  @SideOnly(Side.CLIENT)
//  public IIcon getIcon(int side, int meta) {
//    if(!isEastWest(meta)) {
//      return super.getIcon(side, meta);
//    } else if(isReverse(meta)) {
//      return iconEastWestTurned;
//    }
//    return iconEastWest;
//  }

  
  @Override
  public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem,
      EnumFacing side,
      float hitX, float hitY, float hitZ) {
    if (ToolUtil.isToolEquipped(player, hand)) {
      if(!world.isRemote) {
//        int meta = world.getBlockMetadata(x, y, z);
//        meta = MetadataUtil.setBit(3, !MetadataUtil.isBitSet(3, meta), meta);
//        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
      }
      return true;
    }
    return false;
  }
  
  
  
  @Override
  public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
  return false;
  }
    
  @Override
  public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
    if(!world.isRemote) {
      TileEntity te = world.getTileEntity(pos.down());
      if(te instanceof TileTransceiver) {
        ((TileTransceiver) te).getRailController().dropNonSpawnedCarts();
      }
    }
    return super.removedByPlayer(state, world, pos, player, willHarvest);
  }

//  @Override
//  public int getBasicRailMetadata(IBlockAccess world, EntityMinecart cart, int x, int y, int z) {
//    //Ignore turning bit, used for receive direction
//    return world.getBlockMetadata(x, y, z) & 7;
//  }

  @Override
  public boolean canPlaceBlockAt(World world, BlockPos pos) {
    return world.getBlockState(pos.down()).getBlock() == EnderIO.blockTransceiver;
  }

//  @Override
//  public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
//    return false;
//  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
    return getUnlocalizedName();
  }

  @Override
  public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
    if(world.isRemote) {
      return;
    }

//    int origMeta = world.getBlockMetadata(x, y, z);
//    int newMeta = origMeta;
//    if(field_150053_a) {
//      newMeta = origMeta & 7;
//    }
//
//    if(!canBlockStay(world, x, y, z)) {
//      dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
//      world.setBlockToAir(x, y, z);
//    } else {
//      func_150048_a(world, x, y, z, origMeta, newMeta, block);
//    }

  }
  
  

  @Override
  public void onMinecartPass(World world, EntityMinecart cart, BlockPos pos) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(new BlockPos(pos.down()));
    if(!(te instanceof TileTransceiver)) {
      return;
    }
    TileTransceiver ter = (TileTransceiver) te;
    if(ter.getRailController().isRecievedCart(cart)) {
      return;
    }
    tryTeleport(world, cart, pos.getX(), pos.getY(), pos.getZ());
  }

  private void tryTeleport(World world, EntityMinecart cart, int x, int y, int z) {

    TileEntity te = world.getTileEntity(new BlockPos(x, y - 1, z));
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
    Set<Channel> sendChannels = sender.getSendChannels(ChannelType.RAIL);
    for (Channel channel : sendChannels) {
      RoundRobinIterator<TileTransceiver> iter = ServerChannelRegister.instance.getIterator(channel);
      for (TileTransceiver reciever : iter) {
        if(isValidDestination(sender, channel, reciever)) {
          int requiredPower = getPowerRequired(cart, sender, reciever);
          if(sender.getEnergyStored(null) >= requiredPower) {
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
    Block blk = reciever.getWorld().getBlockState(reciever.getPos()).getBlock();
    if(blk != this) {
      return false;
    }
    return reciever.getRailController().isClear();
  }

  private int getPowerRequired(EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
    int powerPerCart = getPowerRequiredForSingleCart(sender, reciever);
    int numCarts = CartLinkUtil.instance.getNumberOfCartsInTrain(cart);
    return powerPerCart * numCarts;
  }

  private int getPowerRequiredForSingleCart(TileTransceiver sender, TileTransceiver reciever) {
    int powerRequired = 0;
    if(sender.getWorld().provider.getDimension() != reciever.getWorld().provider.getDimension()) {
      powerRequired = Config.enderRailPowerRequireCrossDimensions;
    } else {
      powerRequired += sender.getLocation().getDist(reciever.getLocation()) * Config.enderRailPowerRequiredPerBlock;
      if(Config.enderRailCapSameDimensionPowerAtCrossDimensionCost) {
        powerRequired = Math.min(powerRequired, Config.enderRailPowerRequireCrossDimensions);
      }
    }
    return powerRequired;
  }

  private boolean teleportCart(World world, EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {

    List<EntityMinecart> allCarts = CartLinkUtil.instance.getCartsInTrain(cart);
    if(allCarts.size() > 1) {
      CartLinkUtil.instance.updateCartLinks(world, cart);
    }

    List<List<Entity>> toTeleport = new ArrayList<List<Entity>>(allCarts.size());
    List<EntityMinecart> toDespawn = new ArrayList<EntityMinecart>(allCarts.size());
    // EntityPlayerMP playerToTP = null;
    // EntityMinecart playerToMount = null;
    for (EntityMinecart cartInTrain : allCarts) {
      if(cartInTrain != null) {
        List<Entity> entities = TeleportUtil.createEntitiesForReciever(cartInTrain, sender, reciever);
        if(entities != null) {
          toTeleport.add(entities);
          toDespawn.add(cartInTrain);
//          if(Config.enderRailTeleportPlayers && cartInTrain.riddenByEntity instanceof EntityPlayerMP) {
//            playerToTP = (EntityPlayerMP) cartInTrain.riddenByEntity;
//            playerToMount = getCart(entities);
//          }
        }
      }
    }
    for (EntityMinecart despawnCart : toDespawn) {
      TeleportUtil.spawnTeleportEffects(world, despawnCart);
      TeleportUtil.despawn(sender.getWorld(), despawnCart);
    }
    reciever.getRailController().onTrainRecieved(toTeleport);
    // if(playerToTP != null) {
    // PlayerTeleportHandler.instance.teleportPlayer(reciever, playerToTP, playerToMount);
    // }
    return true;

  }

  // private EntityMinecart getCart(List<Entity> entities) {
  // for (Entity ent : entities) {
  // if(ent instanceof EntityMinecart) {
  // return (EntityMinecart) ent;
  // }
  // }
  // return null;
  // }

}

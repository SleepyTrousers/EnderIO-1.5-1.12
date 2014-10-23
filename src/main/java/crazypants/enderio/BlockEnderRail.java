package crazypants.enderio;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.BlockTransceiver;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.util.RoundRobinIterator;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEnderRail extends BlockRail {

  public static BlockEnderRail create() {
    BlockEnderRail res = new BlockEnderRail();
    res.init();
    return res;
  }

  protected BlockEnderRail() {
    setBlockName(ModObject.blockEnderRail.unlocalisedName);
    setStepSound(Block.soundTypeMetal);
    setCreativeTab(EnderIOTab.tabEnderIO);
    setBlockTextureName("enderio:blockEnderRail");
  }

  private void init() {
    GameRegistry.registerBlock(this, ModObject.blockEnderRail.unlocalisedName);
  }

  public void onMinecartPass(World world, EntityMinecart cart, int x, int y, int z) {
    if(world.isRemote) {
      return;
    }

    NBTTagCompound nbt = cart.getEntityData();
    if(nbt.hasKey("lastTeleportedTime")) {
      long lastTime = nbt.getLong("lastTeleportedTime");
      if(world.getTotalWorldTime() - lastTime < 20) {
        return;
      }
    }
    
    //cart.boundingBox.maxX - cart.boundingBox.minX
    double xOff = cart.boundingBox.minX - Math.floor(cart.boundingBox.minX);
    double zOff = cart.boundingBox.minZ - Math.floor(cart.boundingBox.minZ);
    System.out.println("BlockEnderRail.onMinecartPass: " + xOff + "," + zOff);
    //if( (xOff > 0.2 && xOff < 0.8) || (zOff > 0.2 && zOff < 0.8) ) {
    if( xOff < 0.2 && zOff < 0.2) {
      tryTeleport(world, cart, x, y, z);
    }

//    double xDiff = Math.abs(cart.posX - x + 0.5);
//    
//    double zDiff = Math.abs(cart.posZ - z + 0.5);
//    System.out.println("BlockEnderRail.onMinecartPass: " + cart.boundingBox);
//    if(xDiff < 0.5 && zDiff < 0.5) {
//      tryTeleport(world, cart, x, y, z);
//    }

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
          //TODO: Check and use power
          //sender.usePower(recievedPlusLoss);
          teleportCart(world, cart, reciever.xCoord, reciever.yCoord + 1, reciever.zCoord, reciever.getWorldObj().provider.dimensionId);
          return;
        }
      }
    }
  }

  private boolean isValidDestination(TileTransceiver sender, Channel channel, TileTransceiver reciever) {
    return reciever != sender && reciever.getRecieveChannels(ChannelType.RAIL).contains(channel) && reciever.hasPower() && reciever.isRedstoneChecksPassed();
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

  private void teleportCart(World world, EntityMinecart cart, int toX, int toY, int toZ, int toDimension) {

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

    Entity entity = EntityList.createEntityByName(EntityList.getEntityString(cart), worldserver1);
    if(entity != null) {

      //      entity.copyDataFrom(cart, true);
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      cart.writeToNBT(nbttagcompound);
      entity.readFromNBT(nbttagcompound);

      entity.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
      NBTTagCompound nbt = entity.getEntityData();
      long time = worldserver1.getTotalWorldTime();
      nbt.setLong("lastTeleportedTime", time);

      if(stacks != null && entity instanceof IInventory) {
        IInventory cont = (IInventory) entity;
        for (int i = 0; i < stacks.length; i++) {
          cont.setInventorySlotContents(i, stacks[i]);
        }
      }

      if(passenger != null) {
        Entity newPas = EntityList.createEntityByName(EntityList.getEntityString(passenger), worldserver1);
        newPas.copyDataFrom(passenger, true);
        newPas.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
        entity.riddenByEntity = newPas;
        newPas.ridingEntity = entity;
        worldserver1.spawnEntityInWorld(newPas);
      }

      worldserver1.spawnEntityInWorld(entity);

    }
    cart.isDead = true;
  }

  public boolean isFlexibleRail(IBlockAccess world, int y, int x, int z) {
    return false;
  }

}

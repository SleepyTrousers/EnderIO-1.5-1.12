package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.enderio.network.PacketHandler;

public class TeleportUtil {

  public static List<Entity> createEntitiesForReciever(EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
    int toDimension = reciever.getWorldObj().provider.dimensionId;
    int toX = reciever.xCoord;
    int toY = reciever.yCoord + 1;
    int toZ = reciever.zCoord;

    MinecraftServer minecraftserver = MinecraftServer.getServer();
    WorldServer worldserver1 = minecraftserver.worldServerForDimension(toDimension);
    EntityMinecart newCart = (EntityMinecart) EntityList.createEntityByName(EntityList.getEntityString(cart), worldserver1);
    if(newCart == null) {
      return null;
    }

    NBTTagCompound nbttagcompound = new NBTTagCompound();
    cart.writeToNBT(nbttagcompound);
    newCart.readFromNBT(nbttagcompound);
    newCart.dimension = toDimension;
    newCart.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
    newCart.isDead = false;

    List<Entity> result = new ArrayList<Entity>();
    result.add(newCart);

    Entity passenger = cart.riddenByEntity;
    if(passenger != null && !(passenger instanceof EntityPlayer)) {
      Entity newPas = EntityList.createEntityByName(EntityList.getEntityString(passenger), worldserver1);
      newPas.copyDataFrom(passenger, true);
      newPas.dimension = toDimension;
      newPas.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
      newCart.riddenByEntity = newPas;
      newPas.ridingEntity = newCart;
      result.add(newPas);
    }
    return result;
  }

  public static void despawn(World world, EntityMinecart cart) {

    if(cart instanceof IInventory) {
      IInventory cont = (IInventory) cart;
      for (int i = 0; i < cont.getSizeInventory(); i++) {
        cont.setInventorySlotContents(i, null);
      }
    }

    MinecraftServer minecraftserver = MinecraftServer.getServer();
    WorldServer worldserver = minecraftserver.worldServerForDimension(world.provider.dimensionId);

    Entity passenger = cart.riddenByEntity;
    if(passenger != null && !(passenger instanceof EntityPlayer)) {
      worldserver.removeEntity(passenger);
      passenger.isDead = true;
    }
    worldserver.removeEntity(cart);
    cart.isDead = true;

  }

  public static void spawn(World world, Entity entity) {
    if(entity != null) {
      MinecraftServer minecraftserver = MinecraftServer.getServer();
      WorldServer worldserver = minecraftserver.worldServerForDimension(world.provider.dimensionId);
      worldserver.spawnEntityInWorld(entity);
    }
  }

  public static void spawnTeleportEffects(World world, Entity entity) {
    PacketHandler.INSTANCE.sendToAllAround(new PacketTeleportEffects(entity), new TargetPoint(world.provider.dimensionId, entity.posX, entity.posY,
        entity.posZ, 64));
    if(Config.machineSoundsEnabled) {
      world.playSoundEffect(entity.posX, entity.posY, entity.posZ, "mob.endermen.portal", 0.5F, 0.25F);
    }
  }  

//  public static void teleportPlayer(WorldServer teleportTo, EntityPlayerMP player, int dimension, ChunkCoordinates spawn) {
//    WorldServer originalWorld = (WorldServer) player.worldObj;
//    if(player.ridingEntity != null) {
//      player.mountEntity(null);
//    }
//    boolean changeDimension = originalWorld != teleportTo;
//
//    player.closeScreen();
//    player.setLocationAndAngles(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
//
//    if(changeDimension) {
//      player.dimension = dimension;
//      player.playerNetServerHandler.sendPacket(new S07PacketRespawn(player.dimension, player.worldObj.difficultySetting, teleportTo.getWorldInfo()
//          .getTerrainType(), player.theItemInWorldManager.getGameType()));
//      removePlayerFromWorld(originalWorld, player);
//      teleportTo.spawnEntityInWorld(player);
//      player.setWorld(teleportTo);
//      player.mcServer.getConfigurationManager().func_72375_a(player, teleportTo);
//      player.theItemInWorldManager.setWorld((WorldServer) teleportTo);
//      player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, (WorldServer) teleportTo);
//      player.mcServer.getConfigurationManager().syncPlayerInventory(player);
//      Iterator iter = player.getActivePotionEffects().iterator();
//      while (iter.hasNext()) {
//        PotionEffect effect = (PotionEffect) iter.next();
//        player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), effect));
//      }
//      player.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
//
//    } else {
//      teleportTo.theChunkProviderServer.loadChunk(spawn.posX >> 4, spawn.posZ >> 4);
//    }
//
//    player.playerNetServerHandler.setPlayerLocation(spawn.posX + 0.5D, spawn.posY, spawn.posZ + 0.5D, player.rotationYaw, player.rotationPitch);
//    player.motionX = 0;
//    player.motionY = 0;
//    player.motionZ = 0;
//    teleportTo.updateEntityWithOptionalForce(player, false);
//  }
//
//  private static void removePlayerFromWorld(WorldServer world, EntityPlayerMP player) {
//    world.removePlayerEntityDangerously(player);
////    world.getPlayerManager().removePlayer(player);
////    world.playerEntities.remove(player);
////    world.updateAllPlayersSleepingFlag();
////    int cx = player.chunkCoordX;
////    int cy = player.chunkCoordZ;
////    if((player.addedToChunk) && (world.getChunkProvider().chunkExists(cx, cy))) {
////      world.getChunkFromChunkCoords(cx, cy).removeEntity(player);
////      world.getChunkFromChunkCoords(cx, cy).isModified = true;
////    }
////    world.loadedEntityList.remove(player);
////    world.getEntityTracker().removeEntityFromAllTrackingPlayers(player);    
//  }

 

}

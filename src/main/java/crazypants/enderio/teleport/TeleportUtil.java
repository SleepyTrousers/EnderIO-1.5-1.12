package crazypants.enderio.teleport;

import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.Log;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.rail.TeleporterEIO;
import crazypants.enderio.sound.SoundHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TeleportUtil {

  
  public static boolean doTeleport(Entity entityLiving, BlockPos pos, int targetDim, boolean conserveMotion, TravelSource source) {
    if (entityLiving.worldObj.isRemote) {
      return checkClientTeleport(entityLiving, pos, targetDim, source);
    }       
    return serverTeleport(entityLiving, pos, targetDim, conserveMotion, source);
  }

  public static boolean checkClientTeleport(Entity entityLiving, BlockPos pos, int targetDim, TravelSource source) {
    TeleportEntityEvent evt = new TeleportEntityEvent(entityLiving, source, pos.getX(), pos.getY(), pos.getZ(), targetDim);
    if(MinecraftForge.EVENT_BUS.post(evt)) {
      return false;
    }         
    return true;
  }

  public static boolean serverTeleport(Entity entity, BlockPos pos, int targetDim, boolean conserveMotion, TravelSource source) {
    
    TeleportEntityEvent evt = new TeleportEntityEvent(entity, source, pos.getX(), pos.getY(), pos.getZ(), targetDim);
    if(MinecraftForge.EVENT_BUS.post(evt)) {
      return false;
    }
    
    EntityPlayerMP player = null;
    if (entity instanceof EntityPlayerMP) {
      player = (EntityPlayerMP) entity;
    }
    int x = pos.getX();
    int y = pos.getY();
    int z = pos.getZ();
    
    int from = entity.dimension;
    if (from != targetDim) {
      MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
      WorldServer fromDim = server.worldServerForDimension(from);
      WorldServer toDim = server.worldServerForDimension(targetDim);
      Teleporter teleporter = new TeleporterEIO(toDim);
      if(source != null) {
        //play sound at the dimension we are leaving for others to hear
        SoundHelper.playSound(server.worldServerForDimension(entity.dimension), entity, source.sound, 1.0F, 1.0F);
      }
      if (player != null) {        
        server.getPlayerList().transferPlayerToDimension(player, targetDim, teleporter);
        if (from == 1 && entity.isEntityAlive()) { // get around vanilla End
                                                   // hacks
          toDim.spawnEntityInWorld(entity);
          toDim.updateEntityWithOptionalForce(entity, false);
        }
      } else {
        NBTTagCompound tagCompound = new NBTTagCompound();
        float rotationYaw = entity.rotationYaw;
        float rotationPitch = entity.rotationPitch;
        entity.writeToNBT(tagCompound);
        Class<? extends Entity> entityClass = entity.getClass();
        fromDim.removeEntity(entity);

        try {
          Entity newEntity = entityClass.getConstructor(World.class).newInstance(toDim);
          newEntity.readFromNBT(tagCompound);
          newEntity.setLocationAndAngles(x,y,z, rotationYaw, rotationPitch);
          newEntity.forceSpawn = true;
          toDim.spawnEntityInWorld(newEntity);
          newEntity.forceSpawn = false; // necessary?
        } catch (Exception e) {
          //Throwables.propagate(e);
          Log.error("serverTeleport: Error creating a entity to be created in new dimension.");
          return false;
        }
      }
    }
    
    
    //Force the chunk to load
    if(!entity.worldObj.isBlockLoaded(pos)) {           
      entity.worldObj.getChunkFromBlockCoords(pos);      
    }

    if(player != null) {
      player.connection.setPlayerLocation(x + 0.5, y + 1.1, z + 0.5, player.rotationYaw, player.rotationPitch);
    } else {
      entity.setPositionAndUpdate(x + 0.5, y + 1.1, z + 0.5);
    }

    entity.fallDistance = 0;
    if(source != null) {
      SoundHelper.playSound(entity.worldObj, entity, source.sound, 1.0F, 1.0F);
    }

    if(player != null) {
      if(conserveMotion) {
        Vector3d velocityVex = Util.getLookVecEio(player);
        SPacketEntityVelocity p = new SPacketEntityVelocity(entity.getEntityId(), velocityVex.x, velocityVex.y, velocityVex.z);
        player.connection.sendPacket(p);
      }
    }
    
    return true;
  }
  
  
}

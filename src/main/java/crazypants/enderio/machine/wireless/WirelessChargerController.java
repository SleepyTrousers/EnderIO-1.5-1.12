package crazypants.enderio.machine.wireless;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scala.Array;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelController;
import crazypants.enderio.item.darksteel.DarkSteelRecipeManager;
import crazypants.util.BlockCoord;

public class WirelessChargerController {

  public static WirelessChargerController instance = new WirelessChargerController();

  public static final int RANGE = Config.wirelessChargerRange;
  public static final int RANGE_SQ = RANGE * RANGE;

  static {
    FMLCommonHandler.instance().bus().register(WirelessChargerController.instance);
    MinecraftForge.EVENT_BUS.register(WirelessChargerController.instance);
  }

  private Map<Integer, Map<BlockCoord, IWirelessCharger>> perWorldChargers = new HashMap<Integer, Map<BlockCoord, IWirelessCharger>>();

  private WirelessChargerController() {
  }

  public void registerCharger(IWirelessCharger capBank) {
    if(capBank == null) {
      return;
    }    
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(capBank.getWorld());
    chargers.put(capBank.getLocation(), capBank);
  }

  public void deregisterCharger(IWirelessCharger capBank) {
    if(capBank == null) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(capBank.getWorld());
    chargers.remove(capBank.getLocation());
  }

  @SubscribeEvent
  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    if(event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) {
      return;
    }
    Map<BlockCoord, IWirelessCharger> chargers = getChargersForWorld(event.player.worldObj);
    if(chargers.isEmpty()) {
      return;
    }
    BlockCoord bc = new BlockCoord((int) event.player.posX, (int) event.player.posY, (int) event.player.posZ);
    for (IWirelessCharger capBank : chargers.values()) {
      if(capBank.getLocation().distanceSquared(bc) <= RANGE_SQ) {
        boolean done = chargeFromCapBank(event.player, capBank);
        if(done) {
          return;
        }
      }
    }
  }

  private boolean chargeFromCapBank(EntityPlayer player, IWirelessCharger capBank) {
    boolean res = capBank.chargeItems(player.inventory.armorInventory);
    res |= capBank.chargeItems(player.inventory.mainInventory);
    if(res) {
      player.inventory.markDirty();
    }
    return res;
  }

  private Map<BlockCoord, IWirelessCharger> getChargersForWorld(World world) {
    Map<BlockCoord, IWirelessCharger> res = perWorldChargers.get(world.provider.dimensionId);
    if(res == null) {
      res = new HashMap<BlockCoord, IWirelessCharger>();
      perWorldChargers.put(world.provider.dimensionId, res);
    }
    return res;
  }

  //  public static final int CHUNK_RANGE = (RANGE/16 + (RANGE%16 == 0 ? 0 : 1));
  //
  //  private Map<Integer, Map<ChunkCoordIntPair, List<TileCapacitorBank>>> perWorldChargers = new HashMap<Integer, Map<ChunkCoordIntPair,List<TileCapacitorBank>>>();
  //  
  //  private WirelessChargerController() {   
  //  }
  //  
  //  public void registerCharger(TileCapacitorBank capBank) {
  //    if(capBank == null) {
  //      return;
  //    }       
  //    Map<ChunkCoordIntPair, List<TileCapacitorBank>> chargers = getChargersForWorld(capBank.getWorld());
  //    List<ChunkCoordIntPair> coords = getChunksInRange(capBank.getLocation());
  //    for(ChunkCoordIntPair cc : coords) {
  //      addCharger(chargers, cc, capBank);      
  //    }
  //    
  //  }
  //
  //  public void dereigsterCharger(TileCapacitorBank capBank) {
  //    if(capBank == null) {
  //      return;
  //    }
  //    Map<ChunkCoordIntPair, List<TileCapacitorBank>> chargers = getChargersForWorld(capBank.getWorld());
  //    List<ChunkCoordIntPair> coords = getChunksInRange(capBank.getLocation());
  //    for(ChunkCoordIntPair cc : coords) {
  //      removeCharger(chargers, cc, capBank);
  //    }
  //  }
  //
  //  
  //  @SubscribeEvent
  //  public void onPlayerTick(TickEvent.PlayerTickEvent event) {
  //    if(event.side == Side.CLIENT || event.phase != TickEvent.Phase.END) {
  //      return;
  //    }
  //    
  //    List<TileCapacitorBank> charges = getChargersInRange(event.player);
  //    
  //    
  //  }
  //  
  //  private List<TileCapacitorBank> getChargersInRange(EntityPlayer player) {
  //    Map<ChunkCoordIntPair, List<TileCapacitorBank>> worldCharges = getChargersForWorld(player.worldObj);
  //    if(worldCharges.isEmpty()) {
  //      return null;
  //    }
  //    BlockCoord bc = new BlockCoord((int)player.posX, 0, (int)player.posZ);
  //    List<ChunkCoordIntPair> chunks = getChunksInRange(bc);
  //    
  //    List<TileCapacitorBank> result = new ArrayList<TileCapacitorBank>();
  //    
  //  }
  //  
  //    
  //  private void removeCharger(Map<ChunkCoordIntPair, List<TileCapacitorBank>> chunkChargers, ChunkCoordIntPair cc, TileCapacitorBank capBank) {
  //    List<TileCapacitorBank> chargers = chunkChargers.get(cc);
  //    if(chargers == null) {
  //      return;      
  //    }
  //    chargers.remove(capBank);
  //    if(chargers.size() == 0) {
  //      chunkChargers.remove(cc);
  //    }
  //  }
  //  
  //  private void addCharger(Map<ChunkCoordIntPair, List<TileCapacitorBank>> chunkChargers, ChunkCoordIntPair cc, TileCapacitorBank capBank) {    
  //    List<TileCapacitorBank> chargers = chunkChargers.get(cc);
  //    if(chargers == null) {
  //      chargers = new ArrayList<TileCapacitorBank>();
  //      chunkChargers.put(cc, chargers);
  //    }
  //    chargers.add(capBank);
  //  }
  //
  //  private List<ChunkCoordIntPair> getChunksInRange(BlockCoord coord) {
  //    int chunkX = coord.x >> 4;
  //    int chunkY = coord.y >> 4;
  //    
  //    List<ChunkCoordIntPair> res = new ArrayList<ChunkCoordIntPair>();
  //    for(int xOffset=-CHUNK_RANGE; xOffset <= CHUNK_RANGE; xOffset++) {
  //      for(int yOffset=-CHUNK_RANGE; yOffset <= CHUNK_RANGE; yOffset++) {
  //       res.add(new ChunkCoordIntPair(chunkX + xOffset, chunkY + yOffset)); 
  //      }      
  //    }
  //    return res;
  //  }
  //  
  //  private Map<ChunkCoordIntPair, List<TileCapacitorBank>> getChargersForWorld(World world) {
  //    int id = world.provider.dimensionId;
  //    Map<ChunkCoordIntPair, List<TileCapacitorBank>> res = perWorldChargers.get(id);
  //    if(res == null) {
  //      res = new HashMap<ChunkCoordIntPair, List<TileCapacitorBank>>();
  //      perWorldChargers.put(id, res);
  //    }
  //    return res;
  //  }

}

package crazypants.enderio.machine.obelisk.inhibitor;

import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Maps;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;

public class BlockInhibitorObelisk extends BlockObeliskAbstract<TileInhibitorObelisk> {

  public static BlockInhibitorObelisk instance;

  public static BlockInhibitorObelisk create() {
    BlockInhibitorObelisk res = new BlockInhibitorObelisk();
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return instance = res;
  }

  protected BlockInhibitorObelisk() {
    super(ModObject.blockInhibitorObelisk, TileInhibitorObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == getGuiId()) {
      TileInhibitorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
      if (te != null) {
        return new ContainerInhibitorObelisk(player.inventory, te);
      }
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    if (ID == getGuiId()) {
      TileInhibitorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
      if (te != null) {
        return new GuiInhibitorObelisk(te, new ContainerInhibitorObelisk(player.inventory, te));
      }
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_INHIBITOR;
  }

  public Map<BlockCoord, Float> activeInhibitors = Maps.newHashMap();

  @SubscribeEvent
  public void onTeleport(TeleportEntityEvent event) {
    for (Entry<BlockCoord, Float> e : activeInhibitors.entrySet()) {
      BlockCoord bc = e.getKey();
      int dist = bc.getDist(new BlockCoord(event.targetX, event.targetY, event.targetZ));
      if (dist < e.getValue()) {
        TileEntity te = bc.getTileEntity(event.entity.worldObj);
        if (te instanceof TileInhibitorObelisk && ((TileInhibitorObelisk) te).isActive()) {
          event.setCanceled(true);
        }
      }
    }
  }
}

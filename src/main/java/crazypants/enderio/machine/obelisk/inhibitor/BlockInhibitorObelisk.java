package crazypants.enderio.machine.obelisk.inhibitor;

import java.util.Map;
import java.util.Map.Entry;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Maps;

import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machine.obelisk.GuiRangedObelisk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockInhibitorObelisk extends AbstractBlockObelisk<TileInhibitorObelisk> {

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
        return new GuiRangedObelisk(player.inventory, te, new ContainerInhibitorObelisk(player.inventory, te), "inhibitor");
      }
    }
    return null;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_INHIBITOR;
  }


  public Map<BlockCoord, BoundingBox> activeInhibitors = Maps.newHashMap();

  @SubscribeEvent
  public void onTeleport(TeleportEntityEvent event) {
    Vec3d pos = new Vec3d(event.targetX,event.targetY,event.targetZ);
    for (Entry<BlockCoord, BoundingBox> e : activeInhibitors.entrySet()) {
      if (e.getValue().isVecInside(pos)) {
        BlockCoord bc = e.getKey();
        TileEntity te = bc.getTileEntity(event.getEntity().worldObj);
        if (te instanceof TileInhibitorObelisk && ((TileInhibitorObelisk) te).isActive() && te.getWorld().provider.getDimension() == event.dimension) {
          event.setCanceled(true);
        }
      }
    }
  }
}

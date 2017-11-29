package crazypants.enderio.machines.machine.obelisk.inhibitor;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.google.common.collect.Maps;

import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.init.MachineObject;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machines.machine.obelisk.GuiRangedObelisk;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Map.Entry;

public class BlockInhibitorObelisk extends AbstractBlockObelisk<TileInhibitorObelisk> {

  public static BlockInhibitorObelisk instance;

  public static BlockInhibitorObelisk create(@Nonnull IModObject modObject) {
    BlockInhibitorObelisk res = new BlockInhibitorObelisk(modObject);
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return instance = res;
  }

  protected BlockInhibitorObelisk(@Nonnull IModObject modObject) {
    super(modObject, TileInhibitorObelisk.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInhibitorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerInhibitorObelisk(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileInhibitorObelisk te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiRangedObelisk(player.inventory, te, new ContainerInhibitorObelisk(player.inventory, te), "inhibitor");
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_INHIBITOR;
  }


  public Map<BlockPos, BoundingBox> activeInhibitors = Maps.newHashMap();

  // Ender IO's teleporting
  @SubscribeEvent
  public void onTeleport(TeleportEntityEvent event) {
    if (isTeleportPrevented(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ)) {
      event.setCanceled(true);
    }
    if (isTeleportPrevented(event.getEntity().world, event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ())) {
      event.setCanceled(true);
    }
  }

  // Forge's event for endermen and enderpearl teleporting
  @SubscribeEvent
  public void onEnderTeleport(EnderTeleportEvent event) {
    if (isTeleportPrevented(event.getEntity().world, event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ)) {
      event.setCanceled(true);
    }
    if (isTeleportPrevented(event.getEntity().world, event.getTargetX(), event.getTargetY(), event.getTargetZ())) {
      event.setCanceled(true);
    }
  }

  private boolean isTeleportPrevented(World entityWorld, double d, double f, double g) {
    if (!activeInhibitors.isEmpty()) {
      Vec3d pos = new Vec3d(d, f, g);
      for (Entry<BlockPos, BoundingBox> e : activeInhibitors.entrySet()) {
        if (e.getValue().isVecInside(pos)) {
          BlockPos bc = e.getKey();
          if (entityWorld.isBlockLoaded(bc)) {
            TileEntity te = entityWorld.getTileEntity(bc);
            if (te instanceof TileInhibitorObelisk && ((TileInhibitorObelisk) te).isActive() && ((TileInhibitorObelisk) te).getBounds().isVecInside(pos)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

}

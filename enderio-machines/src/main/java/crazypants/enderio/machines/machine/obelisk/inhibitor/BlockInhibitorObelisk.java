package crazypants.enderio.machines.machine.obelisk.inhibitor;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.NullHelper;
import com.google.common.collect.Maps;

import crazypants.enderio.api.teleport.TeleportEntityEvent;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.base.AbstractBlockRangedObelisk;
import crazypants.enderio.machines.machine.obelisk.base.GuiRangedObelisk;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockInhibitorObelisk extends AbstractBlockRangedObelisk<TileInhibitorObelisk> {

  public static BlockInhibitorObelisk instance;

  public static BlockInhibitorObelisk create(@Nonnull IModObject modObject) {
    BlockInhibitorObelisk res = new BlockInhibitorObelisk(modObject);
    res.init();
    MinecraftForge.EVENT_BUS.register(res);
    return instance = res;
  }

  protected BlockInhibitorObelisk(@Nonnull IModObject modObject) {
    super(modObject);
  }

  @Override
  public @Nullable Container getServerGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInhibitorObelisk te) {
    return new ContainerInhibitorObelisk(player.inventory, te);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public @Nullable GuiScreen getClientGuiElement(@Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos, @Nullable EnumFacing facing,
      int param1, @Nonnull TileInhibitorObelisk te) {
    return new GuiRangedObelisk(player.inventory, te, new ContainerInhibitorObelisk(player.inventory, te), "inhibitor");
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
        if (e.getValue().contains(pos)) {
          BlockPos bc = NullHelper.notnull(e.getKey(), "activeInhibitors has invalid bc");
          if (entityWorld.isBlockLoaded(bc)) {
            TileEntity te = entityWorld.getTileEntity(bc);
            if (te instanceof TileInhibitorObelisk && ((TileInhibitorObelisk) te).isActive() && ((TileInhibitorObelisk) te).getBounds().contains(pos)) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

}

package crazypants.enderio.machine.spawner;

import java.util.Locale;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.client.render.RenderUtil;
import com.enderio.core.common.vecmath.Vector3f;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.Log;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.spawner.TilePoweredSpawner.SpawnResult;
import static crazypants.enderio.machine.spawner.TilePoweredSpawner.SpawnResult.OK;
import static org.lwjgl.opengl.GL11.*;

@SideOnly(Side.CLIENT)
public class PoweredSpawnerSpecialRenderer extends TileEntitySpecialRenderer {
  private String tooltipKey = "gui.notification.poweredSpawner.";

  private void renderPoweredSpawnerAt(TilePoweredSpawner tile, double x, double y, double z, float partialTickTime) {

    SpawnResult reason = tile.getReason();
    if (reason == OK || Config.disableFarmNotification) {
      return;
    }
    String toRender = EnderIO.lang.localize(tooltipKey + reason.name().toLowerCase(Locale.US));

    glDisable(GL_LIGHTING);
    RenderUtil.drawBillboardedText(new Vector3f(x + 0.5, y + 1.5, z + 0.5), toRender, 0.25f);
    glEnable(GL_LIGHTING);

  }

  @Override
  public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime) {
    if (tile instanceof TilePoweredSpawner) {
      renderPoweredSpawnerAt((TilePoweredSpawner) tile, x, y, z, partialTickTime);
    }
  }
}

package crazypants.util;

import java.lang.reflect.Field;

import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.Log;

@SideOnly(Side.CLIENT)
public class RenderPassHelper {

  private static Field worldRenderPass = null;
  private static int savedWorldRenderPass = -1;
  private static int savedEntityRenderPass = -1;

  static {
    try {
      worldRenderPass = ForgeHooksClient.class.getDeclaredField("worldRenderPass");
      worldRenderPass.setAccessible(true);
    } catch (Exception e) {
      Log.warn("Failed to access ForgeHooksClient.worldRenderPass because of: " + e);
      e.printStackTrace();
    }
  }

  private RenderPassHelper() {
  }

  public static void setBlockRenderPass(int pass) {
    savedWorldRenderPass = ForgeHooksClient.getWorldRenderPass();
    savedEntityRenderPass = MinecraftForgeClient.getRenderPass();
    setBlockRenderPassImpl(pass);
    setEntityRenderPass(pass);
  }

  private static void setBlockRenderPassImpl(int pass) {
    if (worldRenderPass != null) {
      try {
        worldRenderPass.setInt(null, pass);
      } catch (Exception e) {
        Log.warn("Failed to access ForgeHooksClient.worldRenderPass because of: " + e);
        e.printStackTrace();
        worldRenderPass = null;
      }
    }
  }

  public static void clearBlockRenderPass() {
    setBlockRenderPassImpl(savedWorldRenderPass);
    setEntityRenderPass(savedEntityRenderPass);
  }

  public static int getBlockRenderPass() {
    int pass = ForgeHooksClient.getWorldRenderPass();
    if (pass < 0) {
      // We are outside Minecraft's block rendering, so most probably we are
      // being rendered by some mod. But it forgot to set the current block
      // render pass. Maybe it set the entity render pass instead?
      pass = MinecraftForgeClient.getRenderPass();
      if (pass < 0) {
        // No, it didn't. That's not good. Let's assume pass 0, that one renders
        // more stuff.
        pass = 0;
      }
    }
    return pass;
  }

  public static void setEntityRenderPass(int pass) {
    ForgeHooksClient.setRenderPass(pass);
  }

  public static void clearEntityRenderPass() {
    ForgeHooksClient.setRenderPass(-1);
  }

  public static int getEntityRenderPass() {
    return MinecraftForgeClient.getRenderPass();
  }
}

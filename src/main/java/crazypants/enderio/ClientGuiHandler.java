package crazypants.enderio;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientGuiHandler extends GuiHandler {

  @Override
  public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    return null;
  }

  @Override
  public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
    ISidedGuiHandler handler = guiHandlers.get(id);
    if(handler != null) {
      return handler.getClientGuiElement(id, player, world, x, y, z);
    }
    return null;
  }

}

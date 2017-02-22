package crazypants.enderio.machine.obelisk.attractor;

import java.util.Locale;

import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiID;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machine.obelisk.PacketObeliskFx;
import crazypants.enderio.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BlockAttractor extends AbstractBlockObelisk<TileAttractor> {

  public static BlockAttractor create() {
    PacketHandler.INSTANCE.registerMessage(PacketObeliskFx.class, PacketObeliskFx.class, PacketHandler.nextID(), Side.CLIENT);
    BlockAttractor res = new BlockAttractor();
    res.init();
    MinecraftForge.EVENT_BUS.register(new EndermanFixer());
    return res;
  }

  protected BlockAttractor() {
    super(ModObject.blockAttractor, TileAttractor.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileAttractor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new ContainerAttractor(player.inventory, te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileAttractor te = getTileEntity(world, new BlockPos(x, y, z));
    if (te != null) {
      return new GuiAttractor(player.inventory, te);
    }
    return null;
  }

  @Override
  protected GuiID getGuiId() {
    return GuiID.GUI_ID_ATTRACTOR;
  }

  protected static String permissionAttracting;

  @Override
  public void init(FMLInitializationEvent event) {
    super.init(event);
    permissionAttracting = PermissionAPI.registerNode(EnderIO.DOMAIN + ".attract." + name.toLowerCase(Locale.ENGLISH), DefaultPermissionLevel.ALL,
        "Permission for the block " + name + " of Ender IO to attract entities."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}

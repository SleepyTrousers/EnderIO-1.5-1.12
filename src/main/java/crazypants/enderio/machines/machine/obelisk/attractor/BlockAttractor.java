package crazypants.enderio.machines.machine.obelisk.attractor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.GuiID;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.network.PacketHandler;
import crazypants.enderio.machines.machine.obelisk.AbstractBlockObelisk;
import crazypants.enderio.machines.machine.obelisk.PacketObeliskFx;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BlockAttractor extends AbstractBlockObelisk<TileAttractor> {

  public static BlockAttractor create(@Nonnull IModObject modObject) {
    PacketHandler.INSTANCE.registerMessage(PacketObeliskFx.class, PacketObeliskFx.class, PacketHandler.nextID(), Side.CLIENT);
    BlockAttractor res = new BlockAttractor(modObject);
    res.init();
    MinecraftForge.EVENT_BUS.register(new EndermanFixer());
    return res;
  }

  protected BlockAttractor(@Nonnull IModObject modObject) {
    super(modObject, TileAttractor.class);
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
  protected @Nonnull GuiID getGuiId() {
    return GuiID.GUI_ID_ATTRACTOR;
  }

  protected static String permissionAttracting;

  @Override
  public void init(@Nonnull IModObject object, @Nonnull FMLInitializationEvent event) {
    super.init(object, event);
    permissionAttracting = PermissionAPI.registerNode(EnderIO.DOMAIN + ".attract." + this.getUnlocalizedName().toLowerCase(Locale.ENGLISH),
        DefaultPermissionLevel.ALL, "Permission for the block " + this.getUnlocalizedName() + " of Ender IO to attract entities."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}

package crazypants.enderio.machines.machine.obelisk.attractor;

import java.util.Locale;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.machines.machine.obelisk.attractor.handlers.EndermanFixer;
import crazypants.enderio.machines.machine.obelisk.base.AbstractBlockRangedObelisk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import net.minecraftforge.server.permission.PermissionAPI;

public class BlockAttractor extends AbstractBlockRangedObelisk<TileAttractor> {

  public static BlockAttractor create(@Nonnull IModObject modObject) {
    BlockAttractor res = new BlockAttractor(modObject);
    res.init();
    MinecraftForge.EVENT_BUS.register(new EndermanFixer());
    return res;
  }

  protected BlockAttractor(@Nonnull IModObject modObject) {
    super(modObject, TileAttractor.class);
  }

  protected static @Nonnull String permissionAttracting = "(unititialized)";

  @Override
  public void init(@Nonnull IModObject object, @Nonnull FMLInitializationEvent event) {
    super.init(object, event);
    permissionAttracting = PermissionAPI.registerNode(EnderIO.DOMAIN + ".attract." + this.getUnlocalizedName().toLowerCase(Locale.ENGLISH),
        DefaultPermissionLevel.ALL, "Permission for the block " + this.getUnlocalizedName() + " of Ender IO to attract entities."
            + " Note: The GameProfile will be for the block owner, the EntityPlayer in the context will be the fake player.");
  }

}

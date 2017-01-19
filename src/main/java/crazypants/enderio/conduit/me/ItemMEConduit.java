package crazypants.enderio.conduit.me;

import appeng.api.AEApi;
import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.registry.ConduitRegistry;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMEConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemMEConduit.name(), EnderIO.DOMAIN + ":itemMeConduit"),
      new ItemConduitSubtype(ModObject.itemMEConduit.name() + "Dense", EnderIO.DOMAIN + ":itemMeConduitDense")
  };

  private final ConduitRegistry.ConduitInfo conduitInfo;

  public static ItemMEConduit create() {
    if (MEUtil.isMEEnabled()) {
      ItemMEConduit result = new ItemMEConduit();
      result.init();
      return result;
    }
    return null;
  }

  protected ItemMEConduit() {
    super(ModObject.itemMEConduit, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.SOUTH_UP, Offset.SOUTH_UP, Offset.NORTH_EAST, Offset.EAST_UP);
    conduitInfo.addMember(MEConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ME, IconEIO.WRENCH_OVERLAY_ME_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    super.registerRenderers();
    conduitInfo.addRenderer(new MEConduitRenderer());
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IMEConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack item, EntityPlayer player) {
    MEConduit con = new MEConduit(item.getItemDamage());
    con.setPlayerID(AEApi.instance().registries().players().getID(player));
    return con;
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}

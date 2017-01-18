package crazypants.enderio.conduit.oc;

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

public class ItemOCConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] { new ItemConduitSubtype(ModObject.itemOCConduit.name(), "enderio:itemOCConduit") };

  private final ConduitRegistry.ConduitInfo conduitInfo;

  public static ItemOCConduit create() {
    if (OCUtil.isOCEnabled()) {
      ItemOCConduit result = new ItemOCConduit();
      result.init();
      return result;
    }
    return null;
  }

  protected ItemOCConduit() {
    super(ModObject.itemOCConduit, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.NORTH_DOWN, Offset.NORTH_DOWN, Offset.SOUTH_WEST, Offset.WEST_DOWN);
    conduitInfo.addMember(OCConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_OC, IconEIO.WRENCH_OVERLAY_OC_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    super.registerRenderers();
    conduitInfo.addRenderer(new OCConduitRenderer());
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return IOCConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack item, EntityPlayer player) {
    OCConduit con = new OCConduit(item.getItemDamage());
    return con;
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}

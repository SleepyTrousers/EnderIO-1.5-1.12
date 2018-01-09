package crazypants.enderio.conduit.oc;

import javax.annotation.Nonnull;

import crazypants.enderio.base.ModObject;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduit.item.AbstractItemConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
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
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
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

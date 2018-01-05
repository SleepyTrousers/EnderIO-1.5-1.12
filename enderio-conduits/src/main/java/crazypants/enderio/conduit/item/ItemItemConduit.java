package crazypants.enderio.conduit.item;

import javax.annotation.Nonnull;

import crazypants.enderio.base.ModObject;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemItemConduit extends AbstractItemConduit {

  private final ConduitRegistry.ConduitInfo conduitInfo;

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemItemConduit.name(), "enderio:itemItemConduit")
  };

  public static ItemItemConduit create() {
    ItemItemConduit result = new ItemItemConduit();
    result.init();
    return result;
  }

  protected ItemItemConduit() {
    super(ModObject.itemItemConduit, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.EAST, Offset.SOUTH, Offset.EAST, Offset.EAST);
    conduitInfo.addMember(ItemConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ITEM, IconEIO.WRENCH_OVERLAY_ITEM_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    super.registerRenderers();
    conduitInfo.addRenderer(new ItemConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack item, EntityPlayer player) {
    return new ItemConduit(item.getItemDamage());
  }

  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}

package crazypants.enderio.conduit.redstone;

import javax.annotation.Nonnull;

import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.item.AbstractItemConduit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRedstoneConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ConduitObject.item_redstone_conduit.name() + "Insulated", "enderio:itemRedstoneInsulatedConduit") };

  public static ItemRedstoneConduit create(@Nonnull IModObject modObject) {
    return new ItemRedstoneConduit(modObject);
  }

  private final ConduitRegistry.ConduitInfo conduitInfo;

  protected ItemRedstoneConduit(@Nonnull IModObject modObject) {
    super(modObject, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.UP, Offset.UP, Offset.NORTH, Offset.UP);
    conduitInfo.addMember(InsulatedRedstoneConduit.class);
    conduitInfo.setCanConnectToAnything();
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_REDSTONE, IconEIO.WRENCH_OVERLAY_REDSTONE_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    conduitInfo.addRenderer(new InsulatedRedstoneConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IRedstoneConduit.class;
  }

  @Override
  public IConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return new InsulatedRedstoneConduit();
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }
}

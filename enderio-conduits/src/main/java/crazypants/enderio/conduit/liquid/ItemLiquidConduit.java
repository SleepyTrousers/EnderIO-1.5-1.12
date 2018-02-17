package crazypants.enderio.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.config.ConduitConfig;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.item.AbstractItemConduit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLiquidConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

  private final ConduitRegistry.ConduitInfo conduitInfo;

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name(), "enderio:itemLiquidConduit"),
      new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name() + "_advanced", "enderio:itemLiquidConduitAdvanced"),
      new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name() + "_ender", "enderio:itemLiquidConduitEnder")

  };

  public static ItemLiquidConduit create(@Nonnull IModObject modObject) {
    return new ItemLiquidConduit(modObject);
  }

  protected ItemLiquidConduit(@Nonnull IModObject modObject) {
    super(modObject, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.WEST, Offset.NORTH, Offset.WEST, Offset.WEST);
    conduitInfo.addMember(LiquidConduit.class);
    conduitInfo.addMember(AdvancedLiquidConduit.class);
    conduitInfo.addMember(EnderLiquidConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_FLUID, IconEIO.WRENCH_OVERLAY_FLUID_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    conduitInfo.addRenderer(LiquidConduitRenderer.create());
    conduitInfo.addRenderer(new AdvancedLiquidConduitRenderer());
    conduitInfo.addRenderer(new EnderLiquidConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public IConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    if (stack.getItemDamage() == 1) {
      return new AdvancedLiquidConduit();
    } else if (stack.getItemDamage() == 2) {
      return new EnderLiquidConduit();
    }
    return new LiquidConduit();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nullable EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    int extractRate;
    int maxIo;

    if (itemstack.getItemDamage() == 0) {
      extractRate = ConduitConfig.fluid_tier1_extractRate.get();
      maxIo = ConduitConfig.fluid_tier1_maxIO.get();
    } else if (itemstack.getItemDamage() == 1) {
      extractRate = ConduitConfig.fluid_tier2_extractRate.get();
      maxIo = ConduitConfig.fluid_tier2_maxIO.get();
    } else {
      extractRate = ConduitConfig.fluid_tier3_extractRate.get();
      maxIo = ConduitConfig.fluid_tier3_maxIO.get();
    }

    // TODO Lang

    String mbt = " " + EnderIO.lang.localize("fluid.millibuckets_tick");
    list.add(EnderIO.lang.localize("item_liquid_conduit.tooltip.max_extract") + " " + extractRate + mbt);
    list.add(EnderIO.lang.localize("item_liquid_conduit.tooltip.max_io") + " " + maxIo + mbt);

    if (itemstack.getItemDamage() == 0) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.item_liquid_conduit");
    } else if (itemstack.getItemDamage() == 2) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.item_liquid_conduit_ender");
    }

  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

}

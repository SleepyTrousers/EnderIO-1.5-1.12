package crazypants.enderio.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.item.AbstractItemConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;



public class ItemLiquidConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

  private final ConduitRegistry.ConduitInfo conduitInfo;

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
    new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name(), "enderio:itemLiquidConduit"),
    new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name() + "Advanced", "enderio:itemLiquidConduitAdvanced"),
    new ItemConduitSubtype(ConduitObject.item_liquid_conduit.name() + "Ender", "enderio:itemLiquidConduitEnder")

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
    if(stack.getItemDamage() == 1) {
      return new AdvancedLiquidConduit();
    } else if(stack.getItemDamage() == 2) {
      return new EnderLiquidConduit();
    }
    return new LiquidConduit();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(@Nonnull ItemStack itemstack, @Nonnull EntityPlayer entityplayer, @Nonnull List<String> list, boolean flag) {
    int extractRate;
    int maxIo;

    if(itemstack.getItemDamage() == 0) {
      extractRate = Config.fluidConduitExtractRate;
      maxIo = Config.fluidConduitMaxIoRate;
    } else if(itemstack.getItemDamage() == 1){
      extractRate = Config.advancedFluidConduitExtractRate;
      maxIo = Config.advancedFluidConduitMaxIoRate;
    } else {
      extractRate = Config.enderFluidConduitExtractRate;
      maxIo = Config.enderFluidConduitMaxIoRate;
    }

    // TODO Lang

    String mbt = " " + EnderIO.lang.localize("fluid.millibucketsTick");
    list.add(EnderIO.lang.localize("itemLiquidConduit.tooltip.maxExtract") + " " + extractRate + mbt);
    list.add(EnderIO.lang.localize("itemLiquidConduit.tooltip.maxIo") + " " + maxIo + mbt);

    if(itemstack.getItemDamage() == 0) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.itemLiquidConduit");
    } else if(itemstack.getItemDamage() == 2) {
      SpecialTooltipHandler.addDetailedTooltipFromResources(list, "enderio.itemLiquidConduitEnder");      
    }

  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

}

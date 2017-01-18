package crazypants.enderio.conduit.liquid;

import java.util.List;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.ConduitDisplayMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.geom.Offset;
import crazypants.enderio.conduit.registry.ConduitRegistry;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IconEIO;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLiquidConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

  private final ConduitRegistry.ConduitInfo conduitInfo;

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] {
    new ItemConduitSubtype(ModObject.itemLiquidConduit.name(), "enderio:itemLiquidConduit"),
    new ItemConduitSubtype(ModObject.itemLiquidConduit.name() + "Advanced", "enderio:itemLiquidConduitAdvanced"),
    new ItemConduitSubtype(ModObject.itemLiquidConduit.name() + "Ender", "enderio:itemLiquidConduitEnder")

  };

  public static ItemLiquidConduit create() {
    ItemLiquidConduit result = new ItemLiquidConduit();
    result.init();
    return result;
  }

  protected ItemLiquidConduit() {
    super(ModObject.itemLiquidConduit, subtypes);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.WEST, Offset.NORTH, Offset.WEST, Offset.WEST);
    conduitInfo.addMember(LiquidConduit.class);
    conduitInfo.addMember(AdvancedLiquidConduit.class);
    conduitInfo.addMember(EnderLiquidConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_FLUID, IconEIO.WRENCH_OVERLAY_FLUID_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    super.registerRenderers();
    conduitInfo.addRenderer(LiquidConduitRenderer.create());
    conduitInfo.addRenderer(new AdvancedLiquidConduitRenderer());
    conduitInfo.addRenderer(new EnderLiquidConduitRenderer());
  }

  @Override
  public Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack, EntityPlayer player) {
    if(stack.getItemDamage() == 1) {
      return new AdvancedLiquidConduit();
    } else if(stack.getItemDamage() == 2) {
      return new EnderLiquidConduit();
    }
    return new LiquidConduit();
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {

  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean flag) {
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
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }

}

package crazypants.enderio.conduits.conduit.liquid;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduits.conduit.AbstractItemConduit;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.conduits.lang.Lang;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLiquidConduit extends AbstractItemConduit implements IAdvancedTooltipProvider {

  public static ItemLiquidConduit create(@Nonnull IModObject modObject) {
    return new ItemLiquidConduit(modObject);
  }

  protected ItemLiquidConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
        new ItemConduitSubtype(modObject.getUnlocalisedName() + "_advanced", modObject.getRegistryName().toString() + "_advanced"),
        new ItemConduitSubtype(modObject.getUnlocalisedName() + "_ender", modObject.getRegistryName().toString() + "_ender"));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "fluid")).setClass(getBaseConduitType())
        .setOffsets(Offset.WEST, Offset.NORTH, Offset.WEST, Offset.WEST).build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "liquid_conduit"))
        .setClass(LiquidConduit.class).build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "advanced_liquid_conduit")).setClass(AdvancedLiquidConduit.class)
        .build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "ender_liquid_conduit")).setClass(EnderLiquidConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_FLUID, IconEIO.WRENCH_OVERLAY_FLUID_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(LiquidConduitRenderer.create());
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new AdvancedLiquidConduitRenderer());
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new EnderLiquidConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return ILiquidConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
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

    String mbt = " " + Lang.FLUID_MILLIBUCKETS_TICK.get();
    list.add(Lang.GUI_LIQUID_TOOLTIP_MAX_EXTRACT.get() + " " + extractRate + mbt);
    list.add(Lang.GUI_LIQUID_TOOLTIP_MAX_IO.get() + " " + maxIo + mbt);

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

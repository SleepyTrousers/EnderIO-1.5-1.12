package crazypants.enderio.conduits.refinedstorage.conduit;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.conduit.item.AbstractItemConduit;
import crazypants.enderio.conduits.refinedstorage.conduit.gui.IconRS;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemRefinedStorageConduit extends AbstractItemConduit {

  public static ItemRefinedStorageConduit create(@Nonnull IModObject modObject) {
    return new ItemRefinedStorageConduit(modObject);
  }

  protected ItemRefinedStorageConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "refinedstorage")).setClass(getBaseConduitType())
        .setOffsets(Offset.EAST_DOWN, Offset.SOUTH_DOWN, Offset.EAST_DOWN, Offset.EAST_DOWN).build()
        .setUUID(new ResourceLocation(EnderIO.DOMAIN, "refinedstorage_conduit")).setClass(RefinedStorageConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconRS.WRENCH_OVERLAY_RS, IconRS.WRENCH_OVERLAY_RS_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new DefaultConduitRenderer());
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IRefinedStorageConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
    return new RefinedStorageConduit();
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

}

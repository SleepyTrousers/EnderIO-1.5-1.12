package crazypants.enderio.conduits.refinedstorage.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemRefinedStorageConduit extends AbstractItemConduit {

  public static ItemRefinedStorageConduit create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemRefinedStorageConduit(modObject);
  }

  protected ItemRefinedStorageConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "refinedstorage")).setClass(getBaseConduitType())
        .setOffsets(Offset.WEST_UP, Offset.NORTH_UP, Offset.NORTH_WEST, Offset.WEST_UP).build()
        .setUUID(new ResourceLocation(EnderIO.DOMAIN, "refinedstorage_conduit")).setClass(RefinedStorageConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_RS, IconEIO.WRENCH_OVERLAY_RS_OFF));
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

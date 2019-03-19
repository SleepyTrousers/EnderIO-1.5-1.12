package crazypants.enderio.invpanel.conduit.data;

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

public class ItemDataConduit extends AbstractItemConduit {

  public static ItemDataConduit create(@Nonnull IModObject modObj, @Nullable Block block) {
    return new ItemDataConduit(modObj);
  }

  public ItemDataConduit(@Nonnull IModObject modObj) {
    super(modObj, new ItemConduitSubtype(modObj.getUnlocalisedName(), modObj.getRegistryName().toString()));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "data")).setClass(getBaseConduitType())
        .setOffsets(Offset.NONE, Offset.NONE, Offset.NONE, Offset.NONE).build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "data_conduit"))
        .setClass(DataConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_DATA, IconEIO.WRENCH_OVERLAY_DATA_OFF));
  }

  @Override
  @Nonnull
  public Class<? extends IConduit> getBaseConduitType() {
    return IDataConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
    return new DataConduit();
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

}

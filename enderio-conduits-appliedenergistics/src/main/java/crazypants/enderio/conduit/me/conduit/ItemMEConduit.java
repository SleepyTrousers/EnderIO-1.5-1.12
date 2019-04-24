package crazypants.enderio.conduit.me.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import appeng.api.AEApi;
import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.conduit.me.MEUtil;
import crazypants.enderio.conduits.conduit.AbstractItemConduit;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemMEConduit extends AbstractItemConduit {

  public static ItemMEConduit create(@Nonnull IModObject modObject, @Nullable Block block) {
    if (MEUtil.isMEEnabled()) {
      return new ItemMEConduit(modObject);
    }
    return null;
  }

  protected ItemMEConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
        new ItemConduitSubtype(modObject.getUnlocalisedName() + "_dense", modObject.getRegistryName().toString() + "_dense"));

    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "appliedenergistics")).setClass(getBaseConduitType())
        .setOffsets(Offset.EAST_UP, Offset.SOUTH_UP, Offset.NORTH_EAST, Offset.EAST_UP).build()
        .setUUID(new ResourceLocation(EnderIO.DOMAIN, "appliedenergistics_conduit")).setClass(MEConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ME, IconEIO.WRENCH_OVERLAY_ME_OFF));
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IMEConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
    MEConduit con = new MEConduit(item.getItemDamage());
    con.setPlayerID(AEApi.instance().registries().players().getID(player));
    return con;
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }

}

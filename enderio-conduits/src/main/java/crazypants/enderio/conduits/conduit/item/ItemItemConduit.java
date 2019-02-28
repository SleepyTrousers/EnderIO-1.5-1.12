package crazypants.enderio.conduits.conduit.item;

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
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemItemConduit extends AbstractItemConduit {

  public static ItemItemConduit create(@Nonnull IModObject modObject, @Nullable Block block) {
    return new ItemItemConduit(modObject);
  }

  protected ItemItemConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "items")).setClass(getBaseConduitType())
        .setOffsets(Offset.EAST, Offset.SOUTH, Offset.EAST, Offset.EAST).build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "item_conduit"))
        .setClass(ItemConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_ITEM, IconEIO.WRENCH_OVERLAY_ITEM_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new ItemConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
    return new ItemConduit(item.getItemDamage());
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }
}

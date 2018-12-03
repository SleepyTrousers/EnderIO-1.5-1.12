package crazypants.enderio.conduits.oc.conduit;

import javax.annotation.Nonnull;

import crazypants.enderio.api.IModObject;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.IServerConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitBuilder;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.conduits.conduit.AbstractItemConduit;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.oc.OCUtil;
import crazypants.enderio.conduits.oc.init.ConduitOpenComputersObject;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import crazypants.enderio.conduits.render.DefaultConduitRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemOCConduit extends AbstractItemConduit {

  private static ItemConduitSubtype[] subtypes = new ItemConduitSubtype[] { new ItemConduitSubtype(ConduitOpenComputersObject.item_opencomputers_conduit.name(), "enderio:item_oc_conduit") };

  public static ItemOCConduit create(@Nonnull IModObject mo) {
    if (OCUtil.isOCEnabled()) {
      return new ItemOCConduit(mo);
    }
    return null;
  }

  protected ItemOCConduit(@Nonnull IModObject mo) {
    super(mo, subtypes);
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "opencomputers")).setClass(getBaseConduitType())
            .setOffsets(Offset.WEST_DOWN, Offset.NORTH_DOWN, Offset.SOUTH_WEST, Offset.WEST_DOWN).build()
            .setUUID(new ResourceLocation(EnderIO.DOMAIN, "opencomputers_conduit")).setClass(OCConduit.class).build().finish());
        ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_OC, IconEIO.WRENCH_OVERLAY_OC_OFF));
  }
  
  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IOCConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack item, @Nonnull EntityPlayer player) {
    OCConduit con = new OCConduit(item.getItemDamage());
    return con;
  }
  
  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }
}

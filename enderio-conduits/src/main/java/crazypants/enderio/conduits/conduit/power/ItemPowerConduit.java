package crazypants.enderio.conduits.conduit.power;

import java.util.List;

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
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.conduits.conduit.AbstractItemConduit;
import crazypants.enderio.conduits.conduit.ItemConduitSubtype;
import crazypants.enderio.conduits.render.ConduitBundleRenderManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPowerConduit extends AbstractItemConduit {

  public static ItemPowerConduit create(@Nonnull IModObject modObject) {
    ItemPowerConduit result = new ItemPowerConduit(modObject);
    return result;
  }

  protected ItemPowerConduit(@Nonnull IModObject modObject) {
    super(modObject, new ItemConduitSubtype(modObject.getUnlocalisedName(), modObject.getRegistryName().toString()),
        new ItemConduitSubtype(modObject.getUnlocalisedName() + "_enhanced", modObject.getRegistryName().toString() + "_enhanced"),
        new ItemConduitSubtype(modObject.getUnlocalisedName() + "_ender", modObject.getRegistryName().toString() + "_ender"));
    ConduitRegistry.register(ConduitBuilder.start().setUUID(new ResourceLocation(EnderIO.DOMAIN, "power")).setClass(getBaseConduitType())
        .setOffsets(Offset.DOWN, Offset.DOWN, Offset.SOUTH, Offset.DOWN).build().setUUID(new ResourceLocation(EnderIO.DOMAIN, "power_conduit"))
        .setClass(PowerConduit.class).build().finish());
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_POWER, IconEIO.WRENCH_OVERLAY_POWER_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    ConduitBundleRenderManager.instance.getConduitBundleRenderer().registerRenderer(new PowerConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  @Override
  public IServerConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return new PowerConduit(IPowerConduitData.Registry.fromID(stack.getItemDamage()));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack itemStack, @Nullable World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
    String prefix = EnderIO.lang.localize("power.max_output") + " ";
    super.addInformation(itemStack, world, list, flag);
    int cap = PowerConduit.getMaxEnergyIO(IPowerConduitData.Registry.fromID(itemStack.getItemDamage()));
    list.add(prefix + LangPower.RFt(cap));
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }
}

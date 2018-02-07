package crazypants.enderio.conduit.power;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.api.tool.ITool;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.init.IModObject;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.conduit.ItemConduitSubtype;
import crazypants.enderio.conduit.init.ConduitObject;
import crazypants.enderio.conduit.item.AbstractItemConduit;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPowerConduit extends AbstractItemConduit {

  static ItemConduitSubtype[] SUBTYPES = new ItemConduitSubtype[] { new ItemConduitSubtype(ConduitObject.item_power_conduit.name(), "enderio:itemPowerConduit"),
      new ItemConduitSubtype(ConduitObject.item_power_conduit.name() + "_enhanced", "enderio:itemPowerConduitEnhanced"),
      new ItemConduitSubtype(ConduitObject.item_power_conduit.name() + "_ender", "enderio:itemPowerConduitEnder") };

  private final ConduitRegistry.ConduitInfo conduitInfo;

  public static ItemPowerConduit create(@Nonnull IModObject modObject) {
    ItemPowerConduit result = new ItemPowerConduit(modObject);
    return result;
  }

  protected ItemPowerConduit(@Nonnull IModObject modObject) {
    super(modObject, SUBTYPES);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.DOWN, Offset.DOWN, Offset.SOUTH, Offset.DOWN);
    conduitInfo.addMember(PowerConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_POWER, IconEIO.WRENCH_OVERLAY_POWER_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers(@Nonnull IModObject modObject) {
    super.registerRenderers(modObject);
    conduitInfo.addRenderer(new PowerConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  @Override
  public IConduit createConduit(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return new PowerConduit(stack.getItemDamage());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(@Nonnull ItemStack itemStack, @Nullable World world, @Nonnull List<String> list, @Nonnull ITooltipFlag flag) {
    String prefix = EnderIO.lang.localize("power.max_output") + " ";
    super.addInformation(itemStack, world, list, flag);
    int cap = PowerConduit.getMaxEnergyIO(itemStack.getMetadata());
    list.add(prefix + LangPower.RFt(cap));
  }

  @Override
  public boolean shouldHideFacades(@Nonnull ItemStack stack, @Nonnull EntityPlayer player) {
    return true;
  }
}

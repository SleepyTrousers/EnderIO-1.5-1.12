package crazypants.enderio.conduit.power;

import java.util.List;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.ModObject;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.conduit.geom.Offset;
import crazypants.enderio.base.conduit.registry.ConduitRegistry;
import crazypants.enderio.base.gui.IconEIO;
import crazypants.enderio.base.lang.LangPower;
import crazypants.enderio.conduit.AbstractItemConduit;
import crazypants.enderio.conduit.ItemConduitSubtype;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPowerConduit extends AbstractItemConduit {

  private static String PREFIX;
  private static String POSTFIX;

  static ItemConduitSubtype[] SUBTYPES = new ItemConduitSubtype[] {
      new ItemConduitSubtype(ModObject.itemPowerConduit.name(), "enderio:itemPowerConduit"),
      new ItemConduitSubtype(ModObject.itemPowerConduit.name() + "Enhanced", "enderio:itemPowerConduitEnhanced"),
      new ItemConduitSubtype(ModObject.itemPowerConduit.name() + "Ender", "enderio:itemPowerConduitEnder")
  };

  private final ConduitRegistry.ConduitInfo conduitInfo;

  public static ItemPowerConduit create() {
    ItemPowerConduit result = new ItemPowerConduit();
    result.init();
    return result;
  }

  protected ItemPowerConduit() {
    super(ModObject.itemPowerConduit, SUBTYPES);
    conduitInfo = new ConduitRegistry.ConduitInfo(getBaseConduitType(), Offset.DOWN, Offset.DOWN, Offset.SOUTH, Offset.DOWN);
    conduitInfo.addMember(PowerConduit.class);
    ConduitRegistry.register(conduitInfo);
    ConduitDisplayMode.registerDisplayMode(new ConduitDisplayMode(getBaseConduitType(), IconEIO.WRENCH_OVERLAY_POWER, IconEIO.WRENCH_OVERLAY_POWER_OFF));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    super.registerRenderers();
    conduitInfo.addRenderer(new PowerConduitRenderer());
  }

  @Override
  public @Nonnull Class<? extends IConduit> getBaseConduitType() {
    return IPowerConduit.class;
  }

  @Override
  public IConduit createConduit(ItemStack stack, EntityPlayer player) {
    return new PowerConduit(stack.getItemDamage());
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack itemStack, EntityPlayer par2EntityPlayer, List<String> list, boolean par4) {
    if(PREFIX == null) {
      PREFIX = EnderIO.lang.localize("power.maxOutput") + " ";
    }
    super.addInformation(itemStack, par2EntityPlayer, list, par4);
    int cap = PowerConduit.getMaxEnergyIO(itemStack.getMetadata());
    list.add(PREFIX + LangPower.RFt(cap));
  }
  
  @Override
  public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
    return true;
  }
}

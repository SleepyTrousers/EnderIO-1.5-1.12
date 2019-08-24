package crazypants.enderio.base.item.darksteel.upgrade.flippers;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;

import crazypants.enderio.api.upgrades.IDarkSteelUpgrade;
import crazypants.enderio.api.upgrades.IRule;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.handler.darksteel.AbstractUpgrade;
import crazypants.enderio.base.handler.darksteel.Rules;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = EnderIO.MODID)
public class SwimUpgrade extends AbstractUpgrade {

  private static final @Nonnull String UPGRADE_NAME = "swim";
  private static final @Nonnull UUID UPGRADE_UUID = UUID.nameUUIDFromBytes(UPGRADE_NAME.getBytes(Charsets.UTF_8));

  public static final @Nonnull SwimUpgrade INSTANCE = new SwimUpgrade();

  private static final @Nonnull AttributeModifier BOOST = new AttributeModifier(UPGRADE_UUID, UPGRADE_NAME, DarkSteelConfig.swimSpeed.get(),
      Constants.AttributeModifierOperation.ADD) {
    @Override
    public double getAmount() {
      return DarkSteelConfig.swimSpeed.get();
    }
  };

  @SubscribeEvent
  public static void registerDarkSteelUpgrades(@Nonnull RegistryEvent.Register<IDarkSteelUpgrade> event) {
    event.getRegistry().register(INSTANCE);
  }

  public SwimUpgrade() {
    super(UPGRADE_NAME, "enderio.darksteel.upgrade.swim", DarkSteelConfig.swimCost);
  }

  @Override
  @Nonnull
  public List<IRule> getRules() {
    return new NNList<>(Rules.forSlot(EntityEquipmentSlot.FEET), Rules.itemTypeTooltip(EntityEquipmentSlot.FEET));
  }

  @Override
  public void addAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack, @Nonnull Multimap<String, AttributeModifier> map) {
    map.put(EntityLivingBase.SWIM_SPEED.getName(), BOOST);
  }

}

package crazypants.enderio.base.potion;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@EventBusSubscriber(modid = EnderIO.MODID)
public class PotionFloating {

  private static final @Nonnull String NAME = "floating";
  private static final @Nonnull String PREFIX_STRONG = "strong_";
  private static final @Nonnull String PREFIX_LONG = "long_";

  private static final @Nonnull PotionType floating = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.LEVITATION, 7 * 20));
  private static final @Nonnull PotionType floatingStrong = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.LEVITATION, 7 * 20, 2));
  private static final @Nonnull PotionType floatingLong = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.LEVITATION, 28 * 20));

  @SubscribeEvent
  public static void register(Register<PotionType> event) {
    IForgeRegistry<PotionType> reg = event.getRegistry();

    reg.register(floating.setRegistryName(EnderIO.MODID, NAME));
    reg.register(floatingStrong.setRegistryName(EnderIO.MODID, PREFIX_STRONG + NAME));
    reg.register(floatingLong.setRegistryName(EnderIO.MODID, PREFIX_LONG + NAME));

    PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromItem(Items.SHULKER_SHELL), floating);
    PotionHelper.addMix(floating, Ingredient.fromItem(Items.GLOWSTONE_DUST), floatingStrong);
    PotionHelper.addMix(floating, Ingredient.fromItem(Items.REDSTONE), floatingLong);
  }

  public static @Nonnull PotionType getFloating() {
    return floating;
  }

  public static @Nonnull PotionType getFloatingstrong() {
    return floatingStrong;
  }

  public static @Nonnull PotionType getFloatinglong() {
    return floatingLong;
  }

}

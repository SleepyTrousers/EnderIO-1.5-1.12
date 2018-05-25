package crazypants.enderio.base.potion;

import javax.annotation.Nonnull;

import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.material.material.Material;
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
public class PotionWithering {

  private static final @Nonnull String NAME = "withering";
  private static final @Nonnull String PREFIX_LONG = "long_";

  private static final @Nonnull PotionType withering = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.WITHER, 900));
  private static final @Nonnull PotionType witheringLong = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.WITHER, 2400));

  @SubscribeEvent
  public static void register(Register<PotionType> event) {
    IForgeRegistry<PotionType> reg = event.getRegistry();

    reg.register(withering.setRegistryName(EnderIO.MODID, NAME));
    reg.register(witheringLong.setRegistryName(EnderIO.MODID, PREFIX_LONG + NAME));

    PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(Material.POWDER_WITHERING.getStack()), withering);
    PotionHelper.addMix(withering, Ingredient.fromItem(Items.REDSTONE), witheringLong);
  }

  public static @Nonnull PotionType getWithering() {
    return withering;
  }

  public static @Nonnull PotionType getWitheringlong() {
    return witheringLong;
  }

}

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
public class PotionConfusion {

  private static final @Nonnull String NAME = "confusion";
  private static final @Nonnull String PREFIX_LONG = "long_";

  private static final @Nonnull PotionType confusion = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.NAUSEA, 900));
  private static final @Nonnull PotionType confusionLong = new PotionType(EnderIO.MODID + "." + NAME, new PotionEffect(MobEffects.NAUSEA, 2400));

  @SubscribeEvent
  public static void register(Register<PotionType> event) {
    IForgeRegistry<PotionType> reg = event.getRegistry();

    reg.register(confusion.setRegistryName(EnderIO.MODID, NAME));
    reg.register(confusionLong.setRegistryName(EnderIO.MODID, PREFIX_LONG + NAME));

    PotionHelper.addMix(PotionTypes.AWKWARD, Ingredient.fromStacks(Material.POWDER_CONFUSION.getStack()), confusion);
    PotionHelper.addMix(confusion, Ingredient.fromItem(Items.REDSTONE), confusionLong);
  }

  public static @Nonnull PotionType getConfusion() {
    return confusion;
  }

  public static @Nonnull PotionType getConfusionlong() {
    return confusionLong;
  }

}

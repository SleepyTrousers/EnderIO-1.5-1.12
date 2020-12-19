package crazypants.enderio.base.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import crazypants.enderio.api.capacitor.ICapacitorKey;
import crazypants.enderio.base.EnderIO;
import crazypants.enderio.base.capacitor.CapacitorKeyRegistry;
import crazypants.enderio.base.events.EnderIOLifecycleEvent;
import crazypants.enderio.gui.GuiMain;
import crazypants.enderio.gui.gamedata.GameLocation;
import crazypants.enderio.gui.gamedata.ValueRepository;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.OreDictionary.OreRegisterEvent;

@EventBusSubscriber(value = Side.CLIENT, modid = EnderIO.MODID)
public class DataWriter {

  private static boolean done = false;

  @SubscribeEvent
  @SuppressWarnings("null")
  static void run(EnderIOLifecycleEvent.PostInit.Post event) {
    GameLocation.setFile(EnderIO.getConfigHandler().getConfigDirectory());

    for (Potion potion : Potion.REGISTRY) {
      ValueRepository.POTIONS.addValue(potion.getRegistryName().toString(), Collections.singletonList(I18n.format(potion.getName())));
    }

    for (ICapacitorKey key : CapacitorKeyRegistry.getAllKeys()) {
      ValueRepository.CAP_KEYS.addValue(key.getRegistryName().toString(), Collections.emptyList());
    }

    for (Item item : Item.REGISTRY) {
      ValueRepository.ITEMS.addValue(item.getRegistryName().toString(), Collections.singletonList(I18n.format(item.getUnlocalizedName() + ".name")));
    }

    for (Block block : Block.REGISTRY) {
      ValueRepository.BLOCKS.addValue(block.getRegistryName().toString(), Collections.singletonList(I18n.format(block.getUnlocalizedName() + ".name")));
    }

    done = true;

    ValueRepository.save();
  }

  @SubscribeEvent
  static void run(OreRegisterEvent event) {
    if (!done) {
      String name = event.getName();
      if (name != null) {
        List<String> list = ValueRepository.OREDICTS.getDescription(name);
        if (list == null) {
          list = new ArrayList<>();
          ValueRepository.OREDICTS.addValue(name, list);
        }
        list.add(item2string(event.getOre()));
      }
    }
  }

  private static String item2string(ItemStack stack) {
    return "item:" + stack.getItem().getRegistryName()
        + (!stack.getItem().isDamageable() && stack.getItem().getHasSubtypes()
            ? stack.getItemDamage() == OreDictionary.WILDCARD_VALUE ? ":*" : ":" + stack.getItemDamage()
            : "");
  }

  public static void run() {
    try {
      GuiMain.run(EnderIO.getConfigHandler().getConfigDirectory().getCanonicalPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}

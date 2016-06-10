package crazypants.enderio;

import java.util.ArrayList;
import java.util.List;

import crazypants.enderio.capacitor.LootSelector;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.material.Alloy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LootManager {

  private static LootManager INSTANCE = new LootManager();

  public static void register() {
    MinecraftForge.EVENT_BUS.register(INSTANCE);
  }

  private LootManager() {
  }

  private LootCondition[] noConditions = new LootCondition[0];

  @SubscribeEvent
  public void onLootTableLoad(LootTableLoadEvent evt) {

    LootTable table = evt.getTable();

    if (evt.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {

      List<LootEntry> entries = new ArrayList<LootEntry>();
      if (Config.lootDarkSteel) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.DARK_STEEL.ordinal(), 1, 3, 15));
      }
      if (Config.lootItemConduitProbe) {
        entries.add(createLootEntry(EnderIO.itemConduitProbe, 1, 1, 10));
      }
      if (Config.lootQuartz) {
        entries.add(createLootEntry(Items.QUARTZ, 3, 16, 20));
      }
      if (Config.lootNetherWart) {
        entries.add(createLootEntry(Items.NETHER_WART, 1, 4, 10));
      }
      if (Config.lootEnderPearl) {
        entries.add(createLootEntry(Items.ENDER_PEARL, 1, 2, 30));
      }
      if(Config.lootTheEnder) {
        entries.add(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1,1, 5));
      }
      if(Config.lootDarkSteelBoots) {
        entries.add(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 1,1, 5));
      }
      entries.add(createLootCapacitor(15));
      if (entries.isEmpty()) {
        return;
      }
      int minRolls = Math.min(2, Math.max(0, entries.size() - 2));
      int maxRolls = Math.min(entries.size() , entries.size() - 2);
      LootPool lp = createPool(minRolls, maxRolls);
      for (LootEntry entry : entries) {
        lp.addEntry(entry);
      }
      table.addPool(lp);

    } else if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
      
      List<LootEntry> entries = new ArrayList<LootEntry>();
      if (Config.lootElectricSteel) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.ELECTRICAL_STEEL.ordinal(), 2, 6, 20));
      }
      if (Config.lootRedstoneAlloy) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.REDSTONE_ALLOY.ordinal(), 3, 6, 35));
      }
      if (Config.lootDarkSteel) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.DARK_STEEL.ordinal(), 3, 6, 35));
      }
      if (Config.lootPhasedIron) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.PULSATING_IRON.ordinal(), 1, 2, 10));
      }
      if (Config.lootPhasedGold) {
        entries.add(createLootEntry(EnderIO.itemAlloy, Alloy.VIBRANT_ALLOY.ordinal(), 1, 2, 5));
      }
      if(Config.lootTheEnder) {
        entries.add(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1,1, 5));
      }
      if(Config.lootDarkSteelBoots) {
        entries.add(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 1,1, 5));
      }
      entries.add(createLootCapacitor(5));

      if (entries.isEmpty()) {
        return;
      }
      int minRolls = Math.min(2, Math.max(0, entries.size() - 2));
      int maxRolls = Math.min(entries.size() , entries.size() - 2);
      LootPool lp = createPool(minRolls, maxRolls);
      for (LootEntry entry : entries) {
        lp.addEntry(entry);
      }
      table.addPool(lp);
    } else if (evt.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {
      LootPool mainPool = table.getPool("main");
      if(mainPool == null) {
        Log.error("LootManager.onLootTableLoad Could not add to the main loot pool of CHESTS_DESERT_PYRAMID");
        return;
      }
      if(Config.lootTheEnder) {               
        mainPool.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1,1, 15));        
      }
      if(Config.lootTravelStaff) {
        mainPool.addEntry(createLootEntry(EnderIO.itemTravelStaff, 1,1, 1));
      }
      mainPool.addEntry(createLootCapacitor(25));
    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
      LootPool mainPool = table.getPool("main");
      if(mainPool == null) {
        Log.error("LootManager.onLootTableLoad Could not add to the main loot pool of CHESTS_JUNGLE_TEMPLE");
        return;
      }
      if(Config.lootTheEnder) {       
        mainPool.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1,1, 15));        
      }
      if(Config.lootTravelStaff) {
        mainPool.addEntry(createLootEntry(EnderIO.itemTravelStaff, 1,1, 1));
      }
      mainPool.addEntry(createLootCapacitor(15));
    } 

  }

  private LootPool createPool(int minRolls, int maxRolls) {
    RandomValueRange bonusRollsIn = new RandomValueRange(0, 0);
    RandomValueRange rollsIn = new RandomValueRange(minRolls, maxRolls);
    return new LootPool(new LootEntry[0], noConditions, rollsIn, bonusRollsIn, EnderIO.MOD_NAME);
  }

  private LootEntryItem createLootEntry(Item item, float minStackSize, float maxStackSize, int weight) {
    LootFunction[] functionsIn = new LootFunction[] { new SetCount(noConditions, new RandomValueRange(minStackSize, maxStackSize)) };
    return new LootEntryItem(item, weight, 1, functionsIn, noConditions, item.getUnlocalizedName());
  }

  private LootEntryItem createLootEntry(Item item, int meta, float minStackSize, float maxStackSize, int weight) {
    LootFunction[] functionsIn = new LootFunction[] { new SetCount(noConditions, new RandomValueRange(minStackSize, maxStackSize)),
        new SetMetadata(noConditions, new RandomValueRange(meta, meta))

    };
    return new LootEntryItem(item, weight, 1, functionsIn, noConditions, item.getUnlocalizedName() + ":" + meta);
  }

  private LootEntryItem createLootCapacitor(int weight) {
    LootFunction[] functionsIn = new LootFunction[] { new SetCount(noConditions, new RandomValueRange(1, 1)), new LootSelector(noConditions) };
    return new LootEntryItem(EnderIO.itemBasicCapacitor, weight, 1, functionsIn, noConditions, EnderIO.itemBasicCapacitor.getUnlocalizedName());
  }

}

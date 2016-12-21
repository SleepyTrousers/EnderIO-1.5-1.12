package crazypants.enderio;

import crazypants.enderio.capacitor.LootSelector;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.material.Alloy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.functions.*;
import net.minecraft.world.storage.loot.conditions.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static crazypants.enderio.ModObject.itemAlloy;
import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemConduitProbe;
import static crazypants.enderio.ModObject.itemTravelStaff;

public class LootManager {

//Add this code to an item (e.g. ItemAlloy) to easily test generation of loot
//@Override
//public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ,
//    EnumHand hand) {
//
//  if (world.isRemote) {
//    return EnumActionResult.PASS;
//  }
//  TileEntity te = world.getTileEntity(pos);
//  if (!(te instanceof TileEntityChest)) {
//    return EnumActionResult.PASS;
//  }
//  TileEntityChest chest = (TileEntityChest) te;
//  chest.clear();
//
//  LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
//  if (player != null) {
//    lootcontext$builder.withLuck(player.getLuck());
//  }
//
////  LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
//  LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
//  loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
//  return EnumActionResult.PASS;
//}

  private static final LootCondition[] NO_CONDITIONS = new LootCondition[0];
  private static LootManager INSTANCE = new LootManager();

  public static void register() {
    MinecraftForge.EVENT_BUS.register(INSTANCE);
  }

  private LootManager() {
  }

  @SubscribeEvent
  public void onLootTableLoad(LootTableLoadEvent evt) {

    LootTable table = evt.getTable();

    LootPool lp = new LootPool(new LootEntry[0], NO_CONDITIONS, new RandomValueRange(1, 3), new RandomValueRange(0, 0), EnderIO.MOD_NAME);

    if (evt.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {

        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 1, 3, 25, Config.lootDarkSteel));
        lp.addEntry(createLootEntry(itemConduitProbe.getItem(), 10, Config.lootItemConduitProbe));
        lp.addEntry(createLootEntry(Items.QUARTZ, 3, 16, 25, Config.lootQuartz));
        lp.addEntry(createLootEntry(Items.NETHER_WART, 1, 4, 20, Config.lootNetherWart));
        lp.addEntry(createLootEntry(Items.ENDER_PEARL, 1, 2, 30, Config.lootEnderPearl));
        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 10, Config.lootTheEnder));
        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 10, Config.lootDarkSteelBoots));
        lp.addEntry(createLootCapacitor(15, true));
        lp.addEntry(createLootCapacitor(15, true));
        lp.addEntry(createLootCapacitor(15, true));
    } else if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {
    	
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.ELECTRICAL_STEEL.ordinal(), 2, 6, 20, Config.lootElectricSteel));
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.REDSTONE_ALLOY.ordinal(), 3, 6, 35, Config.lootRedstoneAlloy));
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 3, 6, 35, Config.lootDarkSteel));
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.PULSATING_IRON.ordinal(), 1, 2, 30, Config.lootPhasedIron));
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.VIBRANT_ALLOY.ordinal(), 1, 2, 20, Config.lootPhasedGold));
        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 25, Config.lootTheEnder));
        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 1, 1, 25, Config.lootDarkSteelBoots));
        lp.addEntry(createLootCapacitor(10, true));
    } else if (evt.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {

        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 20, Config.lootTheEnder));
        lp.addEntry(createLootEntry(itemTravelStaff.getItem(), 10, Config.lootTravelStaff));
        lp.addEntry(createLootCapacitor(25, true));
    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {

        lp.addEntry(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 25, Config.lootTheEnder));
        lp.addEntry(createLootEntry(itemTravelStaff.getItem(), 1, 1, 10, Config.lootTravelStaff));
        lp.addEntry(createLootCapacitor(25, true));
      	lp.addEntry(createLootCapacitor(25, true));
    }
    table.addPool(lp); 
  }
  
  //Each loot entry in a pool must have a unique name
  private int emptyCount = 0;
  private LootEntry createLootEntry(Item item, int chance, boolean enabled) {
	  return createLootEntry(item, 1, 1, chance, enabled);
  }

  private LootEntry createLootEntry(Item item, int minSize, int maxSize, int chance, boolean enabled) {
    return createLootEntry(item, 0, minSize, maxSize, chance, enabled);
  }

  /**If enabled is false, an empty loot entry of the same weight is added.
   *This maintains the generation probabilities of other EIO loot.
   * Without this, disabling loot in the config would change the generation probabilities of other loot. 
   **/
  private LootEntry createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, int chance, boolean enabled) {
	  if(!enabled) {
		  emptyCount++;
		  return new LootEntryEmpty(chance, 1, NO_CONDITIONS, "empty" + emptyCount);
	  }
	  if(item.isDamageable()) {
		  return new LootEntryItem(item, 1, 1, new LootFunction[]{setCount(minStackSize, maxStackSize), setDamage(ordinal)}, NO_CONDITIONS, item.getRegistryName().toString() + ":" + ordinal);
	  }
	  else {
		  return new LootEntryItem(item, 1, 1, new LootFunction[]{setCount(minStackSize, maxStackSize), setMetadata(ordinal)}, NO_CONDITIONS, item.getRegistryName().toString() + "|empty:" + ordinal);  
	  }
  }
  
  int capCount = 0; //Each loot entry in a pool must have a unique name
  /**If enabled is false an empty loot entry of the same weight is added.
  *This maintains the generation probabilities of other EIO loot.
  * Without this, disabling loot in the config would change the generation probabilities of other loot. 
  **/
  private LootEntry createLootCapacitor(int weight, boolean enabled) {
	  if(!enabled) {
		  return new LootEntryEmpty(weight, 1, NO_CONDITIONS, "empty" + emptyCount);
		  
	  }
	  capCount++;
	  return new LootEntryItem(itemBasicCapacitor.getItem(), weight, 1, new LootFunction[]{ls, setMetadata(3)}, NO_CONDITIONS, itemBasicCapacitor.getItem().getRegistryName().toString() + capCount);
  }
	
  private SetCount setCount(int min, int max) {
	  return new SetCount(NO_CONDITIONS, new RandomValueRange(min, min));
  }
  
  private SetDamage setDamage(int damage) {
	  return new SetDamage(NO_CONDITIONS, new RandomValueRange(damage));
  }
  
  private SetMetadata setMetadata(int meta) {
	  return new SetMetadata(NO_CONDITIONS, new RandomValueRange(meta));
  }

  private static LootSelector ls = new LootSelector(NO_CONDITIONS);
}

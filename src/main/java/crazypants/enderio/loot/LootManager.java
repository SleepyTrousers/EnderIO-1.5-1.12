package crazypants.enderio.loot;

import crazypants.enderio.EnderIO;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.fluid.Buckets;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.material.Alloy;
import crazypants.util.CapturedMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.RandomChance;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetMetadata;
import net.minecraft.world.storage.loot.functions.SetNBT;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static crazypants.enderio.ModObject.itemAlloy;
import static crazypants.enderio.ModObject.itemBasicCapacitor;
import static crazypants.enderio.ModObject.itemConduitProbe;
import static crazypants.enderio.ModObject.itemTravelStaff;

public class LootManager {

  // Add this code to an item (e.g. ItemAlloy) to easily test generation of loot
  // @Override
  // public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float
  // hitZ,
  // EnumHand hand) {
  //
  // if (world.isRemote) {
  // return EnumActionResult.PASS;
  // }
  // TileEntity te = world.getTileEntity(pos);
  // if (!(te instanceof TileEntityChest)) {
  // return EnumActionResult.PASS;
  // }
  // TileEntityChest chest = (TileEntityChest) te;
  // chest.clear();
  //
  // LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) world);
  // if (player != null) {
  // lootcontext$builder.withLuck(player.getLuck());
  // }
  //
  //// LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
  // LootTable loottable = world.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_VILLAGE_BLACKSMITH);
  // loottable.fillInventory(chest, world.rand, lootcontext$builder.build());
  // return EnumActionResult.PASS;
  // }

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

      if (Config.lootDarkSteel) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 1, 3, 0.25F));
      }
      if (Config.lootItemConduitProbe) {
        lp.addEntry(createLootEntry(itemConduitProbe.getItem(), 0.10F));
      }
      if (Config.lootQuartz) {
        lp.addEntry(createLootEntry(Items.QUARTZ, 3, 16, 0.25F));
      }
      if (Config.lootNetherWart) {
        lp.addEntry(createLootEntry(Items.NETHER_WART, 1, 4, 0.20F));
      }
      if (Config.lootEnderPearl) {
        lp.addEntry(createLootEntry(Items.ENDER_PEARL, 1, 2, 0.30F));
      }
      if (Config.lootTheEnder) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelSword, 0.1F));
      }
      if (Config.lootDarkSteelBoots) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelBoots, 0.1F));
      }
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.15F));

    } else if (evt.getName().equals(LootTableList.CHESTS_ABANDONED_MINESHAFT)) {

      if (Config.lootDarkSteel) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 1, 3, 0.05F));
      }
      if (Config.lootEnderPearl) {
        lp.addEntry(createLootEntry(Items.ENDER_PEARL, 1, 2, 0.10F));
      }
      if (Config.lootTheEnder) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelSword, 0.2F));
      }
      lp.addEntry(createLootCapacitor(0.15F));
      lp.addEntry(createLootCapacitor(0.05F));

    } else if (evt.getName().equals(LootTableList.CHESTS_NETHER_BRIDGE)) {

      if (Config.lootDarkSteelBoots) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelBoots, 0.1F));
      }
      lp.addEntry(createLootCapacitor(0.15F));

    } else if (evt.getName().equals(LootTableList.CHESTS_IGLOO_CHEST)) {

      final CapturedMob polarBear = CapturedMob.create("PolarBear", null);
      if (polarBear != null) {
        lp.addEntry(
            new LootEntryItem(ModObject.itemSoulVessel.getItem(), 1, 1, new LootFunction[] { setCount(1, 1), new SetNBT(NO_CONDITIONS, polarBear.toNbt(null)) },
                new LootCondition[] { new RandomChance(.2F) }, "PolarBearSoulVial"));
      }
      lp.addEntry(createLootEntry(ModObject.itemSoulVessel.getItem(), 1, 3, 0.5F));
      lp.addEntry(createLootCapacitor(0.05F));

    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE_DISPENSER)) {

      ItemStack bucket = Buckets.itemBucketFireWater.copy();
      lp.addEntry(new LootEntryItem(bucket.getItem(), 1, 1, new LootFunction[] { setCount(1, 1), setMetadata(bucket.getMetadata()), setNBT(bucket) },
          new LootCondition[] { new RandomChance(.05F) }, bucket.getItem().getRegistryName().toString() + ":" + bucket.getMetadata()));

    } else if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {

      if (Config.lootElectricSteel) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.ELECTRICAL_STEEL.ordinal(), 2, 6, 0.20F));
      }
      if (Config.lootRedstoneAlloy) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.REDSTONE_ALLOY.ordinal(), 3, 6, 0.35F));
      }
      if (Config.lootDarkSteel) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 3, 6, 0.35F));
      }
      if (Config.lootPhasedIron) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.PULSATING_IRON.ordinal(), 1, 2, 0.3F));
      }
      if (Config.lootPhasedGold) {
        lp.addEntry(createLootEntry(itemAlloy.getItem(), Alloy.VIBRANT_ALLOY.ordinal(), 1, 2, 0.2F));
      }
      if (Config.lootTheEnder) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 0.25F));
      }
      if (Config.lootDarkSteelBoots) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelBoots, 1, 1, 0.25F));
      }
      lp.addEntry(createLootCapacitor(0.1F));

    } else if (evt.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {

      if (Config.lootTheEnder) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelSword, 0.2F));
      }
      if (Config.lootTravelStaff) {
        lp.addEntry(createLootEntry(itemTravelStaff.getItem(), 0.1F));
      }
      lp.addEntry(createLootCapacitor(25));

    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {

      if (Config.lootTheEnder) {
        lp.addEntry(createDarkSteelLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 0.25F));
      }
      if (Config.lootTravelStaff) {
        lp.addEntry(createLootEntry(itemTravelStaff.getItem(), 1, 1, 0.1F));
      }
      lp.addEntry(createLootCapacitor(0.25F));
      lp.addEntry(createLootCapacitor(0.25F));
    }
    table.addPool(lp);
  }

  private LootEntry createLootEntry(Item item, float chance) {
    return createLootEntry(item, 1, 1, chance);
  }

  private LootEntry createLootEntry(Item item, int minSize, int maxSize, float chance) {
    return createLootEntry(item, 0, minSize, maxSize, chance);
  }

  /*
   * All loot entries are given the same weight, the generation probabilities depend on the RandomChance condition.
   */
  private LootEntry createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, float chance) {
    LootCondition[] chanceCond = new LootCondition[] { new RandomChance(chance) };
    if (item.isDamageable()) {
      return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setDamage(item, ordinal), setEnergy() }, chanceCond,
          item.getRegistryName().toString() + ":" + ordinal);
    } else {
      return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setMetadata(ordinal) }, chanceCond,
          item.getRegistryName().toString() + ":" + ordinal);
    }
  }

  private LootEntry createDarkSteelLootEntry(Item item, float chance) {
    return createDarkSteelLootEntry(item, 1, 1, chance);
  }

  private LootEntry createDarkSteelLootEntry(Item item, int minSize, int maxSize, float chance) {
    return createDarkSteelLootEntry(item, 0, minSize, maxSize, chance);
  }

  private LootEntry createDarkSteelLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, float chance) {
    LootCondition[] chanceCond = new LootCondition[] { new RandomChance(chance) };
    return new LootEntryItem(item, 1, 1, new LootFunction[] { setCount(minStackSize, maxStackSize), setDamage(item, ordinal), setUpgrades(), setEnergy() },
        chanceCond, item.getRegistryName().toString() + ":" + ordinal);
  }

  int capCount = 0; // Each loot entry in a pool must have a unique name

  private LootEntry createLootCapacitor(float chance) {
    capCount++;
    return new LootEntryItem(itemBasicCapacitor.getItem(), 1, 1, new LootFunction[] { ls, setMetadata(3) }, new LootCondition[] { new RandomChance(chance) },
        itemBasicCapacitor.getItem().getRegistryName().toString() + capCount);
  }

  private SetCount setCount(int min, int max) {
    return new SetCount(NO_CONDITIONS, new RandomValueRange(min, min));
  }

  private SetDamage setDamage(Item item, int damage) {
    return new SetDamage(NO_CONDITIONS, new RandomValueRange(damage > 0 ? damage : 1, damage > 0 ? damage : item.getMaxDamage()));
  }

  private SetMetadata setMetadata(int meta) {
    return new SetMetadata(NO_CONDITIONS, new RandomValueRange(meta));
  }

  private SetRandomEnergy setEnergy() {
    return new SetRandomEnergy(NO_CONDITIONS);
  }

  private SetRandomDarkUpgrade setUpgrades() {
    return new SetRandomDarkUpgrade(NO_CONDITIONS);
  }

  private SetNBT setNBT(ItemStack stack) {
    return new SetNBT(NO_CONDITIONS, stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
  }

  private static LootSelector ls = new LootSelector(NO_CONDITIONS);
}

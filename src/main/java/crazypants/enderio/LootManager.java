package crazypants.enderio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import crazypants.enderio.capacitor.LootSelector;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.DarkSteelItems;
import crazypants.enderio.material.Alloy;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
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

  
  private static LootManager INSTANCE = new LootManager();

  public static void register() {
    MinecraftForge.EVENT_BUS.register(INSTANCE);
  }

  private LootManager() {
  }

  @SubscribeEvent
  public void onLootTableLoad(LootTableLoadEvent evt) {

    LootTable table = evt.getTable();

    InnerPool lp = new InnerPool();

    if (evt.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {

      if (Config.lootDarkSteel) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 1, 3, 0.25));
      }
      if (Config.lootItemConduitProbe) {
        lp.addItem(createLootEntry(itemConduitProbe.getItem(), 0.10));
      }
      if (Config.lootQuartz) {
        lp.addItem(createLootEntry(Items.QUARTZ, 3, 16, 0.25));
      }
      if (Config.lootNetherWart) {
        lp.addItem(createLootEntry(Items.NETHER_WART, 1, 4, 0.20));
      }
      if (Config.lootEnderPearl) {
        lp.addItem(createLootEntry(Items.ENDER_PEARL, 1, 2, 0.30));
      }
      if (Config.lootTheEnder) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelSword, 0.1));
      }
      if (Config.lootDarkSteelBoots) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 0.1));
      }
      lp.addItem(createLootCapacitor(0.15));
      lp.addItem(createLootCapacitor(0.15));
      lp.addItem(createLootCapacitor(0.15));

    } else if (evt.getName().equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH)) {

      if (Config.lootElectricSteel) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.ELECTRICAL_STEEL.ordinal(), 2, 6, 0.20));
      }
      if (Config.lootRedstoneAlloy) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.REDSTONE_ALLOY.ordinal(), 3, 6, 0.35));
      }
      if (Config.lootDarkSteel) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.DARK_STEEL.ordinal(), 3, 6, 0.35));
      }
      if (Config.lootPhasedIron) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.PULSATING_IRON.ordinal(), 1, 2, 0.3));
      }
      if (Config.lootPhasedGold) {
        lp.addItem(createLootEntry(itemAlloy.getItem(), Alloy.VIBRANT_ALLOY.ordinal(), 1, 2, 0.2));
      }
      if (Config.lootTheEnder) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 0.25));
      }
      if (Config.lootDarkSteelBoots) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelBoots, 1, 1, 0.25));
      }
      lp.addItem(createLootCapacitor(0.1));

    } else if (evt.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {

      if (Config.lootTheEnder) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelSword, 0.2));
      }
      if (Config.lootTravelStaff) {
        lp.addItem(createLootEntry(itemTravelStaff.getItem(), 0.1));
      }
      lp.addItem(createLootCapacitor(25));

    } else if (evt.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {

      if (Config.lootTheEnder) {
        lp.addItem(createLootEntry(DarkSteelItems.itemDarkSteelSword, 1, 1, 0.25));
      }
      if (Config.lootTravelStaff) {
        lp.addItem(createLootEntry(itemTravelStaff.getItem(), 1, 1, 0.1));
      }
      lp.addItem(createLootCapacitor(0.25));
      lp.addItem(createLootCapacitor(0.25));
    }
    if (!lp.isEmpty()) {
      table.addPool(lp);
    }
    
  }

  private LootItem createLootEntry(Item item, double chance) {
    return new LootItem(new ItemStack(item), chance, 1, 1);
  }

  private LootItem createLootEntry(Item item, int minSize, int maxSize, double chance) {
    return new LootItem(new ItemStack(item), chance, minSize, maxSize);
  }

  private LootItem createLootEntry(Item item, int ordinal, int minStackSize, int maxStackSize, double chance) {
    return new LootItem(new ItemStack(item, 1, ordinal), chance, minStackSize, maxStackSize);
  }

  private LootItem createLootCapacitor(double weight) {
    return new CapItem(weight);
  }


  private static LootSelector ls = new LootSelector(new LootCondition[0]);

  private static class CapItem extends LootItem {

    public CapItem(double chance) {
      super(new ItemStack(itemBasicCapacitor.getItem(), 1, 3), chance);
    }

    @Override
    public ItemStack createStack(Random rnd) {
      ItemStack res = item.copy();
      ls.apply(res, rnd, null);
      return res;
    }
  }

  private static class LootItem {

    ItemStack item;
    double chance;
    int minSize;
    int maxSize;

    public LootItem(ItemStack item, double chance) {
      this(item, chance, 1, 1);
    }

    public LootItem(ItemStack item, double chance, int minSize, int maxSize) {
      this.item = item;
      this.chance = chance;
      this.minSize = minSize;
      this.maxSize = maxSize;
    }

    public ItemStack createStack(Random rnd) {
      int size = minSize;
      if (maxSize > minSize) {
        size += rnd.nextInt(maxSize - minSize + 1);
      }

      ItemStack result = item.copy();
      result.stackSize = size;
      return result;
    }
  }

  private static class InnerPool extends LootPool {

    private final List<LootItem> items = new ArrayList<LootItem>();

    public InnerPool() {
      super(new LootEntry[0], new LootCondition[0], new RandomValueRange(0, 0), new RandomValueRange(0, 0), EnderIO.MOD_NAME);
    }

    public boolean isEmpty() {
      return items.isEmpty();
    }

    public void addItem(LootItem entry) {
      if (entry != null) {
        items.add(entry);
      }

    }

    @Override
    public void generateLoot(Collection<ItemStack> stacks, Random rand, LootContext context) {
      for (LootItem entry : items) {
        if (rand.nextDouble() < entry.chance) {
          ItemStack stack = entry.createStack(rand);
          if (stack != null) {
//            System.out.println("LootManager.InnerPool.generateLoot: Added " + stack.getDisplayName() + " " + stack);
            stacks.add(stack);
          }
        }
      }
    }
  }

}

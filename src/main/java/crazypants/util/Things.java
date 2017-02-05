package crazypants.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;

/**
 * This class is a way to hold lists of configurable items and blocks.
 * <p>
 * It can be fed with item names, items, item stacks, block names, blocks and ore dictionary names.
 * <p>
 * Those can then be used to check if an item, item stack or block is in a list, or to fetch a list in item, item stack or block form.
 * <p>
 * Non-existing things are silently ignored. Adding things in the pre-init phase (i.e. before all modded things exist in the game) is safe.
 *
 */
// TODO 1.11 - use ec version
public class Things {
  private static final Map<String, IThing> aliases = new HashMap<String, IThing>();

  public static final Things TRAVEL_BLACKLIST = new Things(Config.travelStaffBlinkBlackList);

  private static final List<Things> values = new ArrayList<Things>();

  public Things(String... names) {
    for (String string : names) {
      add(string);
    }
    if (inPreInit) {
      values.add(this);
    }
  }

  private static boolean inPreInit = true;

  public static void init(FMLInitializationEvent event) {
    inPreInit = false;
    for (Things element : values) {
      element.bake();
    }
    values.clear();
  }

  private final List<IThing> things = new ArrayList<IThing>();

  public Things add(Item item) {
    if (item != null) {
      add(new ItemThing(item));
    }
    return this;
  }

  public Things add(ItemStack itemStack) {
    if (itemStack != null && itemStack.getItem() != null) {
      add(new ItemStackThing(itemStack));
    }
    return this;
  }

  public Things add(Block block) {
    if (block != null) {
      add(new BlockThing(block));
    }
    return this;
  }

  public Things add(String name) {
    add(new StringThing(name));
    return this;
  }

  public static void addAlias(String name, String value) {
    aliases.put(name, new StringThing(value));
  }

  public Things add(ResourceLocation resourceLocation) {
    add(new ResourceThing(resourceLocation));
    return this;
  }

  public Things addOredict(String name) {
    add(new OreThing(name));
    return this;
  }

  public Things add(Things otherThings) {
    for (IThing thing : otherThings.things) {
      add(thing);
    }
    return this;
  }

  private void add(IThing thing) {
    if (!inPreInit) {
      thing = thing.bake();
    }
    if (thing != null) {
      things.add(thing);
      itemList = null;
      itemStackListRaw = null;
      itemStackList = null;
      blockList = null;
    }
  }

  private void bake() {
    for (int i = 0; i < things.size(); i++) {
      IThing thing = things.get(i);
      IThing bakedThing = thing.bake();
      if (bakedThing != null) {
        things.set(i, bakedThing);
      } else {
        things.remove(i);
        i--;
      }
    }
  }

  public boolean contains(Item item) {
    for (IThing thing : things) {
      if (thing.is(item)) {
        return true;
      }
    }
    return false;
  }

  public boolean contains(ItemStack itemStack) {
    for (IThing thing : things) {
      if (thing.is(itemStack)) {
        return true;
      }
    }
    return false;
  }

  public boolean contains(Block block) {
    for (IThing thing : things) {
      if (thing.is(block)) {
        return true;
      }
    }
    return false;
  }

  public boolean isEmpty() {
    return things.isEmpty();
  }

  private List<Item> itemList = null;
  public List<Item> getItems() {
    if (itemList == null) {
      itemList = new ArrayList<Item>();
      for (IThing thing : things) {
        List<Item> items = thing.getItems();
        if (items != null && !items.isEmpty()) {
          itemList.addAll(items);
        }
      }
    }
    return itemList;
  }

  private List<ItemStack> itemStackListRaw = null;

  public List<ItemStack> getItemStacksRaw() {
    if (itemStackListRaw == null) {
      itemStackListRaw = new ArrayList<ItemStack>();
      for (IThing thing : things) {
        List<ItemStack> itemStacks = thing.getItemStacks();
        if (itemStacks != null && !itemStacks.isEmpty()) {
          itemStackListRaw.addAll(itemStacks);
        }
      }
    }
    return itemStackListRaw;
  }

  private List<ItemStack> itemStackList = null;

  /**
   * Returns a list of item stacks for this Thing. Please note that items that are defined using the wildcard value for the item damage may not return the
   * complete list on standalone servers
   */
  public List<ItemStack> getItemStacks() {
    if (itemStackList == null) {
      itemStackList = new ArrayList<ItemStack>();
      for (ItemStack stack : getItemStacksRaw()) {
        if (stack == null || stack.getItem() == null) {
          // NOP
        } else if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
          EnderIO.proxy.getSubItems(stack.getItem(), EnderIO.proxy.getCreativeTab(stack), itemStackList);
        } else {
          itemStackList.add(stack);
        }
      }
    }
    return itemStackList;
  }

  private List<Block> blockList = null;

  public List<Block> getBlocks() {
    if (blockList == null) {
      blockList = new ArrayList<Block>();
      for (IThing thing : things) {
        List<Block> blocks = thing.getBlocks();
        if (blocks != null && !blocks.isEmpty()) {
          blockList.addAll(thing.getBlocks());
        }
      }
    }
    return blockList;
  }

  public List<Object> getRecipeObjects() {
    List<Object> result = new ArrayList<Object>();
    for (IThing thing : things) {
      Object recipeObject = thing.getRecipeObject();
      if (recipeObject != null) {
        result.add(recipeObject);
      }
    }
    return result;
  }

  private static interface IThing {

    IThing bake();
    
    boolean is(Item item);

    boolean is(ItemStack itemStack);

    boolean is(Block block);

    List<Item> getItems();

    List<ItemStack> getItemStacks();

    List<Block> getBlocks();

    Object getRecipeObject();
  }

  private static class ItemThing implements IThing {

    private final Item item;

    private ItemThing(Item item) {
      this.item = item;
    }

    @Override
    public IThing bake() {
      return item != null ? this : null;
    }

    @Override
    public boolean is(@SuppressWarnings("hiding") Item item) {
      return this.item == item;
    }

    @Override
    public boolean is(ItemStack itemStack) {
      return itemStack != null && itemStack.getItem() == this.item;
    }

    @Override
    public boolean is(Block block) {
      return Item.getItemFromBlock(block) == item || Block.getBlockFromItem(item) == block;
    }

    @Override
    public List<Item> getItems() {
      return Collections.singletonList(item);
    }

    @Override
    public List<ItemStack> getItemStacks() {
      return Collections.singletonList(new ItemStack(item));
    }

    @Override
    public List<Block> getBlocks() {
      Block block = Block.getBlockFromItem(item);
      return block != null && block != Blocks.AIR ? Collections.singletonList(block) : Collections.<Block> emptyList();
    }

    @Override
    public Object getRecipeObject() {
      return item;
    }

  }

  private static class ItemStackThing implements IThing {

    private final ItemStack itemStack;

    private ItemStackThing(ItemStack itemStack) {
      this.itemStack = itemStack;
    }

    @Override
    public IThing bake() {
      return itemStack != null && itemStack.getItem() != null ? this : null;
    }

    @Override
    public boolean is(Item item) {
      return itemStack.getItem() == item;
    }

    @Override
    public boolean is(@SuppressWarnings("hiding") ItemStack itemStack) {
      return itemStack != null && this.itemStack.getItem() == itemStack.getItem()
          && (!this.itemStack.getHasSubtypes() || this.itemStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || this.itemStack.getMetadata() == itemStack
              .getMetadata());
    }

    @Override
    public boolean is(Block block) {
      return Item.getItemFromBlock(block) == itemStack.getItem() || Block.getBlockFromItem(itemStack.getItem()) == block;
    }

    @Override
    public List<Item> getItems() {
      return Collections.singletonList(itemStack.getItem());
    }

    @Override
    public List<ItemStack> getItemStacks() {
      return Collections.singletonList(itemStack);
    }

    @Override
    public List<Block> getBlocks() {
      Block block = Block.getBlockFromItem(itemStack.getItem());
      return block != null && block != Blocks.AIR ? Collections.singletonList(block) : Collections.<Block> emptyList();
    }

    @Override
    public Object getRecipeObject() {
      return itemStack;
    }

  }

  private static class BlockThing implements IThing {

    private final Block block;

    public BlockThing(Block block) {
      this.block = block;
    }

    @Override
    public IThing bake() {
      return block != null ? this : null;
    }

    @Override
    public boolean is(Item item) {
      return Item.getItemFromBlock(block) == item || Block.getBlockFromItem(item) == block;
    }

    @Override
    public boolean is(ItemStack itemStack) {
      return Item.getItemFromBlock(block) == itemStack.getItem() || Block.getBlockFromItem(itemStack.getItem()) == block;
    }

    @Override
    public boolean is(@SuppressWarnings("hiding") Block block) {
      return this.block == block;
    }

    @Override
    public List<Item> getItems() {
      Item item = Item.getItemFromBlock(block);
      return item != null ? Collections.singletonList(item) : null;
    }

    @Override
    public List<ItemStack> getItemStacks() {
      Item item = Item.getItemFromBlock(block);
      return item != null ? Collections.singletonList(new ItemStack(item)) : null;
    }

    @Override
    public List<Block> getBlocks() {
      return Collections.singletonList(block);
    }

    @Override
    public Object getRecipeObject() {
      return block;
    }

  }

  private static class OreThing implements IThing {

    private final String name;
    private List<ItemStack> ores;

    private OreThing(String name) {
      this.name = name;
    }

    @Override
    public IThing bake() {
      if (OreDictionary.doesOreNameExist(name)) {
        ores = OreDictionary.getOres(name);
        if (!ores.isEmpty()) {
          return this;
        }
      }
      return null;
    }

    @Override
    public boolean is(Item item) {
      for (ItemStack oreStack : ores) {
        if (oreStack != null && oreStack.getItem() == item) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean is(ItemStack itemStack) {
      for (ItemStack oreStack : ores) {
        if (itemStack != null && oreStack != null && itemStack.getItem() == oreStack.getItem()
            && (!oreStack.getHasSubtypes() || oreStack.getItemDamage() == OreDictionary.WILDCARD_VALUE || oreStack.getMetadata() == itemStack.getMetadata())) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean is(Block block) {
      for (ItemStack oreStack : ores) {
        if (oreStack != null && (Item.getItemFromBlock(block) == oreStack.getItem() || Block.getBlockFromItem(oreStack.getItem()) == block)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public List<Item> getItems() {
      List<Item> result = new ArrayList<Item>();
      for (ItemStack itemStack : ores) {
        if (itemStack != null && itemStack.getItem() != null && !result.contains(itemStack.getItem())) {
          result.add(itemStack.getItem());
        }
      }
      return result;
    }

    @Override
    public List<ItemStack> getItemStacks() {
      return ores;
    }

    @Override
    public List<Block> getBlocks() {
      List<Block> result = new ArrayList<Block>();
      for (ItemStack itemStack : ores) {
        if (itemStack != null && itemStack.getItem() != null) {
          Block block = Block.getBlockFromItem(itemStack.getItem());
          if (block != null && block != Blocks.AIR) {
            result.add(block);
          }
        }
      }
      return result;
    }

    @Override
    public Object getRecipeObject() {
      return name;
    }

  }

  private static class StringThing implements IThing {

    private final String name;

    private StringThing(String name) {
      this.name = name;
    }

    @Override
    public IThing bake() {
      if (name == null || name.trim().isEmpty()) {
        return null;
      }
      if (aliases.containsKey(name)) {
        return aliases.get(name).bake();
      }
      String mod = "minecraft", ident = name;
      boolean allowItem = true, allowBlock = true, allowOreDict = true;
      if (ident.startsWith("item:")) {
        allowBlock = allowOreDict = false;
        ident = ident.substring("item:".length());
      } else if (ident.startsWith("block:")) {
        allowItem = allowOreDict = false;
        ident = ident.substring("block:".length());
      } else if (ident.startsWith("oredict:")) {
        allowBlock = allowItem = false;
        ident = ident.substring("oredict:".length());
      }
      int meta = -1;
      if (ident.contains(":")) {
        String[] split = ident.split(":", 3);
        if (split != null && split.length >= 2) {
          mod = split[0];
          ident = split[1];
          if (split.length >= 3) {
            if ("*".equals(split[2])) {
              meta = OreDictionary.WILDCARD_VALUE;
            } else {
              try {
                meta = Integer.parseInt(split[2]);
              } catch (NumberFormatException e) {
                return null;
              }
            }
          }
        }
      }
      ResourceLocation resourceLocation = new ResourceLocation(mod, ident);
      if (meta < 0) {
        // this ugly thing seems to be what Forge wants you to use
        if (allowBlock && net.minecraft.block.Block.REGISTRY.containsKey(resourceLocation)) {
          Block block = net.minecraft.block.Block.REGISTRY.getObject(resourceLocation);
          return new BlockThing(block).bake();
        }
        // this ugly thing seems to be what Forge wants you to use
        if (allowItem && net.minecraft.item.Item.REGISTRY.containsKey(resourceLocation)) {
          Item item = net.minecraft.item.Item.REGISTRY.getObject(resourceLocation);
          if (item != null) {
            return new ItemThing(item).bake();
          }
        }
        if (allowOreDict) {
          return new OreThing(ident).bake();
        }
      } else {
        // this ugly thing seems to be what Forge wants you to use
        if (allowItem && net.minecraft.item.Item.REGISTRY.containsKey(resourceLocation)) {
          Item item = net.minecraft.item.Item.REGISTRY.getObject(resourceLocation);
          if (item != null) {
            return new ItemStackThing(new ItemStack(item, 1, meta)).bake();
          }
        }
        // this ugly thing seems to be what Forge wants you to use
        if (allowBlock && net.minecraft.block.Block.REGISTRY.containsKey(resourceLocation)) {
          Block block = net.minecraft.block.Block.REGISTRY.getObject(resourceLocation);
          return new ItemStackThing(new ItemStack(block, 1, meta)).bake();
        }
      }
      return null;
    }

    @Override
    public boolean is(Item item) {
      return false;
    }

    @Override
    public boolean is(ItemStack itemStack) {
      return false;
    }

    @Override
    public boolean is(Block block) {
      return false;
    }

    @Override
    public List<Item> getItems() {
      return Collections.<Item> emptyList();
    }

    @Override
    public List<ItemStack> getItemStacks() {
      return Collections.<ItemStack> emptyList();
    }

    @Override
    public List<Block> getBlocks() {
      return Collections.<Block> emptyList();
    }

    @Override
    public Object getRecipeObject() {
      return null;
    }

  }

  private static class ResourceThing implements IThing {

    private final ResourceLocation resourceLocation;

    private ResourceThing(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
    }

    @Override
    public IThing bake() {
      if (resourceLocation == null) {
        return null;
      }
      // this ugly thing seems to be what Forge wants you to use
      if (net.minecraft.block.Block.REGISTRY.containsKey(resourceLocation)) {
        Block block = net.minecraft.block.Block.REGISTRY.getObject(resourceLocation);
        return new BlockThing(block).bake();
      }
      // this ugly thing seems to be what Forge wants you to use
      Item item = net.minecraft.item.Item.REGISTRY.getObject(resourceLocation);
      if (item != null) {
        return new ItemThing(item).bake();
      }
      return null;
    }

    @Override
    public boolean is(Item item) {
      return false;
    }

    @Override
    public boolean is(ItemStack itemStack) {
      return false;
    }

    @Override
    public boolean is(Block block) {
      return false;
    }

    @Override
    public List<Item> getItems() {
      return Collections.<Item> emptyList();
    }

    @Override
    public List<ItemStack> getItemStacks() {
      return Collections.<ItemStack> emptyList();
    }

    @Override
    public List<Block> getBlocks() {
      return Collections.<Block> emptyList();
    }

    @Override
    public Object getRecipeObject() {
      return null;
    }

  }

}

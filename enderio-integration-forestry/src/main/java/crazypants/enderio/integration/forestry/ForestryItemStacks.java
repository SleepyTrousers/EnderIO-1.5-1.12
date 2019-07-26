package crazypants.enderio.integration.forestry;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry.ItemStackHolder;

public class ForestryItemStacks {

  // Forge will force-load this class because of the itemStack holders. So this cannot be in any class that depends on Forestry...

  @ItemStackHolder("forestry:naturalist_helmet")
  public static final ItemStack FORESTRY_HELMET = null;

  @ItemStackHolder("forestry:apiarist_boots")
  public static final ItemStack FORESTRY_FEET = null;
  @ItemStackHolder("forestry:apiarist_legs")
  public static final ItemStack FORESTRY_LEGS = null;
  @ItemStackHolder("forestry:apiarist_chest")
  public static final ItemStack FORESTRY_CHEST = null;
  @ItemStackHolder("forestry:apiarist_helmet")
  public static final ItemStack FORESTRY_HEAD = null;

  @ItemStackHolder("forestry:sapling")
  public static final ItemStack FORESTRY_SAPLING = null;

  @ItemStackHolder("forestry:fertilizer_compound")
  public static final ItemStack FORESTRY_FERTILIZER = null;

  // Alloy.VIBRANT_ALLOY.getStackBlock(), direct
  // new ItemStack(Items.ELYTRA),
  // emp: Material.VIBRANT_CRYSTAL.getStack(), new ItemStack(itemBasicCapacitor.getItemNN(), 1, 0),1,2, new ItemStack(blockEndermanSkull.getBlockNN(), 1,
  // SkullType.TORMENTED.ordinal())
  // new ItemStack(Blocks.CARPET, 1, OreDictionary.WILDCARD_VALUE),
  // new ItemStack(Items.SKULL, 1, 4), depth
  // new ItemStack(Blocks.TNT),
  // Material.GLIDER_WINGS.getStack(),
  // Item.REGISTRY.getObject(new ResourceLocation("thaumcraft", "goggles"));
  // new ItemStack(Items.DIAMOND_HOE),
  // new ItemStack(Blocks.PISTON),
  // PotionUtil.createNightVisionPotion(false, false);
  // new ItemStack(Blocks.WOOL, 1, OreDictionary.WILDCARD_VALUE),
  // new ItemStack(Blocks.NOTEBLOCK),
  // PotionUtil.createSwiftnessPotion(true, false);
  // new ItemStack(Items.DIAMOND_SHOVEL),
  // new ItemStack(Blocks.CHEST),
  // new ItemStack(Blocks.WATERLILY),
  // Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_boots"));
  // Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_legs"));
  // Item.REGISTRY.getObject(new ResourceLocation(MODID_THAUMCRAFT, "cloth_chest"));
  // @ItemStackHolder("theoneprobe:probe")
  // Material.ENDER_CRYSTAL.getStack(),
}

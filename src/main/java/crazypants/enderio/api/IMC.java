package crazypants.enderio.api;

import crazypants.enderio.fluid.FluidFuelRegister;
import crazypants.enderio.item.ItemSoulVessel;
import crazypants.enderio.machine.alloy.AlloyRecipeManager;
import crazypants.enderio.machine.crusher.CrusherRecipeManager;
import crazypants.enderio.machine.painter.PaintSourceValidator;
import crazypants.enderio.machine.soul.SoulBinderRecipeManager;
import crazypants.enderio.machine.spawner.PoweredSpawnerConfig;
import crazypants.enderio.machine.still.VatRecipeManager;
import crazypants.enderio.teleport.TravelController;

/**
 * This class provides the keys for the IMC messages supported by EIO and links
 * the the details of how the messages are processed. It is preferable not to
 * refer to these constants directly to avoid a dependence on this class.
 */
public final class IMC {

  /**
   * Key for a string message to add Vat recipes. Calls
   * {@link VatRecipeManager#addCustumRecipes(String)} with the string value of
   * the message.
   */
  public static final String VAT_RECIPE = "recipe:vat";

  /**
   * Key for a string message to add SAGMill recipes. Calls
   * {@link CrusherRecipeManager#addCustumRecipes(String)} with the string value
   * of the message.
   */
  public static final String SAG_RECIPE = "recipe:sagmill";

  /**
   * Key for a string message to add Alloy Smelter recipes. Calls
   * {@link AlloyRecipeManager#addCustumRecipes(String)} with the string value
   * of the message.
   */
  public static final String ALLOY_RECIPE = "recipe:alloysmelter";

  /**
   * Key for an NBT message to add Soul Binder recipes. Calls
   * {@link SoulBinderRecipeManager#addRecipeFromNBT(net.minecraft.nbt.NBTTagCompound)}
   * with the NBT value of the message.
   */
  public static final String SOUL_BINDER_RECIPE = "recipe:soulbinder";

  /**
   * Key for an ItemStack message to add an item to the Painters paint source
   * whitelist. Calls
   * {@link PaintSourceValidator#addToWhitelist(net.minecraft.item.ItemStack) 
   */
  public static final String PAINTER_WHITELIST_ADD = "painter:whitelist:add";

  /**
   * Key for an ItemStack message to add an item to the Painters paint source
   * blacklist. Calls
   * {@link PaintSourceValidator#addToBlacklist(net.minecraft.item.ItemStack) 
   */
  public static final String PAINTER_BLACKLIST_ADD = "painter:blacklist:add";

  /**
   * Key for a string message to add an entity to the Powered Spawner blacklist.
   * Calls {@link PoweredSpawnerConfig#addToBlacklist(String)} with the string
   * value of the message.
   */
  public static final String POWERED_SPAWNER_BLACKLIST_ADD = "poweredSpawner:blacklist:add";

  /**
   * Key for an NBT message to specify a cost multiplier for spawning an entity
   * in the Powered Spawner. Calls
   * {@link PoweredSpawnerConfig#addEntityCostFromNBT(net.minecraft.nbt.NBTTagCompound)}
   * with the NBT value of the message.
   */
  public static final String POWERED_SPAWNER_COST_MULTIPLIER = "poweredSpawner:costMultiplier";

  /**
   * Key for a string message to add an entity to the Soul Vial blacklist. Calls
   * {@link ItemSoulVessel#addEntityToBlackList(String)} with the string value
   * of the message.
   */
  public static final String SOUL_VIAL_BLACKLIST = "soulVial:blacklist:add";

  /**
   * Key for an NBT message to register a fluid fuel. Calls
   * {@link FluidFuelRegister#addFuel(net.minecraft.nbt.NBTTagCompound)} with
   * the NBT value of the message.
   */
  public static final String FLUID_FUEL_ADD = "fluidFuel:add";

  /**
   * Key for an NBT message to register a fluid coolant. Calls
   * {@link FluidFuelRegister#addCoolant(net.minecraft.nbt.NBTTagCompound)} with
   * the NBT value of the message.
   */
  public static final String FLUID_COOLANT_ADD = "fluidCoolant:add";

  /**
   * Key for a string message to add a block to the list of blocks that cannot
   * be teleported through using the Staff of Travel or Dark Steel Travel
   * Upgrade. Value must be in the form: 'modid:blockName' Calls
   * {@link TravelController#addBlockToBlinkBlackList(String)} with the string
   * value of the message.
   */
  public static final String TELEPORT_BLACKLIST_ADD = "teleport:blacklist:add";


  private IMC() {
  }

}

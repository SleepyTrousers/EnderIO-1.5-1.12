package crazypants.enderio.api;

import crazypants.enderio.api.redstone_dont_crash_us_mcjty.IRedstoneConnectable_dont_crash_us_mcjty;
import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.config.Config;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.paint.PaintSourceValidator;
import crazypants.enderio.base.recipe.soul.SoulBinderRecipeManager;
import crazypants.enderio.base.recipe.vat.VatRecipeManager;
import crazypants.enderio.util.CapturedMob;

/**
 * This class provides the keys for the IMC messages supported by EIO and links the the details of how the messages are processed. It is preferable not to refer
 * to these constants directly to avoid a dependence on this class.
 * <p>
 * Missing some entries? Many recipes were moved over to a new XML parser and can now registered with {@link #XML_RECIPE}.
 */
public final class IMC {

  /**
   * Key for a string message to add Vat recipes. Calls {@link VatRecipeManager#addCustomRecipes(String)} with the string value of the message.
   */
  public static final String VAT_RECIPE = "recipe:vat";

  /**
   * Key for a string message to add xml recipes. The supplied xml is treated as if it was loaded from one of the recipe XMLs. IMC recipes are processed after
   * core recipes but before user recipes. This means they can replace/disable core recipes but can themselves be altered by user recipes.
   * <p>
   * Note that for the recipe merging to work, the IMC message must be sent before the IMC lifecycle event (which is between init and post-init). IMC messages
   * sent later will still be processed, but then all other recipes will already have been loaded.
   */
  public static final String XML_RECIPE = "recipe:xml";

  /**
   * Key for an NBT message to add Soul Binder recipes. Calls {@link SoulBinderRecipeManager#addRecipeFromNBT(net.minecraft.nbt.NBTTagCompound)} with the NBT
   * value of the message.
   */
  public static final String SOUL_BINDER_RECIPE = "recipe:soulbinder";

  /**
   * Key for an ItemStack message to add an item to the Painters paint source whitelist. Calls
   * {@link PaintSourceValidator#addToWhitelist(net.minecraft.item.ItemStack)}
   */
  public static final String PAINTER_WHITELIST_ADD = "painter:whitelist:add";

  /**
   * Key for an ItemStack message to add an item to the Painters paint source blacklist. Calls
   * {@link PaintSourceValidator#addToBlacklist(net.minecraft.item.ItemStack)}
   */
  public static final String PAINTER_BLACKLIST_ADD = "painter:blacklist:add";

  /**
   * Key for a string message to add an entity to the Soul Vial blacklist. Calls {@link CapturedMob#addToBlackList(net.minecraft.util.ResourceLocation)} with
   * the string value of the message. Entities blacklisted this way cannot be picked up with Soul Vials.
   */
  public static final String SOUL_VIAL_BLACKLIST = "soulVial:blacklist:add";

  /**
   * Key for a string message to add an entity to the Spawner's "clone instead of spawn" list. Entities on this list will be cloned by the Powered Spawner
   * instead of spawned as a new entity. This is needed for entities that have sub-types (e.g. Fluid Cows). Using this for entities that have an inventory (e.g.
   * zombies with their hands) will allow item duping!
   */
  public static final String SOUL_VIAL_UNSPAWNABLELIST = "soulVial:unspawnablelist:add";

  /**
   * Key for an NBT message to register a fluid fuel. Calls {@link FluidFuelRegister#addFuel(net.minecraft.nbt.NBTTagCompound)} with the NBT value of the
   * message.
   */
  public static final String FLUID_FUEL_ADD = "fluidFuel:add";

  /**
   * Key for an NBT message to register a fluid coolant. Calls {@link FluidFuelRegister#addCoolant(net.minecraft.nbt.NBTTagCompound)} with the NBT value of the
   * message.
   */
  public static final String FLUID_COOLANT_ADD = "fluidCoolant:add";

  /**
   * Key for a string message to add a block to the list of blocks that cannot be teleported through using the Staff of Travel or Dark Steel Travel Upgrade.
   * Value must be in the form: 'modid:blockName' Adds the value to {@link Config#TRAVEL_BLACKLIST}.
   */
  public static final String TELEPORT_BLACKLIST_ADD = "teleport:blacklist:add";

  /**
   * Key for an string message to register a block as connectable to insulated redstone conduits. Calls {@link ConnectivityTool#registerRedstoneAware(String)}
   * with the value of the message. The value has the same syntax that is used in the xml config files. Using {@link IRedstoneConnectable_dont_crash_us_mcjty}
   * is generally preferred to this because it allows location-, state- and side-awareness.
   */
  public static final String REDSTONE_CONNECTABLE_ADD = "redstone:connectable:add";

  private IMC() {
  }

}

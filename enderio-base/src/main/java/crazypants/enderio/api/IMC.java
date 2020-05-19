package crazypants.enderio.api;

import crazypants.enderio.base.conduit.redstone.ConnectivityTool;
import crazypants.enderio.base.config.recipes.RecipeFactory;
import crazypants.enderio.base.fluid.FluidFuelRegister;
import crazypants.enderio.base.paint.PaintSourceValidator;
import net.minecraft.block.Block;

/**
 * This class provides the keys for the IMC messages supported by EIO and links the the details of how the messages are processed. It is preferable not to refer
 * to these constants directly to avoid a dependence on this class.
 * <p>
 * Missing some entries? Many recipes were moved over to a new XML parser and can now registered with {@link #XML_RECIPE}.
 */
public final class IMC {

  /**
   * Key for a string message to add xml recipes. The supplied xml is treated as if it was loaded from one of the recipe XMLs. IMC recipes are processed after
   * core recipes but before user recipes. This means they can replace/disable core recipes but can themselves be altered by user recipes.
   * <p>
   * Note that for the recipe merging to work, the IMC message must be sent before the IMC lifecycle event (which is between init and post-init). IMC messages
   * sent later will still be processed, but then all other recipes will already have been loaded.
   */
  public static final String XML_RECIPE = "recipe:xml";

  /**
   * Key for a string message to add xml recipes from a file. The supplied string must be a filename that must exist and be readable. The contents of the file
   * are treated the same as the contents from a {@link #XML_RECIPE} message.
   * <p>
   * We <b>really</b> recommend for that file to be in your config folder, but do not enforce it.
   * <p>
   * Have a look at {@link RecipeFactory#copyCore(String)} to see how to copy a file from your jar into your config folder.
   */
  public static final String XML_RECIPE_FILE = "recipe:xml:file";

  /**
   * @deprecated Use {@link #XML_RECIPE}
   */
  @Deprecated
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
   * Key for a string (ResourceLocation) message to add an entity to the Soul Vial blacklist. Entities blacklisted this way cannot be picked up with Soul Vials.
   * 
   * @deprecated Use {@link #XML_RECIPE}
   */
  @Deprecated
  public static final String SOUL_VIAL_BLACKLIST = "soulVial:blacklist:add";

  /**
   * Key for a string message to add an entity to the Spawner's "clone instead of spawn" list. Entities on this list will be cloned by the Powered Spawner
   * instead of spawned as a new entity. This is needed for entities that have sub-types (e.g. Fluid Cows). Using this for entities that have an inventory (e.g.
   * zombies with their hands) will allow item duping!
   * 
   * @deprecated Use {@link #XML_RECIPE}
   */
  @Deprecated
  public static final String SOUL_VIAL_UNSPAWNABLELIST = "soulVial:unspawnablelist:add";

  /**
   * Key for an NBT message to register a fluid fuel. Calls {@link FluidFuelRegister#addFuel(net.minecraft.nbt.NBTTagCompound)} with the NBT value of the
   * message.
   * 
   * @deprecated Use {@link #XML_RECIPE}
   */
  @Deprecated
  public static final String FLUID_FUEL_ADD = "fluidFuel:add";

  /**
   * Key for an NBT message to register a fluid coolant. Calls {@link FluidFuelRegister#addCoolant(net.minecraft.nbt.NBTTagCompound)} with the NBT value of the
   * message.
   * 
   * @deprecated Use {@link #XML_RECIPE}
   */
  @Deprecated
  public static final String FLUID_COOLANT_ADD = "fluidCoolant:add";

  /**
   * Key for a string message to add a block to the list of blocks that cannot be teleported through using the Staff of Travel or Dark Steel Travel Upgrade.
   * Value must be in the form: 'modid:blockName'.
   */
  public static final String TELEPORT_BLACKLIST_ADD = "teleport:blacklist:add";

  /**
   * Key for an string message to register a block as connectable to insulated redstone conduits. Calls {@link ConnectivityTool#registerRedstoneAware(String)}
   * with the value of the message. The value has the same syntax that is used in the xml config files.
   * 
   * @deprecated Using {@link Block#canConnectRedstone} is generally preferred to this because those allow location-, state- and side-awareness.
   */
  @Deprecated
  public static final String REDSTONE_CONNECTABLE_ADD = "redstone:connectable:add";

  /**
   * Key for a message to tell Ender IO that you provide a way to paint items. If Ender IO Machines is installed this will have no effect, otherwise it will
   * tell the base module that there is some alternative to the Painting Machine installed. For now this only enabled item tooltips.
   */
  public static final String ENABLE_PAINTING = "enablePainting";

  private IMC() {
  }

}

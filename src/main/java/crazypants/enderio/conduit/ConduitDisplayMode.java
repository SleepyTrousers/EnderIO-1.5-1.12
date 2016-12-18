package crazypants.enderio.conduit;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static crazypants.enderio.gui.IconEIO.CROSS;
import static crazypants.enderio.gui.IconEIO.TICK;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_FLUID;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_FLUID_OFF;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_ITEM;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_ITEM_OFF;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_POWER;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_POWER_OFF;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_REDSTONE;
import static crazypants.enderio.gui.IconEIO.WRENCH_OVERLAY_REDSTONE_OFF;
import static crazypants.enderio.gui.IconEIO.YETA_GEAR;

public class ConduitDisplayMode {

  public static final ConduitDisplayMode NEUTRAL = new ConduitDisplayMode("neutral", YETA_GEAR, YETA_GEAR);
  public static final ConduitDisplayMode ALL = new ConduitDisplayMode("all", TICK, TICK);
  public static final ConduitDisplayMode NONE = new ConduitDisplayMode("none", CROSS, CROSS);
  
  private static final List<ConduitDisplayMode> registrar;

  // @formatter:off
  static {
    registrar = Lists.newArrayList(
        NONE, 
        ALL, 
        new ConduitDisplayMode(IItemConduit.class, WRENCH_OVERLAY_ITEM, WRENCH_OVERLAY_ITEM_OFF), 
        new ConduitDisplayMode(ILiquidConduit.class, WRENCH_OVERLAY_FLUID, WRENCH_OVERLAY_FLUID_OFF), 
        new ConduitDisplayMode(IPowerConduit.class, WRENCH_OVERLAY_POWER, WRENCH_OVERLAY_POWER_OFF), 
        new ConduitDisplayMode(IRedstoneConduit.class, WRENCH_OVERLAY_REDSTONE, WRENCH_OVERLAY_REDSTONE_OFF),
        NEUTRAL
     );
  }
  // @formatter:on

  public static void registerDisplayMode(ConduitDisplayMode mode) {
    if (!registrar.contains(mode)) {
      registrar.add(mode);
    }
  }

  private final Class<? extends IConduit> conduitType;
  private final IWidgetIcon widgetSelected, widgetUnselected;

  private String overrideName = null;

  /**
   * Use this constructor if you have custom display logic, it will use
   * {@code IConduit.class} as the conduitType, and the passed name as the
   * override name.
   * 
   * @see #ConduitDisplayMode(Class, IWidgetIcon, IWidgetIcon)
   * 
   * @param name
   *          The override name.
   * @param widgetSelected
   *          The widget to render when this type is selected.
   * @param widgetUnselected
   *          The widget to render when this type is unselected.
   */
  public ConduitDisplayMode(String name, IWidgetIcon widgetSelected, IWidgetIcon widgetUnselected) {
    this(IConduit.class, widgetSelected, widgetUnselected);
    setName(name);
  }

  /**
   * Creates a new display mode for any {@link IConduitControl} wrench. Contains
   * data about which conduit type this is for, and the icons to render while
   * holding the wrench. wrench.
   * 
   * @param conduitType
   *          The base class for your conduit type, typically an interface (e.g.
   *          {@code IPowerConduit}).
   * @param widgetSelected
   *          The widget to render when this type is selected.
   * @param widgetUnselected
   *          The widget to render when this type is unselected.
   */
  public ConduitDisplayMode(@Nonnull Class<? extends IConduit> conduitType, IWidgetIcon widgetSelected, IWidgetIcon widgetUnselected) {
    this.conduitType = conduitType;
    this.widgetSelected = widgetSelected;
    this.widgetUnselected = widgetUnselected;
  }

  @Nullable
  public Class<? extends IConduit> getConduitType() {
    return conduitType;
  }

  public boolean renderConduit(Class<? extends IConduit> conduitTypeIn) {
    if (this == ALL || this == NEUTRAL) {
      return true;
    } else if (this == NONE) {
      return false;
    } else {
      return this.conduitType.isAssignableFrom(conduitTypeIn);
    }
  }

  @SuppressWarnings("null")
  @Nonnull
  public String getName() {
    return overrideName == null ? conduitType.getSimpleName() : overrideName;
  }

  /**
   * The name is null by default, and will use the simple class name of the
   * conduit type.
   * 
   * @param name
   *          The override name to set.
   */
  public void setName(@Nullable String name) {
    this.overrideName = name;
  }

  public IWidgetIcon getWidgetSelected() {
    return widgetSelected;
  }

  public IWidgetIcon getWidgetUnselected() {
    return widgetUnselected;
  }
  
  public static ConduitDisplayMode next(ConduitDisplayMode mode) {
    int index = registrar.indexOf(mode) + 1;
    if (index >= registrar.size()) {
      index = 0;
    }
    return registrar.get(index);
  }

  public static ConduitDisplayMode previous(ConduitDisplayMode mode) {
    int index = registrar.indexOf(mode) - 1;
    if (index < 0) {
      index = registrar.size() - 1;
    }
    return registrar.get(index);
  }

  public static ConduitDisplayMode fromName(String name) {
    for (ConduitDisplayMode mode : registrar) {
      if (mode.getName().equals(name)) {
        return mode;
      }
    }
    return null;
  }

  public int ordinal() {
    return registrar.indexOf(this);
  }

  private static final String NBT_KEY = "enderio.displaymode";

  public static ConduitDisplayMode getDisplayMode(ItemStack equipped) {
    if (equipped == null || !(equipped.getItem() instanceof IConduitControl)) {
      return ALL;
    }
    initDisplayModeTag(equipped);
    String name = equipped.getTagCompound().getString(NBT_KEY);
    ConduitDisplayMode mode = fromName(name);
    if (mode == null) { // backwards compat
      setDisplayMode(equipped, ALL);
      return ALL;
    }
    return mode;
  }

  public static void setDisplayMode(ItemStack equipped, ConduitDisplayMode mode) {
    if (mode == null || equipped == null || !(equipped.getItem() instanceof IConduitControl)) {
      return;
    }
    initDisplayModeTag(equipped);
    equipped.getTagCompound().setString(NBT_KEY, mode.getName());
  }

  private static void initDisplayModeTag(ItemStack stack) {
    if (stack.getTagCompound() == null) {
      NBTTagCompound stackTagCompound = new NBTTagCompound();
      stackTagCompound.setString(NBT_KEY, ConduitDisplayMode.ALL.getName());
      stack.setTagCompound(stackTagCompound);
    }
  }

  public ConduitDisplayMode next() {
    return next(this);
  }

  public ConduitDisplayMode previous() {
    return previous(this);
  }

  public boolean isAll() {
    return this == ALL || this == NEUTRAL;
  }

  public static int registrySize() {
    return registrar.size() - 2;
  }

  public static Iterable<ConduitDisplayMode> getRenderableModes() {
    return FluentIterable.from(registrar).filter(new Predicate<ConduitDisplayMode>() {
      @Override
      public boolean apply(@Nullable ConduitDisplayMode input) {
        return input != ALL && input != NONE;// && input != NEUTRAL;
      }

      @Override
      public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
      }
    });
  }

  @Override
  public String toString() {
    return getName();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((conduitType == null) ? 0 : conduitType.hashCode());
    result = prime * result + ((overrideName == null) ? 0 : overrideName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ConduitDisplayMode other = (ConduitDisplayMode) obj;
    if (conduitType == null) {
      if (other.conduitType != null)
        return false;
    } else if (!conduitType.equals(other.conduitType))
      return false;
    if (overrideName == null) {
      if (other.overrideName != null)
        return false;
    } else if (!overrideName.equals(other.overrideName))
      return false;
    return true;
  }
}
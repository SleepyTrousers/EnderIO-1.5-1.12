package crazypants.enderio.conduit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.api.client.render.IWidgetIcon;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import crazypants.enderio.api.tool.IConduitControl;
import crazypants.enderio.base.conduit.ConduitDisplayMode;
import crazypants.enderio.base.conduit.IConduit;
import net.minecraft.item.ItemStack;

import static crazypants.enderio.base.gui.IconEIO.CROSS;
import static crazypants.enderio.base.gui.IconEIO.TICK;
import static crazypants.enderio.base.gui.IconEIO.YETA_GEAR;
import static crazypants.enderio.util.NbtValue.DISPLAYMODE;

public class ConduitDisplayMode {

  public static final @Nonnull ConduitDisplayMode NEUTRAL = new ConduitDisplayMode("neutral", YETA_GEAR, YETA_GEAR);
  public static final @Nonnull ConduitDisplayMode ALL = new ConduitDisplayMode("all", TICK, TICK);
  public static final @Nonnull ConduitDisplayMode NONE = new ConduitDisplayMode("none", CROSS, CROSS);
  
  private static final @Nonnull NNList<ConduitDisplayMode> registrar = new NNList<ConduitDisplayMode>();

  static {
    registerDisplayMode(NEUTRAL);
    registerDisplayMode(NONE);
    registerDisplayMode(ALL);
  }

  public static void registerDisplayMode(@Nonnull ConduitDisplayMode mode) {
    if (!registrar.contains(mode)) {
      registrar.add(mode);
    }
  }

  private final @Nonnull Class<? extends IConduit> conduitType;
  private final @Nonnull IWidgetIcon widgetSelected, widgetUnselected;

  private @Nullable String overrideName = null;

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
  public ConduitDisplayMode(@Nonnull String name, @Nonnull IWidgetIcon widgetSelected, @Nonnull IWidgetIcon widgetUnselected) {
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
  public ConduitDisplayMode(@Nonnull Class<? extends IConduit> conduitType, @Nonnull IWidgetIcon widgetSelected, @Nonnull IWidgetIcon widgetUnselected) {
    this.conduitType = conduitType;
    this.widgetSelected = widgetSelected;
    this.widgetUnselected = widgetUnselected;
  }

  public @Nonnull Class<? extends IConduit> getConduitType() {
    return conduitType;
  }

  public boolean renderConduit(Class<? extends IConduit> conduitTypeIn) {
    if (this == ALL || this == NEUTRAL || conduitTypeIn == null) {
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
  public void setName(@Nonnull String name) {
    this.overrideName = name;
  }

  public @Nonnull IWidgetIcon getWidgetSelected() {
    return widgetSelected;
  }

  public @Nonnull IWidgetIcon getWidgetUnselected() {
    return widgetUnselected;
  }
  
  public static @Nonnull ConduitDisplayMode next(@Nonnull ConduitDisplayMode mode) {
    return registrar.next(mode);
  }

  public static @Nonnull ConduitDisplayMode previous(@Nonnull ConduitDisplayMode mode) {
    return registrar.prev(mode);
  }

  public static @Nullable ConduitDisplayMode fromName(String name) {
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

  public static @Nonnull ConduitDisplayMode getDisplayMode(@Nonnull ItemStack equipped) {
    if (!(equipped.getItem() instanceof IConduitControl)) {
      return ALL;
    }
    ConduitDisplayMode mode = fromName(DISPLAYMODE.getString(equipped, ConduitDisplayMode.ALL.getName()));
    if (mode == null) { // backwards compat
      setDisplayMode(equipped, ALL);
      return ALL;
    }
    return mode;
  }

  public static void setDisplayMode(@Nonnull ItemStack equipped, @Nonnull ConduitDisplayMode mode) {
    if (!(equipped.getItem() instanceof IConduitControl)) {
      return;
    }
    DISPLAYMODE.setString(equipped, mode.getName());
  }

  public @Nonnull ConduitDisplayMode next() {
    return next(this);
  }

  public @Nonnull ConduitDisplayMode previous() {
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

      @Override
      public int hashCode() {
        return super.hashCode();
      }
    });
  }

  @Override
  public @Nonnull String toString() {
    return getName();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + conduitType.hashCode();
    result = prime * result + NullHelper.first(overrideName, "").hashCode();
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
    if (!conduitType.equals(other.conduitType))
      return false;
    if (!Objects.equal(overrideName, other.overrideName))
      return false;
    return true;
  }
}
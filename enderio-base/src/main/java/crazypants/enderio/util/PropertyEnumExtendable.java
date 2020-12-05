package crazypants.enderio.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NullHelper;
import com.google.common.base.Optional;

import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;

public class PropertyEnumExtendable<T extends Comparable<T> & IStringSerializable> extends PropertyHelper<T> {

  private final @Nonnull Set<T> allowedValues = new HashSet<>();
  private final @Nonnull Map<String, T> nameToValue = new HashMap<>();
  private final @Nonnull Map<T, Integer> nameToOrder = new HashMap<>();
  private final @Nonnull List<T> orderValues = new ArrayList<>();
  private boolean locked = false;

  protected PropertyEnumExtendable(String name, Class<T> valueClass) {
    super(name, valueClass);
  }

  public void addValue(@Nonnull T value) {
    if (locked) {
      throw new RuntimeException("Cannot add values after this Property has been used");
    }
    String s = ((IStringSerializable) value).getName();

    if (nameToValue.containsKey(s)) {
      throw new IllegalArgumentException("Multiple values have the same name '" + s + "'");
    }

    allowedValues.add(value);
    nameToValue.put(s, value);
    nameToOrder.put(value, orderValues.size());
    orderValues.add(value);
  }

  public @Nonnull T byID(int id) {
    return NullHelper.first(orderValues.get(MathHelper.clamp(id, 0, orderValues.size() - 1)), orderValues.get(0));
  }

  public int byIdentity(T id) {
    return NullHelper.first(nameToOrder.get(id), 0);
  }

  public List<T> getOrderedValues() {
    locked = true;
    return orderValues;
  }

  @Override
  public @Nonnull Collection<T> getAllowedValues() {
    locked = true;
    return allowedValues;
  }

  @SuppressWarnings("null")
  @Override
  public @Nonnull Optional<T> parseValue(@Nonnull String value) {
    return Optional.<T> fromNullable(nameToValue.get(value));
  }

  /**
   * Get the name for the given value.
   */
  @Override
  public @Nonnull String getName(@Nonnull T value) {
    return ((IStringSerializable) value).getName();
  }

  @Override
  public boolean equals(Object p_equals_1_) {
    if (this == p_equals_1_) {
      return true;
    } else if (p_equals_1_ instanceof PropertyEnumExtendable && super.equals(p_equals_1_)) {
      PropertyEnumExtendable<?> propertyenum = (PropertyEnumExtendable<?>) p_equals_1_;
      return allowedValues.equals(propertyenum.allowedValues) && nameToValue.equals(propertyenum.nameToValue);
    } else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    int i = super.hashCode();
    i = 31 * i + allowedValues.hashCode();
    i = 31 * i + nameToValue.hashCode();
    return i;
  }

  public static @Nonnull <T extends Comparable<T> & IStringSerializable> PropertyEnumExtendable<T> create(@Nonnull String name, @Nonnull Class<T> clazz) {
    return new PropertyEnumExtendable<T>(name, clazz);
  }
}
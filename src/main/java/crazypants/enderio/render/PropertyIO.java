package crazypants.enderio.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.EnumFacing;

import com.google.common.collect.ImmutableSet;

public class PropertyIO extends PropertyHelper<IOMode> {

  private final static ImmutableSet<IOMode> allowedValues;
  static {
    List<IOMode> values = new ArrayList<IOMode>();
    for (EnumFacing facing : EnumFacing.values()) {
      for (IOMode.EnumIOMode mode : IOMode.EnumIOMode.values()) {
        values.add(IOMode.get(facing, mode));
      }
    }
    allowedValues = ImmutableSet.copyOf(values);
  }

  private static PropertyIO instance = new PropertyIO("iomode");

  protected PropertyIO(String name) {
    super(name, IOMode.class);
  }

  public static PropertyIO getInstance() {
    return instance;
  }

  @Override
  public Collection<IOMode> getAllowedValues() {
    return allowedValues;
  }

  @Override
  public String getName(IOMode value) {
    return value.toString();
  }

}

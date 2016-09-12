package crazypants.enderio.render.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PropertyIO extends PropertyHelper<IOMode> {

  private final static ImmutableSet<IOMode> allowedValues;
  private final static Map<String, IOMode> nameToValue = Maps.<String, IOMode> newHashMap();
  static {
    List<IOMode> values = new ArrayList<IOMode>();
    for (EnumFacing facing : EnumFacing.values()) {
      for (IOMode.EnumIOMode mode : IOMode.EnumIOMode.values()) {
        IOMode value = IOMode.get(facing, mode);
        values.add(value);
        nameToValue.put(value.toString(), value);
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

  @Override
  @SideOnly(Side.CLIENT)
  public Optional<IOMode> parseValue(String value) {
    return Optional.<IOMode> fromNullable(nameToValue.get(value));
  }
 
}

package crazypants.enderio.base.render.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import crazypants.enderio.base.render.property.IOMode.EnumIOMode;
import net.minecraft.block.properties.PropertyHelper;
import net.minecraft.util.EnumFacing;

public class PropertyIO extends PropertyHelper<IOMode> {

  private final static @Nonnull ImmutableSet<IOMode> allowedValues;
  private final static @Nonnull Map<String, IOMode> nameToValue = new HashMap<>();
  static {
    List<IOMode> values = new ArrayList<IOMode>();
    NNIterator<EnumFacing> faces = NNList.FACING.iterator();
    while (faces.hasNext()) {
      EnumFacing facing = faces.next();
      NNIterator<EnumIOMode> iomodes = IOMode.EnumIOMode.IOMODES.iterator();
      while (iomodes.hasNext()) {
        IOMode value = IOMode.get(facing, iomodes.next());
        values.add(value);
        nameToValue.put(value.toString(), value);
      }
    }
    allowedValues = ImmutableSet.copyOf(values);
  }

  private static final @Nonnull PropertyIO instance = new PropertyIO("iomode");

  protected PropertyIO(String name) {
    super(name, IOMode.class);
  }

  public static @Nonnull PropertyIO getInstance() {
    return instance;
  }

  @Override
  public @Nonnull Collection<IOMode> getAllowedValues() {
    return allowedValues;
  }

  @Override
  public @Nonnull String getName(@Nonnull IOMode value) {
    return value.toString();
  }

  @Override
  public @Nonnull Optional<IOMode> parseValue(@Nonnull String value) {
    return Optional.<IOMode> fromNullable(nameToValue.get(value));
  }

}

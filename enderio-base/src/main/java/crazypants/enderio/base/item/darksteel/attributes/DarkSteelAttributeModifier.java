package crazypants.enderio.base.item.darksteel.attributes;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;

import crazypants.enderio.base.config.ValueFactory.IValue;
import net.minecraft.entity.ai.attributes.AttributeModifier;

class DarkSteelAttributeModifier extends AttributeModifier {

  private final IValue<Float> config;

  DarkSteelAttributeModifier(@Nonnull String name, @Nonnull IValue<Float> config, @Nonnull Operation op) {
    super(UUID.nameUUIDFromBytes(name.getBytes(Charsets.UTF_8)), name, 0, op.ordinal());
    this.config = config;
  }

  @Override
  public double getAmount() {
    return config.get();
  }

}
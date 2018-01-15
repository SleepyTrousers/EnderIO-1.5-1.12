package crazypants.enderio.base.item.darksteel.attributes;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;

import crazypants.enderio.base.config.ValueFactory.IValue;
import net.minecraft.entity.ai.attributes.AttributeModifier;

class DoubleSteelAttributeModifier extends AttributeModifier {

  private final IValue<Float> config, config2;

  DoubleSteelAttributeModifier(@Nonnull String name, @Nonnull IValue<Float> config, @Nonnull IValue<Float> config2) {
    super(UUID.nameUUIDFromBytes(name.getBytes(Charsets.UTF_8)), name, 0, Operation.PERCENT_OF_BASE.ordinal());
    this.config = config;
    this.config2 = config2;
  }

  @Override
  public double getAmount() {
    return config.get() * config2.get();
  }

}
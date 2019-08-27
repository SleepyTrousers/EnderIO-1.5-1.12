package crazypants.enderio.base.item.darksteel.attributes;

import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.base.Charsets;

import info.loenwind.autoconfig.factory.IValue;
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

  @SuppressWarnings({ "null", "unused" })
  @Override
  public boolean equals(Object p_equals_1_) {
    if (this == p_equals_1_) {
      return true;
    } else if (p_equals_1_ != null && p_equals_1_ instanceof AttributeModifier) {
      AttributeModifier attributemodifier = (AttributeModifier) p_equals_1_;

      if (this.getID() != null) {
        if (!this.getID().equals(attributemodifier.getID())) {
          return false;
        }
      } else if (attributemodifier.getID() != null) {
        return false;
      }

      return true;
    } else {
      return false;
    }
  }

  @SuppressWarnings("null")
  @Override
  public int hashCode() {
    return this.getID() != null ? this.getID().hashCode() : 0;
  }

  @Override
  public @Nonnull String toString() {
    return "DoubleSteelAttributeModifier{amount=" + this.getAmount() + ", super=" + super.toString() + "}";
  }
}

package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Fluid implements IRecipeConfigElement {

  protected Optional<String> name = empty();
  protected Optional<String> nbt = empty();
  protected transient Optional<net.minecraftforge.fluids.Fluid> fluid = empty();
  protected transient Optional<NBTTagCompound> tag = empty();

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("Missing fluid name");
    }
    fluid = ofNullable(FluidRegistry.getFluid(get(name)));
    if (nbt.isPresent()) {
      try {
        tag = of(JsonToNBT.getTagFromJson(get(nbt)));
      } catch (NBTException e) {
        throw new InvalidRecipeConfigException("'" + nbt.get() + "' is not valid NBT json");
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a fluid for '" + name.get());
    }
  }

  @Override
  public boolean isValid() {
    return fluid.isPresent();
  }

  public net.minecraftforge.fluids.Fluid getFluid() {
    return get(fluid);
  }

  public FluidStack getFluidStack() {
    return new FluidStack(fluid.get(), 1000, tag.orElse(null));
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("nbt".equals(name)) {
      this.nbt = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

}

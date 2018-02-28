package crazypants.enderio.base.config.recipes.xml;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.RecipeConfigElement;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class Fluid implements RecipeConfigElement {

  protected String name;
  protected String nbt;
  protected transient net.minecraftforge.fluids.Fluid fluid;
  private transient NBTTagCompound tag;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (name == null || name.trim().isEmpty()) {
      throw new InvalidRecipeConfigException("Missing fluid name");
    }
    fluid = FluidRegistry.getFluid(name);
    final String nbt_nullchecked = nbt;
    if (nbt_nullchecked != null) {
      if (!nbt_nullchecked.trim().isEmpty()) {
        try {
          tag = JsonToNBT.getTagFromJson(nbt_nullchecked);
        } catch (NBTException e) {
          throw new InvalidRecipeConfigException(nbt_nullchecked + " is not valid NBT json.");
        }
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a fluid for '" + name);
    }
  }

  @Override
  public boolean isValid() {
    return fluid != null;
  }

  public net.minecraftforge.fluids.Fluid getFluid() {
    return fluid;
  }

  public @Nonnull FluidStack getFluidStack() {
    return new FluidStack(fluid, 1000, tag);
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = value;
      return true;
    }
    if ("nbt".equals(name)) {
      this.nbt = value;
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

}
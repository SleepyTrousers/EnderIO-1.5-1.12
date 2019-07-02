package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.xml.stream.XMLStreamException;

import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class Output extends AbstractConditional {

  private int amount;

  private Optional<String> nbt = empty();

  private Optional<Item> item = empty();

  private boolean required = true;

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    super.readResolve();
    if (!item.isPresent()) {
      throw new InvalidRecipeConfigException("Missing name in <output>");
    }
    if (amount < 0) {
      throw new InvalidRecipeConfigException("Invalid negative amount in <output>");
    }
    if (amount > item.get().getItemStack().getMaxStackSize()) {
      throw new InvalidRecipeConfigException("Invalid amount in <output>, bigger than maximum stack size");
    }
    if (amount == 0) {
      amount = 1;
    }
    item.get().getThing().setSize(amount);
    if (nbt.isPresent()) {
      try {
        item.get().getThing().setNbt(JsonToNBT.getTagFromJson(get(nbt)));
      } catch (NBTException e) {
        throw new InvalidRecipeConfigException("'" + nbt.get() + "' is not valid NBT json");
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    item.get().enforceValidity();
  }

  // Items can be potentially valid with an oredict value that doesn't yet have any items. We cannot use that for the output, so we force it to have at least
  // one valid stack.
  @Override
  public boolean isValid() {
    return item.isPresent() && item.get().isValid();
  }

  @Override
  public boolean isActive() {
    return super.isActive() && (required || isValid());
  }

  public ItemStack getItemStack() {
    ItemStack itemStack = item.get().getItemStack().copy();
    itemStack.setCount(amount);
    return itemStack;
  }

  public boolean hasAlternatives() {
    return getThing().getItemStacks().size() > 1;
  }

  public NNList<ItemStack> getAlternatives() {
    NNList<ItemStack> list = getThing().getItemStacks().copy();
    list.remove(0);
    for (ItemStack itemStack : list) {
      itemStack.setCount(amount);
    }
    return list;
  }

  public Things getThing() {
    return item.get().getThing();
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("amount".equals(name)) {
      this.amount = Integer.valueOf(value);
      return true;
    }
    if ("nbt".equals(name)) {
      this.nbt = ofString(value);
      return true;
    }
    if ("name".equals(name)) {
      @SuppressWarnings("hiding")
      Item item = new Item().setAllowDelaying(false);
      item.setName(value);
      item.readResolve();
      this.item = of(item);
      return true;
    }
    if ("required".equals(name)) {
      this.required = Boolean.parseBoolean(value);
      return true;
    }

    return super.setAttribute(factory, name, value);
  }

  @Override
  public void register(@Nonnull String recipeName) {
  }

}
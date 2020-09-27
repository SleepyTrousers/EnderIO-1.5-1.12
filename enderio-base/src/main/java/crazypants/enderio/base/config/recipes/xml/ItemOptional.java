package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import com.enderio.core.common.util.stackable.Things;

import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.potion.PotionUtil;
import info.loenwind.autoconfig.util.NullHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;

public class ItemOptional implements IRecipeConfigElement {

  protected transient boolean allowDelaying = false;
  protected Optional<String> name = empty();
  protected Optional<String> nbt = empty();
  protected Optional<String> potion = empty();
  protected transient boolean nullItem;
  protected transient final Things thing = new Things();

  public ItemOptional() {
    super();
  }

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (potion.isPresent()) {
      if (nbt.isPresent()) {
        throw new InvalidRecipeConfigException("Cannot have nbt on a potion");
      }
      PotionType potionType = PotionType.getPotionTypeForName(get(potion));
      if (potionType == null) {
        throw new InvalidRecipeConfigException("'" + get(potion) + "' is not a valid potion name");
      }
      final ItemStack stack = PotionUtils.addPotionToItemStack(PotionUtil.getEmptyPotion(false), potionType);
      if (!name.isPresent()) {
        name = ofString("item:" + NullHelper.first(stack.getItem().getRegistryName(), (Object) "").toString());
      }
      nbt = ofString(NullHelper.first(stack.getTagCompound(), (Object) "").toString());
    }

    if (!name.isPresent()) {
      if (nbt.isPresent()) {
        throw new InvalidRecipeConfigException("Cannot have nbt on an empty item");
      }
      nullItem = true;
      return this;
    }
    thing.add(get(name));
    if (nbt.isPresent()) {
      try {
        thing.setNbt(JsonToNBT.getTagFromJson(get(nbt)));
      } catch (NBTException e) {
        throw new InvalidRecipeConfigException("'" + nbt.get() + "' is not valid NBT json");
      }
    }
    return this;
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      throw new InvalidRecipeConfigException("Could not find a crafting ingredient for '" + name.get() + "'");
    }
  }

  @Override
  public boolean isValid() {
    return nullItem || (allowDelaying ? thing.isPotentiallyValid() : thing.isValid());
  }

  public @Nullable Ingredient getRecipeObject() {
    return nullItem ? null : thing.asIngredient();
  }

  public ItemStack getItemStack() {
    ItemStack itemStack = thing.getItemStack();
    itemStack.setCount(1);
    return itemStack;
  }

  public void setName(String name) {
    this.name = ofString(name);
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
    if ("potion".equals(name)) {
      this.potion = ofString(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public Things getThing() {
    return thing;
  }

  @SuppressWarnings("unchecked")
  public <T extends ItemOptional> T setAllowDelaying(boolean allowDelaying) {
    this.allowDelaying = allowDelaying;
    return (T) this;
  }

  // TODO: Can we fold this into a standard equals()?
  public boolean isSame(ItemOptional other) {
    // Note: Optional's equals() compares the values and also handles null correctly
    return name.isPresent() && name.equals(other.name) && nbt.equals(other.nbt);
  }

}

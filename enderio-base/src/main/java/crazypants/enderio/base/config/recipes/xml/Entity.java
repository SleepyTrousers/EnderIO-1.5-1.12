package crazypants.enderio.base.config.recipes.xml;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.recipes.IRecipeConfigElement;
import crazypants.enderio.base.config.recipes.InvalidRecipeConfigException;
import crazypants.enderio.base.config.recipes.StaxFactory;
import crazypants.enderio.base.recipe.spawner.EntityDataRegistry;
import crazypants.enderio.util.CapturedMob;
import net.minecraft.util.ResourceLocation;

public class Entity implements IRecipeConfigElement {

  protected Optional<String> name = empty();
  private double costMultiplier = 1;
  private boolean disabled = false;
  private boolean isDefault = false;
  private boolean isBoss = false;
  private boolean clone = false;
  private boolean soulvial = true;
  protected transient Predicate<ResourceLocation> filter = always -> false;

  public void register(String recipeName) {
    if (isDefault()) {
      EntityDataRegistry.getInstance().setDefaults(getCostMultiplier(), isDisabled(), !isSoulvial(), isClone());
    } else if (isBoss()) {
      CapturedMob.setBossesBlacklisted(!isSoulvial());
    } else {
      EntityDataRegistry.getInstance().addEntityData(name, filter, getCostMultiplier(), isDisabled(), !isSoulvial(), isClone());
    }
  }

  @SuppressWarnings("null")
  private static final Pattern WILDCARD1 = Pattern.compile("^([a-z0-9_]+|\\*):([a-z0-9_]*)\\*([a-z0-9_]*)$"),
      WILDCARD2 = Pattern.compile("^([a-z0-9_]*)\\*([a-z0-9_]*):([a-z0-9_]+|\\*)$");

  @Override
  public Object readResolve() throws InvalidRecipeConfigException {
    if (!name.isPresent()) {
      throw new InvalidRecipeConfigException("Entity name must be set");
    }
    @SuppressWarnings("hiding")
    String name = this.name.get();
    if (name.equals("*") || name.equals("*:*")) {
      isDefault = true;
    } else if (name.equals("*boss*") || name.equals("*:*boss*")) {
      isBoss = true;
      if (disabled) {
        throw new InvalidRecipeConfigException("Cannot set the 'all modded bosses' entry to 'disabled' (set 'soulvial' to false instead)");
      }
      if (clone) {
        throw new InvalidRecipeConfigException("Cannot set the 'all modded bosses' entry to 'clone'");
      }
    } else if (name.contains("*")) {
      Matcher matcher1 = WILDCARD1.matcher(name);
      Matcher matcher2 = WILDCARD2.matcher(name);
      if (matcher1.matches()) {
        filter = new ResourceLocationMatcher(matcher1.group(1), null, null, null, matcher1.group(2), matcher1.group(3));
      } else if (matcher2.matches()) {
        filter = new ResourceLocationMatcher(null, matcher2.group(1), matcher2.group(2), matcher2.group(3), null, null);
      } else {
        throw new InvalidRecipeConfigException("'" + name + "' is not a valid wildcard pattern");
      }
    } else {
      final ResourceLocation entityId = new ResourceLocation(name.trim());
      filter = id -> entityId.equals(id);
    }
    return this;
  }

  private static class ResourceLocationMatcher implements Predicate<ResourceLocation> {
    private final @Nullable String modid, modidPre, modidPost, id, pre, post;

    private static @Nullable String fix(@Nullable String id) {
      return id == null || id.trim().isEmpty() || "*".equals(id.trim()) ? null : id;
    }

    ResourceLocationMatcher(@Nullable String modid, @Nullable String modidPre, @Nullable String modidPost, @Nullable String id, @Nullable String pre,
        @Nullable String post) {
      this.modid = fix(modid);
      this.modidPre = fix(modidPre);
      this.modidPost = fix(modidPost);
      this.id = fix(id);
      this.pre = fix(pre);
      this.post = fix(post);
    }

    @Override
    public boolean test(@Nullable ResourceLocation t) {
      return t != null //
          && (modid == null || t.getResourceDomain().equals(modid)) //
          && (modidPre == null || t.getResourceDomain().startsWith(modidPre)) //
          && (modidPost == null || t.getResourceDomain().endsWith(modidPost)) //
          && (id == null || t.getResourcePath().equals(id)) //
          && (pre == null || t.getResourcePath().startsWith(pre)) //
          && (post == null || t.getResourcePath().endsWith(post));
    }
  }

  @Override
  public void enforceValidity() throws InvalidRecipeConfigException {
    if (!isValid()) {
      Log.warn("Could not find an entity for '" + name.get() + "'");
      Log.warn("Available entities are:");
      for (CapturedMob possible : CapturedMob.getAllSouls()) {
        Log.warn(" -> " + possible.getEntityName() + " (" + possible.getDisplayName() + ")");
      }
      throw new InvalidRecipeConfigException("Could not find an entity for '" + name.get() + "'");
    }
  }

  @Override
  public boolean isValid() {
    if (isDefault || isBoss) {
      return true;
    }
    for (CapturedMob possible : CapturedMob.getAllSouls()) {
      if (filter.test(possible.getEntityName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean setAttribute(StaxFactory factory, String name, String value) throws InvalidRecipeConfigException, XMLStreamException {
    if ("name".equals(name)) {
      this.name = ofString(value);
      return true;
    }
    if ("costMultiplier".equals(name)) {
      try {
        this.costMultiplier = Double.parseDouble(value);
      } catch (NumberFormatException e) {
        throw new InvalidRecipeConfigException("Invalid value in 'amount': Not a number");
      }
      return true;
    }
    if ("disabled".equals(name)) {
      this.disabled = Boolean.parseBoolean(value);
      return true;
    }
    if ("clone".equals(name)) {
      this.clone = Boolean.parseBoolean(value);
      return true;
    }
    if ("soulvial".equals(name)) {
      this.soulvial = Boolean.parseBoolean(value);
      return true;
    }

    return false;
  }

  @Override
  public boolean setElement(StaxFactory factory, String name, StartElement startElement) throws InvalidRecipeConfigException, XMLStreamException {
    return false;
  }

  public double getCostMultiplier() {
    return costMultiplier;
  }

  public boolean isDisabled() {
    return disabled;
  }

  public boolean isDefault() {
    return isDefault;
  }

  public boolean isBoss() {
    return isBoss;
  }

  public boolean isClone() {
    return clone;
  }

  public boolean isSoulvial() {
    return soulvial;
  }

}

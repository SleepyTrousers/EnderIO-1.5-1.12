package crazypants.enderio.gui.xml;

import java.util.Locale;

import javax.annotation.Nonnull;

public class ResourceLocation implements Comparable<ResourceLocation> {

  protected final @Nonnull String resourceDomain;
  protected final @Nonnull String resourcePath;

  public ResourceLocation(@Nonnull String resourceName) {
    this(findDomain(resourceName), findPath(resourceName));
  }

  public ResourceLocation(@Nonnull String resourceDomainIn, @Nonnull String resourcePathIn) {
    this.resourceDomain = resourceDomainIn.isEmpty() ? "minecraft" : resourceDomainIn.toLowerCase(Locale.ROOT);
    this.resourcePath = resourcePathIn.toLowerCase(Locale.ROOT);
  }

  @SuppressWarnings("null")
  private static @Nonnull String findDomain(@Nonnull String toSplit) {
    return toSplit.contains(":") ? toSplit.split(":", 2)[0] : "minecraft";
  }

  @SuppressWarnings("null")
  private static @Nonnull String findPath(@Nonnull String toSplit) {
    return toSplit.contains(":") ? toSplit.split(":", 2)[1] : toSplit;
  }

  public @Nonnull String getResourcePath() {
    return resourcePath;
  }

  public @Nonnull String getResourceDomain() {
    return resourceDomain;
  }

  @Override
  public @Nonnull String toString() {
    return resourceDomain + ':' + resourcePath;
  }

  @Override
  public int hashCode() {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other != null && toString().equals(other.toString());
  }

  @Override
  public int compareTo(ResourceLocation other) {
    return toString().compareTo(other.toString());
  }

}
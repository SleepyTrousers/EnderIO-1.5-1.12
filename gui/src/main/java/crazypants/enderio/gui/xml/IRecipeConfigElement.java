package crazypants.enderio.gui.xml;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import crazypants.enderio.gui.xml.builder.IXMLBuilder;

public interface IRecipeConfigElement {

  void validate() throws InvalidRecipeConfigException;

  default @Nonnull Object readResolve() throws XMLStreamException {
    return this;
  }

  boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value) throws InvalidRecipeConfigException, XMLStreamException;

  boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException;

  default @Nonnull ElementList getSubElements() {
    return ElementList.of();
  }

  /**
   * Sets a human-readable designation of the source of the XML, e.g. the filename or the IMC sender.
   */
  default void setSource(@Nonnull String source) {
  }

  default @Nonnull String getName() {
    return "unnamed";
  }

  default @Nonnull String getSource() {
    return "unknown";
  }

  /**
   * Bouncer for {@link Optional#of(Object)} with added null annotations.
   */
  @SuppressWarnings("null")
  default @Nonnull <T> Optional<T> of(@Nonnull T value) {
    return Optional.of(value);
  }

  /**
   * Helper method to convert a String to an Optional. <code>null</code>, the empty String and Strings only containing whitespace will be mapped top empty.
   * Otherwise the String will be trimmed.
   */
  @SuppressWarnings("null")
  default @Nonnull Optional<String> ofString(@Nullable String value) {
    return value == null || value.isEmpty() ? empty() : Optional.of(value.trim());
  }

  /**
   * Bouncer for {@link Optional#ofNullable(Object)} with added null annotations.
   */
  @SuppressWarnings("null")
  default @Nonnull <T> Optional<T> ofNullable(@Nullable T value) {
    return Optional.ofNullable(value);
  }

  /**
   * Bouncer for {@link Optional#empty()} with added null annotations.
   */
  @SuppressWarnings("null")
  default @Nonnull <T> Optional<T> empty() {
    return Optional.empty();
  }

  /**
   * Bouncer for {@link Optional#get()} with added null annotations.
   */
  @SuppressWarnings("null")
  default @Nonnull <T> T get(@Nonnull Optional<T> o) {
    return o.get();
  }

  void write(@Nonnull IXMLBuilder parent);

  /**
   * Can this element coexist with another one that has the same name?
   * <p>
   * Usually they cannot as recipes are indexed by name, but there are some exceptions, e.g. aliases.
   */
  default boolean supportsDuplicates() {
    return false;
  }
}
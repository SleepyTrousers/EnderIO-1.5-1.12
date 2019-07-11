package crazypants.enderio.base.config.recipes;

import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import org.apache.logging.log4j.util.Strings;

public interface IRecipeConfigElement {

  /**
   * Called after the object has been populated from XML. Needs to check if it is formally correct.
   * 
   * @return The object itself
   * @throws InvalidRecipeConfigException
   * @throws XMLStreamException
   */
  @Nonnull
  Object readResolve() throws InvalidRecipeConfigException, XMLStreamException;

  /**
   * Determine if an object is semantically valid and throw a nice user-presentable error if not.
   * 
   * @throws InvalidRecipeConfigException
   */
  void enforceValidity() throws InvalidRecipeConfigException;

  /**
   * Determine if an object is semantically valid.
   * 
   * @return
   */
  boolean isValid();

  boolean setAttribute(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull String value) throws InvalidRecipeConfigException, XMLStreamException;

  boolean setElement(@Nonnull StaxFactory factory, @Nonnull String name, @Nonnull StartElement startElement)
      throws InvalidRecipeConfigException, XMLStreamException;

  /**
   * Sets a human-readable designation of the source of the XML, e.g. the filename or the IMC sender.
   */
  default void setSource(@Nonnull String source) {
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
    return Strings.isBlank(value) ? empty() : Optional.of(value.trim());
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

}
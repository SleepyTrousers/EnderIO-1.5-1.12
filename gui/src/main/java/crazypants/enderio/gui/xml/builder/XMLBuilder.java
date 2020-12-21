package crazypants.enderio.gui.xml.builder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

public class XMLBuilder implements IXMLBuilder.Impl {

  private final @Nonnull String indent;
  private final @Nonnull String tagname;
  private final @Nonnull Map<String, String> attributes = new LinkedHashMap<>();
  private final @Nonnull List<XMLBuilder> children = new ArrayList<>();
  private final @Nonnull List<String> comments = new ArrayList<>();

  private XMLBuilder(@Nonnull String indent, @Nonnull String tagname) {
    this.indent = indent;
    this.tagname = tagname;
  }

  public static @Nonnull IXMLBuilder root() {
    return new XMLBuilder(ROOT, ROOT);
  }

  public static @Nonnull IXMLBuilder single() {
    return new XMLBuilder(ROOT, SINGLE);
  }

  @Override
  public @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull String value, String defaultValue) {
    if (!value.isEmpty()) {
      attributes.put(key, value);
    } else if (defaultValue != null) {
      attributes.put(key, defaultValue);
    }
    return this;
  }

  @Override
  public @Nonnull IXMLBuilder comment(@Nonnull String line) {
    comments.add(line);
    return this;
  }

  @Override
  public @Nonnull IXMLBuilder child(@Nonnull String name) {
    XMLBuilder result = new XMLBuilder(ROOT.equals(indent) ? "" : (indent + "\t"), name);
    children.add(result);
    return result;
  }

  @Override
  public String toString() {
    return writeXML();
  }

  @Override
  public @Nonnull String getIndent() {
    return indent;
  }

  @Override
  public @Nonnull String getTagname() {
    return tagname;
  }

  @Override
  public @Nonnull Map<String, String> getAttributes() {
    return attributes;
  }

  @Override
  public @Nonnull List<XMLBuilder> getChildren() {
    return children;
  }

  @Override
  public @Nonnull List<String> getComments() {
    return comments;
  }

}

package crazypants.enderio.gui.xml.builder;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nonnull;

public interface IXMLBuilder {

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull String value) {
    return attribute(key, value, false);
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull String value, boolean skipEmpty) {
    return attribute(key, value, skipEmpty ? null : "");
  }

  @Nonnull
  IXMLBuilder attribute(@Nonnull String key, @Nonnull String value, String defaultValue);

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, boolean value) {
    return attribute(key, Boolean.toString(value));
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, int value) {
    return attribute(key, Integer.toString(value));
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, float value) {
    return attribute(key, Float.toString(value));
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, double value) {
    return attribute(key, Double.toString(value));
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull Optional<? extends Object> value) {
    return attribute(key, value, false, false);
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull Optional<? extends Object> value, boolean skipMissing) {
    return attribute(key, value, skipMissing, skipMissing);
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull Optional<? extends Object> value, boolean skipMissing, boolean skipEmpty) {
    if (value.isPresent()) {
      return attribute(key, Impl.nn(value.get().toString()), skipEmpty);
    } else if (!skipMissing) {
      return attribute(key, "", skipEmpty);
    }
    return this;
  }

  default @Nonnull IXMLBuilder attribute(@Nonnull String key, @Nonnull Optional<? extends Object> value, @Nonnull Object defaultValue) {
    if (value.isPresent()) {
      return attribute(key, Impl.nn(value.get().toString()), Impl.nn(defaultValue.toString()));
    } else {
      return attribute(key, Impl.nn(defaultValue.toString()), "");
    }
  }

  @Nonnull
  IXMLBuilder comment(@Nonnull String line);

  @Nonnull
  IXMLBuilder child(@Nonnull String name);

  /**
   * Call a method that wants to create a child element but prevent a child being created and force the attributes to be added to this element instead.
   */
  default @Nonnull IXMLBuilder superCall(Impl.NNConsumer<IXMLBuilder> superWrite) {
    superWrite.accept(new XMLBuilderWrapper(this));
    return this;
  }

  // ------------------------------------------ //

  interface Impl extends IXMLBuilder {

    @FunctionalInterface
    public interface NNConsumer<T> {
      void accept(@Nonnull T t);
    }

    final static @Nonnull String NEWLINE = "\n";
    final static @Nonnull String ROOT = "<>";

    @Nonnull
    List<String> getComments();

    @Nonnull
    List<XMLBuilder> getChildren();

    @Nonnull
    Map<String, String> getAttributes();

    @Nonnull
    String getTagname();

    @Nonnull
    String getIndent();

    default String writeXML() {
      StringBuilder builder = new StringBuilder();
      if (ROOT.equals(getIndent())) {
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        builder.append(NEWLINE);
      } else {
        for (String comment : getComments()) {
          builder.append(getIndent());
          builder.append("<!-- ");
          builder.append(quotComment(comment));
          builder.append(" -->");
          builder.append(NEWLINE);
        }
        builder.append(getIndent());
        builder.append("<");
        builder.append(getTagname());
        for (Entry<String, String> attribute : getAttributes().entrySet()) {
          builder.append(" ");
          builder.append(attribute.getKey());
          builder.append("=");
          builder.append(quot(attribute.getValue()));
        }
        if (getChildren().isEmpty()) {
          builder.append(" />");
          builder.append(NEWLINE);
        } else {
          builder.append(NEWLINE);
          for (IXMLBuilder child : getChildren()) {
            builder.append(child);
          }
          builder.append(getIndent());
          builder.append("</");
          builder.append(getTagname());
          builder.append(">");
          builder.append(NEWLINE);
        }
      }
      return builder.toString();
    }

    static @Nonnull String nn(String s) {
      return s != null ? s : "";
    }

    static String quot(String s) {
      if (s.contains("'")) {
        s = "\"" + s.replaceAll("\"", "&quot;") + "\"";
      } else {
        s = "'" + s.replaceAll("'", "&quot;") + "'";
      }
      return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\\p{Cntrl}", "");
    }

    static String quotComment(String s) {
      return s.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll("\\p{Cntrl}", "").replaceAll("--+", "-");
    }

  }

}
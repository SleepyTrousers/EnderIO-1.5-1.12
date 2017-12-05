package crazypants.enderio.base.config;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.network.ThreadedNetworkWrapper;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NullHelper;
import com.enderio.core.common.util.stackable.RebuildableThings;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.config.Config.Section;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class ValueFactory {

  protected final @Nonnull ThreadedNetworkWrapper network;
  protected Configuration config = null;
  protected boolean inInit = false;
  protected Map<String, Object> serverConfig = null;
  protected int generation = 0;
  protected final @Nonnull NNList<AbstractValue<?>> syncValues = new NNList<>();
  protected final @Nonnull NNList<AbstractValue<?>> preloadValues = new NNList<>();

  public ValueFactory(@Nonnull ThreadedNetworkWrapper network) {
    MinecraftForge.EVENT_BUS.register(this);
    this.network = network;
  }

  public void setConfig(Configuration config) {
    this.config = config;
    generation++;
    inInit = true;
    for (AbstractValue<?> value : preloadValues) {
      value.get();
    }
    inInit = false;
    if (config.hasChanged()) {
      config.save();
    }
    // Note: Forge trashes the config when loading it from disk, so we need to re-configure all values every time that happens
    // preloadValues.clear();
  }

  public @Nonnull IValue<Integer> make(@Nonnull Section section, @Nonnull String keyname, int defaultValue, @Nonnull String text) {
    return new IntValue(section.name, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Double> make(@Nonnull Section section, @Nonnull String keyname, double defaultValue, @Nonnull String text) {
    return new DoubleValue(section.name, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Float> make(@Nonnull Section section, @Nonnull String keyname, float defaultValue, @Nonnull String text) {
    return new FloatValue(section.name, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<String> make(@Nonnull Section section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
    return new StringValue(section.name, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<Boolean> make(@Nonnull Section section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
    return new BooleanValue(section.name, keyname, defaultValue, text).preload();
  }

  public @Nonnull IValue<RebuildableThings> make(@Nonnull Section section, @Nonnull String keyname, @Nonnull RebuildableThings defaultValue,
      @Nonnull String text) {
    return new ThingsValue(section.name, keyname, defaultValue, text).preload();
  }

  public interface IValue<T> {
    @Nonnull
    T get();

    /**
     * Marks this config value as one that needs to be synced from the server to the client. Returns the object itself for chaining.
     * 
     * Note: Not all config values support this.
     */
    @Nonnull
    default IValue<T> sync() {
      return this;
    }

    @Nonnull
    default IValue<T> setRange(double min, double max) {
      setMin(min);
      setMax(max);
      return this;
    }

    @Nonnull
    default IValue<T> setMin(double min) {
      return this;
    }

    @Nonnull
    default IValue<T> setMax(double max) {
      return this;
    }

  }

  public abstract class AbstractValue<T> implements IValue<T> {

    protected int valueGeneration = 0;
    protected final @Nonnull String section, keyname, text;
    protected final @Nonnull T defaultValue;
    protected @Nullable T value = null;
    protected Double minValue, maxValue;

    protected AbstractValue(@Nonnull String section, @Nonnull String keyname, @Nonnull T defaultValue, @Nonnull String text) {
      this.section = section;
      this.keyname = keyname;
      this.text = text;
      this.defaultValue = defaultValue;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    @Override
    public T get() {
      if (value == null || valueGeneration != generation) {
        if (serverConfig != null && serverConfig.containsKey(keyname)) {
          try {
            value = (T) serverConfig.get(keyname);
          } catch (java.lang.ClassCastException e) {
            // I'm quite sure this will not happen here but when the caller gets the value. I'm not sure how to catch it at all, but it actually should not be
            // possible to happen as the server config is generated in code, and that code should be the same on server and client.
            Log.error("Server config value " + keyname + " is invalid");
            value = null;
          }
        } else {
          value = makeValue();
          if (!inInit && config.hasChanged()) {
            config.save();
          }
        }
        valueGeneration = generation;
      }
      return NullHelper.first(value, defaultValue);
    }

    protected abstract @Nullable T makeValue();

    @Override
    @Nonnull
    public IValue<T> setMin(double min) {
      this.minValue = min;
      return this;
    }

    @Override
    @Nonnull
    public IValue<T> setMax(double max) {
      this.maxValue = max;
      return this;
    }

    @Override
    @Nonnull
    public IValue<T> sync() {
      syncValues.add(this);
      return this;
    };

    @Nonnull
    public IValue<T> preload() {
      preloadValues.add(this);
      return this;
    };

    public void save(final ByteBuf buf) {
      final byte[] bytes = keyname.getBytes(Charset.forName("UTF-8"));
      buf.writeInt(bytes.length);
      buf.writeBytes(bytes);
      DataTypes dataType = getDataType();
      buf.writeByte(dataType.ordinal());
      dataType.saveValue(buf, get());
    }

    protected abstract DataTypes getDataType();
  }

  protected final void read(final ByteBuf buf) {
    Map<String, Object> result = new HashMap<>();
    boolean reading = true;
    while (reading) {
      final int len = buf.readInt();
      if (len > 0) {
        final byte[] bytes = new byte[len];
        buf.readBytes(bytes, 0, len);
        String keyName = new String(bytes, Charset.forName("UTF-8"));
        byte dataTypeByte = buf.readByte();
        DataTypes dataType = DataTypes.values()[dataTypeByte];
        Object readValue = dataType.readValue(buf);
        result.put(keyName, readValue);
      } else {
        reading = false;
      }
    }
    Log.info("Read " + result.size() + " config values from server packet");
    serverConfig = result;
    generation++;
  }

  protected final void save(final ByteBuf buf) {
    for (AbstractValue<?> value : syncValues) {
      value.save(buf);
    }
    buf.writeInt(0);
  }

  @SubscribeEvent
  public void onPlayerLoggon(final PlayerLoggedInEvent evt) {
    network.sendTo(new PacketConfigSyncNew(this), (EntityPlayerMP) evt.player);
    Log.info("Sent config to player " + evt.player.getDisplayNameString());
  }

  @SubscribeEvent
  public void onPlayerLogout(final ClientDisconnectionFromServerEvent event) {
    Log.info("Removed server config override");
    serverConfig = null;
    generation++;
  }

  public class IntValue extends AbstractValue<Integer> {

    protected IntValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Integer defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Integer makeValue() {
      return config.getInt(keyname, section, defaultValue, minValue != null ? minValue.intValue() : Integer.MIN_VALUE,
          maxValue != null ? maxValue.intValue() : Integer.MAX_VALUE, text);
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.INTEGER;
    }

  }

  public class DoubleValue extends AbstractValue<Double> {

    protected DoubleValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Double defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Double makeValue() {
      String comment = text + " [range: " + (minValue != null ? minValue : Double.NEGATIVE_INFINITY) + " ~ " + (maxValue != null ? maxValue : Double.MAX_VALUE)
          + ", default: " + defaultValue + "]";
      final Property property = config.get(section, keyname, defaultValue, comment);
      if (minValue != null) {
        property.setMinValue(minValue);
      }
      if (maxValue != null) {
        property.setMaxValue(maxValue);
      }
      return property.getDouble(defaultValue);
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.DOUBLE;
    }

  }

  public class FloatValue extends AbstractValue<Float> {

    protected FloatValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Float defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Float makeValue() {
      return config.getFloat(keyname, section, defaultValue, minValue == null ? Float.NEGATIVE_INFINITY : minValue.floatValue(),
          maxValue == null ? Float.MAX_VALUE : maxValue.floatValue(), text);
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.FLOAT;
    }

  }

  public class StringValue extends AbstractValue<String> {

    protected StringValue(@Nonnull String section, @Nonnull String keyname, @Nonnull String defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable String makeValue() {
      return config.getString(keyname, section, defaultValue, text);
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.STRING;
    }

  }

  public class BooleanValue extends AbstractValue<Boolean> {

    protected BooleanValue(@Nonnull String section, @Nonnull String keyname, @Nonnull Boolean defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable Boolean makeValue() {
      return config.getBoolean(keyname, section, defaultValue, text);
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.BOOLEAN;
    }

  }

  public class ThingsValue extends AbstractValue<RebuildableThings> {

    protected ThingsValue(@Nonnull String section, @Nonnull String keyname, @Nonnull RebuildableThings defaultValue, @Nonnull String text) {
      super(section, keyname, defaultValue, text);
    }

    @Override
    protected @Nullable RebuildableThings makeValue() {
      return new RebuildableThings(config.getStringList(keyname, section, defaultValue.getNameList().toArray(new String[0]), text));
    }

    @Override
    protected DataTypes getDataType() {
      return DataTypes.NONE;
    }

    @Override
    public void save(ByteBuf buf) {
      Log.warn("StringList config options cannot be synced to the client. This is a coding error.");
    }
  }

  protected static enum DataTypes {
    INTEGER {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
        buf.writeInt((int) value);
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        return buf.readInt();
      }
    },
    DOUBLE {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
        buf.writeDouble((double) value);
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        return buf.readDouble();
      }
    },
    FLOAT {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
        buf.writeFloat((float) value);
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        return buf.readFloat();
      }
    },
    STRING {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
        final byte[] vbytes = ((String) value).getBytes(Charset.forName("UTF-8"));
        buf.writeInt(vbytes.length);
        buf.writeBytes(vbytes);
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        final int len = buf.readInt();
        final byte[] bytes = new byte[len];
        buf.readBytes(bytes, 0, len);
        return new String(bytes, Charset.forName("UTF-8"));
      }
    },
    BOOLEAN {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
        buf.writeBoolean((boolean) value);
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        return buf.readBoolean();
      }
    },
    NONE {
      @Override
      protected void saveValue(ByteBuf buf, @Nonnull Object value) {
      }

      @Override
      protected Object readValue(ByteBuf buf) {
        return null;
      }
    };

    protected abstract void saveValue(final ByteBuf buf, @Nonnull Object value);

    protected abstract Object readValue(final ByteBuf buf);
  }
}

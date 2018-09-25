package crazypants.enderio.base.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.base.config.config.DarkSteelConfig;
import crazypants.enderio.base.config.factory.FactoryManager;
import crazypants.enderio.base.lang.Lang;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.ConfigGuiType;
import net.minecraftforge.fml.client.config.GuiConfigEntries.IConfigEntry;
import net.minecraftforge.fml.client.config.GuiEditArrayEntries.IArrayEntry;
import net.minecraftforge.fml.client.config.IConfigElement;

/**
 ** Forge's {@link ConfigElement} is written in a way to prevent sub-classing, so we had to copy it.
 **/
public class ConfigElementEio implements IConfigElement {
  private Property prop;
  private Property.Type type;
  private boolean isProperty;
  private ConfigCategory category;

  public ConfigElementEio(ConfigCategory category) {
    this.category = category;
    this.isProperty = false;
  }

  public ConfigElementEio(Property prop) {
    this.prop = prop;
    this.type = prop.getType();
    this.isProperty = true;
  }

  @Override
  public List<IConfigElement> getChildElements() {
    if (!isProperty) {
      List<IConfigElement> elements = new ArrayList<IConfigElement>();

      for (ConfigCategory subcat : category.getChildren()) {
        if (subcat.showInGui())
          elements.add(new ConfigElementEio(subcat));
      }
      for (Property element : category.getOrderedValues()) {
        if (element.showInGui())
          elements.add(new ConfigElementEio(element));
      }

      return elements;
    }
    return null;
  }

  @Override
  public String getName() {
    return isProperty ? prop.getName() : category.getName();
  }

  @Override
  public boolean isProperty() {
    return isProperty;
  }

  @Override
  public Class<? extends IConfigEntry> getConfigEntryClass() {
    return isProperty ? prop.getConfigEntryClass() : category.getConfigEntryClass();
  }

  @Override
  public Class<? extends IArrayEntry> getArrayEntryClass() {
    return isProperty ? prop.getArrayEntryClass() : null;
  }

  @Override
  public String getQualifiedName() {
    return isProperty ? prop.getName() : category.getQualifiedName();
  }

  @Override
  public ConfigGuiType getType() {
    return isProperty ? getType(this.prop) : ConfigGuiType.CONFIG_CATEGORY;
  }

  public static ConfigGuiType getType(Property prop) {
    switch (prop.getType()) {
    case BOOLEAN:
      return ConfigGuiType.BOOLEAN;
    case COLOR:
      return ConfigGuiType.COLOR;
    case DOUBLE:
      return ConfigGuiType.DOUBLE;
    case INTEGER:
      return ConfigGuiType.INTEGER;
    case MOD_ID:
      return ConfigGuiType.MOD_ID;
    case STRING:
    default:
      return ConfigGuiType.STRING;
    }
  }

  @Override
  public boolean isList() {
    return isProperty && prop.isList();
  }

  @Override
  public boolean isListLengthFixed() {
    return isProperty && prop.isListLengthFixed();
  }

  @Override
  public int getMaxListLength() {
    return isProperty ? prop.getMaxListLength() : -1;
  }

  @Override
  public String getComment() {
    String raw = NullHelper.first(isProperty ? prop.getComment() : category.getComment(), "").replaceFirst("\\s*\\[.*\\]", "");
    if (isSynced()) {
      return Lang.NETWORK_CONFIG_CONNECTED.get(raw.replace(FactoryManager.SERVER_OVERRIDE, ""));
    } else if (raw.contains(FactoryManager.SERVER_SYNC)) {
      return Lang.NETWORK_CONFIG_OFFLINE.get(raw.replace(FactoryManager.SERVER_SYNC, ""));
    } else if (raw.contains(FactoryManager.SERVER_OVERRIDE)) {
      return Lang.NETWORK_CONFIG_SYNC.get(raw.replace(FactoryManager.SERVER_OVERRIDE, ""));
    } else {
      return raw;
    }
  }

  @Override
  public boolean isDefault() {
    return !isProperty || prop.isDefault();
  }

  @Override
  public void setToDefault() {
    if (isProperty) {
      prop.setToDefault();
    }
  }

  @Override
  public boolean requiresWorldRestart() {
    return isSynced() || (isProperty ? prop.requiresWorldRestart() : category.requiresWorldRestart());
  }

  protected boolean isSynced() {
    // we cheat a bit here and assume that when Ender IO has server overrides, all submod have them
    return isProperty && DarkSteelConfig.F_SWORD.isServerOverrideInPlace() && prop.getComment().contains(FactoryManager.SERVER_OVERRIDE);
  }

  @Override
  public boolean showInGui() {
    // properties with a null comments only exist in the config file, not in code
    return isProperty ? (prop.showInGui() && prop.getComment() != null && !prop.getComment().trim().isEmpty()) : category.showInGui();
  }

  @Override
  public boolean requiresMcRestart() {
    return isProperty ? prop.requiresMcRestart() : category.requiresMcRestart();
  }

  @Override
  public String[] getValidValues() {
    return isProperty ? prop.getValidValues() : null;
  }

  @Override
  public String getLanguageKey() {
    return isProperty ? prop.getLanguageKey() : category.getLanguagekey();
  }

  @Override
  public Object getDefault() {
    return isProperty ? prop.getDefault() : null;
  }

  @Override
  public Object[] getDefaults() {
    if (isProperty) {
      String[] aVal = prop.getDefaults();
      if (type == Property.Type.BOOLEAN) {
        Boolean[] ba = new Boolean[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ba[i] = Boolean.valueOf(aVal[i]);
        }
        return ba;
      } else if (type == Property.Type.DOUBLE) {
        Double[] da = new Double[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          da[i] = Double.valueOf(aVal[i].toString());
        }
        return da;
      } else if (type == Property.Type.INTEGER) {
        Integer[] ia = new Integer[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ia[i] = Integer.valueOf(aVal[i].toString());
        }
        return ia;
      } else
        return aVal;
    }
    return null;
  }

  @Override
  public Pattern getValidationPattern() {
    return isProperty ? prop.getValidationPattern() : null;
  }

  @Override
  public Object get() {
    return isProperty ? prop.getString() : null;
  }

  @Override
  public Object[] getList() {
    if (isProperty) {
      String[] aVal = prop.getStringList();
      if (type == Property.Type.BOOLEAN) {
        Boolean[] ba = new Boolean[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ba[i] = Boolean.valueOf(aVal[i]);
        }
        return ba;
      } else if (type == Property.Type.DOUBLE) {
        Double[] da = new Double[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          da[i] = Double.valueOf(aVal[i].toString());
        }
        return da;
      } else if (type == Property.Type.INTEGER) {
        Integer[] ia = new Integer[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ia[i] = Integer.valueOf(aVal[i].toString());
        }
        return ia;
      } else
        return aVal;
    }
    return null;
  }

  @Override
  public void set(Object value) {
    if (isProperty) {
      if (type == Property.Type.BOOLEAN) {
        prop.set(Boolean.parseBoolean(value.toString()));
      } else if (type == Property.Type.DOUBLE) {
        prop.set(Double.parseDouble(value.toString()));
      } else if (type == Property.Type.INTEGER) {
        prop.set(Integer.parseInt(value.toString()));
      } else {
        prop.set(value.toString());
      }
    }
  }

  @Override
  public void set(Object[] aVal) {
    if (isProperty) {
      if (type == Property.Type.BOOLEAN) {
        boolean[] ba = new boolean[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ba[i] = Boolean.valueOf(aVal[i].toString());
        }
        prop.set(ba);
      } else if (type == Property.Type.DOUBLE) {
        double[] da = new double[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          da[i] = Double.valueOf(aVal[i].toString());
        }
        prop.set(da);
      } else if (type == Property.Type.INTEGER) {
        int[] ia = new int[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          ia[i] = Integer.valueOf(aVal[i].toString());
        }
        prop.set(ia);
      } else {
        String[] is = new String[aVal.length];
        for (int i = 0; i < aVal.length; i++) {
          is[i] = aVal[i].toString();
        }
        prop.set(is);
      }
    }
  }

  @Override
  public Object getMinValue() {
    return isProperty ? prop.getMinValue() : null;
  }

  @Override
  public Object getMaxValue() {
    return isProperty ? prop.getMaxValue() : null;
  }

}
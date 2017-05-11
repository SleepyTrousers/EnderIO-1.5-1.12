package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.enderio.core.common.util.NullHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.ModObject.blockIngotStorage;
import static crazypants.enderio.ModObject.itemAlloy;

public enum Alloy implements IStringSerializable {

  ELECTRICAL_STEEL("electricalSteel", 6.0f, 0xa9a9a9, 1202),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f, 0xd9934c, 855),
  VIBRANT_ALLOY("vibrantAlloy", 4.0f, 0xb6c870, 640),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f, 0xb12727, 1084),
  CONDUCTIVE_IRON("conductiveIron", 5.2f, 0xab5d5f, 1127),
  PULSATING_IRON("pulsatingIron", 7.0f, 0x2c9044, 1132),
  DARK_STEEL("darkSteel", 10.0f, 0x6c6c6c, 1540),
  SOULARIUM("soularium", 10.0f, 0x695b4d, 363);

  public static List<ResourceLocation> resources() {
    List<ResourceLocation> res = new ArrayList<ResourceLocation>(values().length);
    for(Alloy a : values()) {
      res.add(new ResourceLocation(a.iconKey));
    }
    return res;
  }
  
  public final @Nonnull String baseName;
  public final @Nonnull String unlocalisedName;
  public final @Nonnull String iconKey;
  private final @Nonnull List<String> oreIngots = new ArrayList<String>();
  private final @Nonnull List<String> oreBlocks = new ArrayList<String>();
  private final float hardness;
  private final int color;
  private final int meltingPoint; // in °C

  private Alloy(@Nonnull String baseName, float hardness, @Nullable String oreDictName, int color, int meltingPoint) {
    this.baseName = baseName;
    this.unlocalisedName = "enderio." + baseName;
    this.iconKey = "enderio:" + baseName;
    if(oreDictName != null) {
      this.oreIngots.add("ingot" + StringUtils.capitalize(oreDictName));
      this.oreBlocks.add("block" + StringUtils.capitalize(oreDictName));
    }
    this.oreIngots.add("ingot" + StringUtils.capitalize(baseName));
    this.oreBlocks.add("block" + StringUtils.capitalize(baseName));
    this.hardness = hardness;
    this.color = color;
    this.meltingPoint = meltingPoint;
  }

  private Alloy(@Nonnull String baseName, float hardness, int color, int meltingPoint) {
    this(baseName, hardness, null, color, meltingPoint);
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public @Nonnull String getFluidName() {
    return NullHelper.notnullJ(baseName.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public float getHardness() {
    return hardness;
  }

  public int getColor() {
    return color;
  }

  public int getMeltingPoint() {
    return meltingPoint;
  }

  public @Nonnull ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public @Nonnull ItemStack getStackIngot(int size) {
    return new ItemStack(itemAlloy.getItem(), size, ordinal());
  }

  public @Nonnull ItemStack getStackBlock() {
    return getStackBlock(1);
  }

  public @Nonnull ItemStack getStackBlock(int size) {
    return new ItemStack(blockIngotStorage.getBlock(), size, ordinal());
  }

  public @Nonnull String getOreIngot() {
    return NullHelper.notnull(oreIngots.get(0), "Data corruption");
  }

  public @Nonnull String getOreBlock() {
    return NullHelper.notnull(oreBlocks.get(0), "Data corruption");
  }

  public @Nonnull List<String> getOreIngots() {
    return oreIngots;
  }

  public @Nonnull List<String> getOreBlocks() {
    return oreBlocks;
  }

  @Override
  public @Nonnull String getName() {
    return NullHelper.notnullJ(baseName.toLowerCase(Locale.ENGLISH), "String.toLowerCase()");
  }

  public static @Nonnull Alloy getTypeFromMeta(int meta) {
    return NullHelper.notnullJ(values()[meta >= 0 && meta < values().length ? meta : 0], "Enum.values()");
  }

  public static int getMetaFromType(Alloy value) {
    return value.ordinal();
  }

}

package crazypants.enderio.material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import crazypants.enderio.EnderIO;
import crazypants.util.NullHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;

import static crazypants.enderio.ModObject.blockIngotStorage;

public enum Alloy implements IStringSerializable {

  ELECTRICAL_STEEL("electricalSteel", 6.0f),
  ENERGETIC_ALLOY("energeticAlloy", 7.0f),
  VIBRANT_ALLOY("vibrantAlloy", 4.0f),
  REDSTONE_ALLOY("redstoneAlloy", 1.0f),
  CONDUCTIVE_IRON("conductiveIron", 5.2f),
  PULSATING_IRON("pulsatingIron", 7.0f),
  DARK_STEEL("darkSteel", 10.0f),
  SOULARIUM("soularium", 10.0f);

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

  private Alloy(@Nonnull String baseName, float hardness, @Nullable String oreDictName) {
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
  }

  private Alloy(@Nonnull String baseName, float hardness) {
    this(baseName, hardness, null);
  }

  public @Nonnull String getBaseName() {
    return baseName;
  }

  public float getHardness() {
    return hardness;
  }

  public @Nonnull ItemStack getStackIngot() {
    return getStackIngot(1);
  }

  public @Nonnull ItemStack getStackIngot(int size) {
    return new ItemStack(EnderIO.itemAlloy, size, ordinal());
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

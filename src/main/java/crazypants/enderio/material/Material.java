package crazypants.enderio.material;

import crazypants.enderio.EnderIO;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

public enum Material {
    SILICON("silicon"),
    CONDUIT_BINDER("conduitBinder"),
    BINDER_COMPOSITE("binderComposite"),
    PHASED_IRON_NUGGET("phasedIronNugget"),
    VIBRANT_NUGGET("vibrantNugget"),
    PULSATING_CYSTAL("pulsatingCrystal", true),
    VIBRANT_CYSTAL("vibrantCrystal", true),
    DARK_GRINDING_BALL("darkGrindingBall"),
    ENDER_CRYSTAL("enderCrystal", true),
    ATTRACTOR_CRYSTAL("attractorCrystal", true),
    WEATHER_CRYSTAL("weatherCrystal", true),
    END_STEEL_NUGGET("endSteelNugget"),
    DARK_STEEL_ROD("darkSteelRod"),
    PRECIENT_CRYSTAL("precientCrystal", true),
    PULSATING_POWDER("pulsatingPowder", true),
    VIBRANT_POWDER("vibrantPowder", true),
    ENDER_POWDER("enderCrystalPowder", true),
    PRECIENT_POWDER("precientPowder", true);

    public final String unlocalisedName;
    public final String iconKey;
    public final String oreDict;
    public final boolean hasEffect;

    private Material(String unlocalisedName) {
        this(unlocalisedName, false);
    }

    private Material(String unlocalisedName, boolean hasEffect) {
        this.unlocalisedName = "enderio." + unlocalisedName;
        this.iconKey = "enderio:" + unlocalisedName;
        this.hasEffect = hasEffect;
        this.oreDict = "item" + StringUtils.capitalize(unlocalisedName);
    }

    public static void registerOres(Item item) {
        for (Material m : values()) {
            OreDictionary.registerOre(m.oreDict, new ItemStack(item, 1, m.ordinal()));
        }
    }

    public ItemStack getStack() {
        return getStack(1);
    }

    public ItemStack getStack(int size) {
        return new ItemStack(EnderIO.itemMaterial, size, ordinal());
    }
}

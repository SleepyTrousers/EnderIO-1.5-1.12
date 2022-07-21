package crazypants.enderio.material;

import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraftforge.oredict.OreDictionary;

public class BlockDarkSteelBars extends BlockPane {

    public static class BlockSoulariumBars extends BlockDarkSteelBars {

        public BlockSoulariumBars() {
            super(ModObject.blockSoulariumBars, "blockSoulariumBars");
            setBlockName(ModObject.blockSoulariumBars.unlocalisedName);
        }

        public static BlockSoulariumBars create() {
            BlockSoulariumBars res = new BlockSoulariumBars();
            res.init();
            return res;
        }
    }

    public static class BlockEndSteelBars extends BlockDarkSteelBars {

        public BlockEndSteelBars() {
            super(ModObject.blockEndSteelBars, "blockEndSteelBars");
            setBlockName(ModObject.blockEndSteelBars.unlocalisedName);
        }

        public static BlockEndSteelBars create() {
            BlockEndSteelBars res = new BlockEndSteelBars();
            res.init();
            return res;
        }
    }

    public static BlockDarkSteelBars create() {
        BlockDarkSteelBars res = new BlockDarkSteelBars();
        res.init();
        return res;
    }

    ModObject modObj;

    public BlockDarkSteelBars(ModObject obj, String name) {
        super("enderio:" + name, "enderio:" + name, Material.iron, true);
        this.modObj = obj;
        setResistance(2000.0F); // TNT Proof
        setHardness(5.0F);
        setStepSound(soundTypeMetal);
        setBlockName(obj.unlocalisedName);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    public BlockDarkSteelBars() {
        this(ModObject.blockDarkIronBars, "blockDarkIronBars");
    }

    protected void init() {
        GameRegistry.registerBlock(this, BlockItemDarkIronBars.class, modObj.unlocalisedName);
        OreDictionary.registerOre("barsIron", this);
    }
}

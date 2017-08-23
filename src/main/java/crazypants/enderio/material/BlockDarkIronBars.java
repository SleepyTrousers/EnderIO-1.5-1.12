package crazypants.enderio.material;

import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.registry.GameRegistry;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;

public class BlockDarkIronBars extends BlockPane {
  
  public static BlockDarkIronBars create() {
    BlockDarkIronBars res = new BlockDarkIronBars();
    res.init();
    return res;
  }

  public BlockDarkIronBars() {
    super("enderio:blockDarkIronBars", "enderio:blockDarkIronBars", Material.iron, true);
    setResistance(2000.0F); //TNT Proof
    setHardness(5.0F);
    setStepSound(soundTypeMetal);
    setBlockName(ModObject.blockDarkIronBars.unlocalisedName);
    setCreativeTab(EnderIOTab.tabEnderIO);
  }
  
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemDarkIronBars.class, ModObject.blockDarkIronBars.unlocalisedName);
    OreDictionary.registerOre("barsIron", this);
  }

}

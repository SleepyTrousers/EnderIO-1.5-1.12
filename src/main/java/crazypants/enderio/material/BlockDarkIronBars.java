package crazypants.enderio.material;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

public class BlockDarkIronBars extends BlockPane {
  
  public static BlockDarkIronBars create() {
    BlockDarkIronBars res = new BlockDarkIronBars();
    res.init();
    return res;
  }

  public BlockDarkIronBars() {
    super(Material.IRON, true);
    setResistance(2000.0F); //TNT Proof
    setHardness(5.0F);
    setSoundType(SoundType.METAL);
    setUnlocalizedName(ModObject.blockDarkIronBars.getUnlocalisedName());
    setRegistryName(ModObject.blockDarkIronBars.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);    
  }
  
  protected void init() {
    GameRegistry.register(this);
    GameRegistry.register(new BlockItemDarkIronBars(this, ModObject.blockDarkIronBars.getUnlocalisedName()));
    OreDictionary.registerOre("barsIron", this);
  }

}

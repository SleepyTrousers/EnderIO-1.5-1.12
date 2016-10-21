package crazypants.enderio.material;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkIronBars extends BlockPane implements IHaveRenderers {
  
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
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(ModObject.blockDarkIronBars);
  }

}

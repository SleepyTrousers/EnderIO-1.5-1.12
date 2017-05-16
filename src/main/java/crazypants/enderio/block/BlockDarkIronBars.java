package crazypants.enderio.block;

import javax.annotation.Nonnull;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockPane;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkIronBars extends BlockPane implements IHaveRenderers {

  public static BlockDarkIronBars create(@Nonnull IModObject modObject) {
    BlockDarkIronBars res = new BlockDarkIronBars(modObject);
    res.init();
    return res;
  }

  private final @Nonnull IModObject modObject;

  public BlockDarkIronBars(@Nonnull IModObject modObject) {
    super(Material.IRON, true);
    setResistance(2000.0F); // TNT Proof
    setHardness(5.0F);
    setSoundType(SoundType.METAL);
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
    this.modObject = modObject;
  }

  protected void init() {
    GameRegistry.register(this);
    GameRegistry.register(new BlastResistantItemBlock(this, modObject.getUnlocalisedName()));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void registerRenderers() {
    ClientUtil.registerDefaultItemRenderer(modObject);
  }

}

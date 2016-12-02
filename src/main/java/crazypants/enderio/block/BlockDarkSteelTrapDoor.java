package crazypants.enderio.block;

import crazypants.enderio.EnderIOTab;
import crazypants.enderio.IModObject;
import crazypants.enderio.ModObject;
import crazypants.enderio.render.IHaveRenderers;
import crazypants.util.ClientUtil;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockDarkSteelTrapDoor extends BlockTrapDoor implements IHaveRenderers {

  public static BlockDarkSteelTrapDoor create() {
    BlockDarkSteelTrapDoor res = new BlockDarkSteelTrapDoor(ModObject.blockDarkSteelTrapdoor, Material.IRON, true);
    res.init();
    return res;
  }

  protected final IModObject modObject;

  public BlockDarkSteelTrapDoor(IModObject modObject, Material materialIn, boolean isBlastResistant) {
    super(materialIn);
    this.modObject = modObject;
    if (isBlastResistant) {
      setResistance(2000.0F); // TNT Proof
    }
    if (materialIn == Material.IRON) {
      setHardness(5.0F);
      setSoundType(SoundType.METAL);
    } else {
      setHardness(3.0F);
      setSoundType(SoundType.WOOD);
    }
    disableStats();
    setUnlocalizedName(modObject.getUnlocalisedName());
    setRegistryName(modObject.getUnlocalisedName());
    setCreativeTab(EnderIOTab.tabEnderIO);
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

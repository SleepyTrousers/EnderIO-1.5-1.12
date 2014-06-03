package crazypants.enderio.machine.generator.stirling;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.network.PacketHandler;

public class BlockStirlingGenerator extends AbstractMachineBlock<TileEntityStirlingGenerator> {

  public static BlockStirlingGenerator create() {
    
    PacketHandler.INSTANCE.registerMessage(PacketBurnTime.class, PacketBurnTime.class, PacketHandler.nextID(), Side.CLIENT);
    
    BlockStirlingGenerator gen = new BlockStirlingGenerator();
    gen.init();
    return gen;
  }

  protected BlockStirlingGenerator() {
    super(ModObject.blockStirlingGenerator, TileEntityStirlingGenerator.class);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new StirlingGeneratorContainer(player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(x, y, z));
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    return new GuiStirlingGenerator(player.inventory, (TileEntityStirlingGenerator) world.getTileEntity(x, y, z));
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_STIRLING_GEN;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if(active) {
      return "enderio:stirlingGenFrontOn";
    }
    return "enderio:stirlingGenFrontOff";
  }


}

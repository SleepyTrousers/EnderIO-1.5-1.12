package crazypants.enderio.machine.alloy;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.IMachineRecipe;
import crazypants.enderio.machine.MachineRecipeRegistry;

public class BlockAlloySmelter extends AbstractMachineBlock<TileAlloySmelter> {

  public static BlockAlloySmelter create() {
    PacketHandler.instance.addPacketProcessor(new AlloySmelterPacketProcessor());
    BlockAlloySmelter ppainter = new BlockAlloySmelter();
    ppainter.init();
    return ppainter;
  }
  
  Icon vanillaSmeltingOn;
  Icon vanillaSmeltingOff;

  private BlockAlloySmelter() {
    super(ModObject.blockAlloySmelter, TileAlloySmelter.class);
  }

  @Override
  protected void init() {
    super.init();

    // GameRegistry.addRecipe(new ItemStack(this), "bbb", "iri", "bbb", 'i', new
    // ItemStack(Item.ingotIron), 'r', new ItemStack(Item.redstone), 'b', new
    // ItemStack(
    // ModObject.itemIndustrialBinder.id, 1, 0));
    IMachineRecipe recipe;
    recipe = new BasicAlloyRecipe(new ItemStack(Item.axeDiamond), "Diamond Axe", new ItemStack(Item.diamond, 3, 0), new ItemStack(Item.stick, 2, 0));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, recipe);

    recipe = new BasicAlloyRecipe(new ItemStack(Item.blazeRod), "Blazedy Blaze", new ItemStack(Item.ingotIron, 1, 0), new ItemStack(Item.ingotGold, 1, 0),
        new ItemStack(Item.diamond, 1, 0));
    MachineRecipeRegistry.instance.registerRecipe(ModObject.blockAlloySmelter.unlocalisedName, recipe);
  }
  
  

  @Override
  public void registerIcons(IconRegister iconRegister) {
    super.registerIcons(iconRegister);
    vanillaSmeltingOn = iconRegister.registerIcon("enderio:furnaceSmeltingOn");
    vanillaSmeltingOff = iconRegister.registerIcon("enderio:furnaceSmeltingOff");
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    // The server needs the container as it manages the adding and removing of
    // items, which are then sent to the client for display
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if (te instanceof TileAlloySmelter) {
      return new ContainerAlloySmelter(player.inventory, (TileAlloySmelter) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getBlockTileEntity(x, y, z);
    return new GuiAlloySmelter(player.inventory, (TileAlloySmelter) te);
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_ALLOY_SMELTER;
  }

  @Override
  protected String getMachineFrontIconKey(boolean active) {
    if (active) {
      return "enderio:alloySmelterFrontOn";
    }
    return "enderio:alloySmelterFront";
  }

}

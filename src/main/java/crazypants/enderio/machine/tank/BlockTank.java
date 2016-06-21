package crazypants.enderio.machine.tank;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.client.handlers.SpecialTooltipHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;

public class BlockTank extends AbstractMachineBlock<TileTank> implements IAdvancedTooltipProvider {

  public static BlockTank create() {
    PacketHandler.INSTANCE.registerMessage(PacketTankFluid.class, PacketTankFluid.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketTankVoidMode.class, PacketTankVoidMode.class, PacketHandler.nextID(), Side.SERVER);
    BlockTank res = new BlockTank();
    res.init();
    return res;
  }

  protected BlockTank() {
    super(ModObject.blockTank, TileTank.class);
    setStepSound(Block.soundTypeGlass);
    setLightOpacity(0);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemTank.class, modObject.unlocalisedName);
    GameRegistry.registerTileEntity(teClass, modObject.unlocalisedName + "TileEntity");
    EnderIO.guiHandler.registerGuiHandler(getGuiId(), this);
  }

  @Override
  public int damageDropped(int par1) {
    return par1;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs p_149666_2_, List list) {
    list.add(new ItemStack(this, 1, 0));
    list.add(new ItemStack(this, 1, 1));
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileTank(metadata);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      return null;
    }
    return new ContainerTank(player.inventory, (TileTank) te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileTank)) {
      return null;
    }
    return new GuiTank(player.inventory, (TileTank) te);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_TANK;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess world, int x, int y, int z, int blockSide) {

    // used to render the block in the world
    TileEntity te = world.getTileEntity(x, y, z);
    int facing = 0;
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity) te;
      facing = me.facing;
    }
    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, 1);
    if(meta == 1) {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing] + 6];
    } else {
      return iconBuffer[0][ClientProxy.sideAndFacingToSpriteOffset[blockSide][facing]];
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int blockSide, int blockMeta) {
    int offset = MathHelper.clamp_int(blockMeta, 0, 1) == 0 ? 0 : 6;
    return iconBuffer[0][blockSide + offset];
  }

  @Override
  public int getLightValue(IBlockAccess world, int x, int y, int z) {
    TileEntity tank = world.getTileEntity(x, y, z);
    if(tank instanceof TileTank) {
      FluidStack stack = ((TileTank) tank).tank.getFluid();
      return stack == null || stack.amount <= 0 ? 0 : stack.getFluid().getLuminosity(stack);
    }
    return super.getLightValue(world, x, y, z);
  }

  @Override
  protected String getMachineFrontIconKey(boolean pressurized) {
    if(pressurized) {
      return "enderio:blockTankAdvanced";
    }
    return "enderio:blockTank";
  }

  @Override
  protected String getSideIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  protected String getBackIconKey(boolean active) {
    return getMachineFrontIconKey(active);
  }

  @Override
  protected String getTopIconKey(boolean pressurized) {
    if(pressurized) {
      return "enderio:blockTankTopAdvanced";
    }
    return "enderio:machineTop";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
    int meta = world.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, 1);
    if(meta == 1) {
      return 2000;
    } else {
      return super.getExplosionResistance(par1Entity);
    }
  }

  @Override
  public boolean hasComparatorInputOverride() {
    return true;
  }

  @Override
  public int getComparatorInputOverride(World w, int x, int y, int z, int side) {
    TileEntity te = w.getTileEntity(x, y, z);
    if (te instanceof TileTank) {
      return ((TileTank) te).getComparatorOutput();
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if(itemstack.stackTagCompound != null && itemstack.stackTagCompound.hasKey("tankContents")) {
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) itemstack.stackTagCompound.getTag("tankContents"));
      if(fl != null && fl.getFluid() != null) {
        String str = fl.amount + " " + EnderIO.lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " " + fl.getFluid().getLocalizedName();
        list.add(str);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    SpecialTooltipHandler.addDetailedTooltipFromResources(list, itemstack);
    if(itemstack.getItemDamage() == 1) {
      list.add(EnumChatFormatting.ITALIC + EnderIO.lang.localize("blastResistant"));
    }
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    System.out.println("BlockTank.getUnlocalizedNameForTooltip: ");
    return stack.getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileTank) {
      TileTank tank = (TileTank) te;
      FluidStack stored = tank.tank.getFluid();
      String fluid = stored == null ? EnderIO.lang.localize("tooltip.none") : stored.getFluid().getLocalizedName(stored);
      int amount = stored == null ? 0 : stored.amount;

      tooltip.add(String.format("%s%s : %s (%d %s)", EnumChatFormatting.WHITE, EnderIO.lang.localize("tooltip.fluidStored"), fluid, amount, EnderIO.lang.localize("fluid.millibucket.abr")));
    }
  }
}

package crazypants.enderio.machine.drain;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidContainerRegistry.FluidContainerData;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.ClientProxy;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.AbstractMachineBlock;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.FluidUtil;
import crazypants.util.Lang;
import crazypants.util.Util;

public class BlockDrain extends AbstractMachineBlock<TileDrain> implements IAdvancedTooltipProvider {

  public static BlockDrain create() {
    PacketHandler.INSTANCE.registerMessage(PacketDrain.class, PacketDrain.class, PacketHandler.nextID(), Side.CLIENT);
    BlockDrain res = new BlockDrain();
    res.init();
    return res;
  }

  protected BlockDrain() {
    super(ModObject.blockDrain, TileDrain.class);
    setStepSound(Block.soundTypeGlass);
    setLightOpacity(0);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemDrain.class, modObject.unlocalisedName);
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
  }

  @Override
  public TileEntity createTileEntity(World world, int metadata) {
    return new TileDrain(metadata);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {

    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileDrain)) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    TileDrain drain = (TileDrain) te;
    ItemStack item = entityPlayer.inventory.getCurrentItem();
    if(item == null) {
      return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
    }

    //now check for empty fluid containers to fill
    if (FluidUtil.fillPlayerHandItemFromInternalTank(world, x, y, z, entityPlayer, drain)) {
      return true;
    }

    return super.onBlockActivated(world, x, y, z, entityPlayer, par6, par7, par8, par9);
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileDrain)) {
      return null;
    }
    return new ContainerDrain(player.inventory, (TileDrain) te);
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileDrain)) {
      return null;
    }
    return new GuiDrain(player.inventory, (TileDrain) te);
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  protected int getGuiId() {
    return GuiHandler.GUI_ID_DRAIN;
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
  protected String getMachineFrontIconKey(boolean active) {
    return "enderio:machineSide";
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
  protected String getTopIconKey(boolean active) {
    return "enderio:blockDrain";
  }

  @Override
  protected String getBottomIconKey(boolean active) {
    return "enderio:machineTemplate";
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if (!Config.drainAllowOnDedicatedServer && !Minecraft.getMinecraft().isSingleplayer()) {
      list.add(Lang.localize("blockDrain.tooltip.disabledMessage"));
    }
  }

  @Override
  public boolean hasComparatorInputOverride() {
    return true;
  }

  @Override
  public int getComparatorInputOverride(World w, int x, int y, int z, int side) {
    TileEntity te = w.getTileEntity(x, y, z);
    if (te instanceof TileDrain) {
      return ((TileDrain) te).getComparatorOutput();
    }
    return 0;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    if(itemstack.stackTagCompound != null && itemstack.stackTagCompound.hasKey("tankContents")) {
      FluidStack fl = FluidStack.loadFluidStackFromNBT((NBTTagCompound) itemstack.stackTagCompound.getTag("tankContents"));
      if(fl != null && fl.getFluid() != null) {
        String str = fl.amount + " " + Lang.localize("fluid.millibucket.abr") + " " + PowerDisplayUtil.ofStr() + " " + fl.getFluid().getLocalizedName();
        list.add(str);
      }
    }
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    TooltipAddera.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public String getUnlocalizedNameForTooltip(ItemStack stack) {
    return stack.getUnlocalizedName();
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if (te instanceof TileDrain) {
      TileDrain tank = (TileDrain) te;
      FluidStack stored = tank.tank.getFluid();
      String fluid = stored == null ? Lang.localize("tooltip.none") : stored.getFluid().getLocalizedName(stored);
      int amount = stored == null ? 0 : stored.amount;

      tooltip.add(String.format("%s%s : %s (%d %s)", EnumChatFormatting.WHITE, Lang.localize("tooltip.fluidStored"), fluid, amount, Lang.localize("fluid.millibucket.abr")));
    }
  }

  public static int renderId;

  @Override
  public int getRenderType() {
    return renderId;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
  }
}

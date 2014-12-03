package crazypants.enderio.machine.capbank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.BlockEio;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.gui.IAdvancedTooltipProvider;
import crazypants.enderio.gui.TooltipAddera;
import crazypants.enderio.machine.IoMode;
import crazypants.enderio.machine.capbank.network.CapBankClientNetwork;
import crazypants.enderio.machine.capbank.network.ClientNetworkManager;
import crazypants.enderio.machine.capbank.packet.PacketGuiChange;
import crazypants.enderio.machine.capbank.packet.PacketNetworkEnergyRequest;
import crazypants.enderio.machine.capbank.packet.PacketNetworkEnergyResponse;
import crazypants.enderio.machine.capbank.packet.PacketNetworkIdRequest;
import crazypants.enderio.machine.capbank.packet.PacketNetworkIdResponse;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateRequest;
import crazypants.enderio.machine.capbank.packet.PacketNetworkStateResponse;
import crazypants.enderio.machine.power.PowerDisplayUtil;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.tool.ToolUtil;
import crazypants.enderio.waila.IWailaInfoProvider;
import crazypants.vecmath.Vector3d;

public class BlockCapBank extends BlockEio implements IGuiHandler, IAdvancedTooltipProvider, IWailaInfoProvider {

  public static int renderId = -1;

  public static BlockCapBank create() {

    PacketHandler.INSTANCE.registerMessage(PacketNetworkStateResponse.class, PacketNetworkStateResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkStateRequest.class, PacketNetworkStateRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkIdRequest.class, PacketNetworkIdRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkIdResponse.class, PacketNetworkIdResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkEnergyRequest.class, PacketNetworkEnergyRequest.class, PacketHandler.nextID(), Side.SERVER);
    PacketHandler.INSTANCE.registerMessage(PacketNetworkEnergyResponse.class, PacketNetworkEnergyResponse.class, PacketHandler.nextID(), Side.CLIENT);
    PacketHandler.INSTANCE.registerMessage(PacketGuiChange.class, PacketGuiChange.class, PacketHandler.nextID(), Side.SERVER);

    BlockCapBank res = new BlockCapBank();
    res.init();
    return res;
  }

  @SideOnly(Side.CLIENT)
  IIcon overlayIcon;
  @SideOnly(Side.CLIENT)
  IIcon fillBarIcon;

  @SideOnly(Side.CLIENT)
  private IIcon[] blockIcons;
  @SideOnly(Side.CLIENT)
  private IIcon[] borderIcons;
  @SideOnly(Side.CLIENT)
  private IIcon[] inputIcons;
  @SideOnly(Side.CLIENT)
  private IIcon[] outputIcons;
  @SideOnly(Side.CLIENT)
  private IIcon[] lockedIcons;

  protected BlockCapBank() {
    super(ModObject.blockCapBank.unlocalisedName, TileCapBank.class);
    setHardness(2.0F);
  }

  @Override
  protected void init() {
    GameRegistry.registerBlock(this, BlockItemCapBank.class, name);
    if(teClass != null) {
      GameRegistry.registerTileEntity(teClass, name + "TileEntity");
    }

    EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_CAP_BANK, this);
    setLightOpacity(255);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {
    int meta = 0;
    for (CapBankType type : CapBankType.types()) {
      if(type.isCreative()) {
        list.add(BlockItemCapBank.createItemStackWithPower(meta, type.getMaxEnergyStored() / 2));
      } else {
        list.add(BlockItemCapBank.createItemStackWithPower(meta, 0));
        list.add(BlockItemCapBank.createItemStackWithPower(meta, type.getMaxEnergyStored()));
      }
      meta++;
    }
  }

  @Override
  public int damageDropped(int par1) {
    return par1;
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {    
    list.add(PowerDisplayUtil.formatStoredPower(PowerHandlerUtil.getStoredEnergyForItem(itemstack), CapBankType.getTypeFromMeta(itemstack.getItemDamage())
        .getMaxEnergyStored()));
  }

  @Override
  @SideOnly(Side.CLIENT)
  public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
    TooltipAddera.addDetailedTooltipFromResources(list, itemstack);
  }

  @Override
  public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float par7, float par8, float par9) {

    if(ToolUtil.breakBlockWithTool(this, world, x, y, z, entityPlayer)) {
      return true;
    }

    if(entityPlayer.isSneaking()) {
      return false;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCapBank)) {
      return false;
    }

    if(ToolUtil.isToolEquipped(entityPlayer)) {

      ForgeDirection faceHit = ForgeDirection.getOrientation(side);
      TileCapBank tcb = (TileCapBank) te;
      tcb.toggleIoModeForFace(faceHit);
      if(world.isRemote) {
        world.markBlockForUpdate(x, y, z);
      } else {
        world.notifyBlocksOfNeighborChange(x, y, z, EnderIO.blockCapBank);
        world.markBlockForUpdate(x, y, z);
      }

      return true;
    }

    if(!world.isRemote) {
      entityPlayer.openGui(EnderIO.instance, GuiHandler.GUI_ID_CAP_BANK, world, x, y, z);
    }
    return true;
  }

  @Override
  public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCapBank) {
      return new ContainerCapBank(player, player.inventory, (TileCapBank) te);
    }
    return null;
  }

  @Override
  public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(te instanceof TileCapBank) {
      return new GuiCapBank(player, player.inventory, (TileCapBank) te);
    }
    return null;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void registerBlockIcons(IIconRegister IIconRegister) {
    blockIcon = IIconRegister.registerIcon("enderio:capacitorBank");
    overlayIcon = IIconRegister.registerIcon("enderio:capacitorBankOverlays");
    fillBarIcon = IIconRegister.registerIcon("enderio:capacitorBankFillBar");

    blockIcons = new IIcon[CapBankType.types().size()];
    borderIcons = new IIcon[CapBankType.types().size()];
    inputIcons = new IIcon[CapBankType.types().size()];
    outputIcons = new IIcon[CapBankType.types().size()];
    lockedIcons = new IIcon[CapBankType.types().size()];
    int index = 0;
    for (CapBankType type : CapBankType.types()) {
      blockIcons[index] = IIconRegister.registerIcon(type.getIcon());
      borderIcons[index] = IIconRegister.registerIcon(type.getBorderIcon());

      inputIcons[index] = IIconRegister.registerIcon(type.getInputIcon());
      outputIcons[index] = IIconRegister.registerIcon(type.getOutputIcon());
      lockedIcons[index] = IIconRegister.registerIcon(type.getLockedIcon());

      ++index;
    }

  }

  @Override
  public int getRenderType() {
    return renderId;
  }

  @Override
  public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
    return true;
  }

  @Override
  public boolean isOpaqueCube() {
    return false;
  }

  @Override
  public boolean renderAsNormalBlock() {
    return false;
  }

  @Override
  public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
    Block i1 = par1IBlockAccess.getBlock(par2, par3, par4);
    return i1 == this ? false : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(int side, int meta) {
    meta = MathHelper.clamp_int(meta, 0, blockIcons.length - 1);
    return blockIcons[meta];
  }

  @SideOnly(Side.CLIENT)
  public IIcon getBorderIcon(int side, int meta) {
    meta = MathHelper.clamp_int(meta, 0, blockIcons.length - 1);
    return borderIcons[meta];
  }

  @Override
  @SideOnly(Side.CLIENT)
  public IIcon getIcon(IBlockAccess ba, int x, int y, int z, int side) {
    TileEntity te = ba.getTileEntity(x, y, z);
    if(!(te instanceof TileCapBank)) {
      return blockIcons[0];
    }
    int meta = ba.getBlockMetadata(x, y, z);
    meta = MathHelper.clamp_int(meta, 0, CapBankType.types().size() - 1);

    TileCapBank cb = (TileCapBank) te;
    IoMode mode = cb.getIoMode(ForgeDirection.values()[side]);
    if(mode == null || mode == IoMode.NONE) {
      return blockIcons[meta];
    }
    if(mode == IoMode.PULL) {
      return inputIcons[meta];
    }
    if(mode == IoMode.PUSH) {
      return outputIcons[meta];
    }
    return lockedIcons[meta];
  }

  //  @Override
  //  public void onBlockAdded(World world, int x, int y, int z) {
  //    if(world.isRemote) {
  //      return;
  //    }
  //    TileEntity te = world.getTileEntity(x, y, z);
  //    if(te instanceof TileCapacitorBank) {
  //      TileCapacitorBank tr = (TileCapacitorBank) te;
  //      int meta = world.getBlockMetadata(x, y, z);
  //      if(meta == 1) {
  //        tr.setCreativeMode();
  //      }
  //      tr.onBlockAdded();
  //    }
  //  }

  @Override
  public void onNeighborBlockChange(World world, int x, int y, int z, Block blockId) {
    if(world.isRemote) {
      return;
    }
    TileEntity tile = world.getTileEntity(x, y, z);
    if(tile instanceof TileCapBank) {
      TileCapBank te = (TileCapBank) tile;
      te.onNeighborBlockChange(blockId);
    }
  }

  @Override
  public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
    //    if(!world.isRemote) {
    //      TileEntity te = world.getTileEntity(x, y, z);
    //      if(te instanceof TileCapBank) {
    //        TileCapBank cb = (TileCapBank) te;
    //        cb.onBreakBlock();
    //
    //        ItemStack itemStack =
    //            BlockItemCapacitorBank.createItemStackWithPower(cb.doGetEnergyStored());
    //        ret.add(itemStack);
    //      }
    //    }
    return ret;
  }

  @Override
  public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    if(!world.isRemote) {
      //      TileEntity te = world.getTileEntity(x, y, z);
      //      if(te instanceof TileCapBank) {
      //        TileCapBank cb = (TileCapBank) te;
      //        cb.onBreakBlock();
      //
      //        // If we are not in Creative or blockCapBankAllwaysDrop is set to true, allow the item drop.
      //        // This option allows creative players to pick up broken capacitor banks
      //
      //        if(!player.capabilities.isCreativeMode || "true".equalsIgnoreCase(System.getProperty("blockCapBankAllwaysDrop"))) {
      //          ItemStack itemStack =
      //              BlockItemCapacitorBank.createItemStackWithPower(cb.doGetEnergyStored());
      //          if(cb.isCreative()) {
      //            itemStack.setItemDamage(1);
      //          }
      //          float f = 0.7F;
      //          double d0 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      //          double d1 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      //          double d2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
      //          EntityItem entityitem = new EntityItem(world, x + d0, y + d1, z + d2, itemStack);
      //          entityitem.delayBeforeCanPickup = 10;
      //          world.spawnEntityInWorld(entityitem);
      //        }
      //      }
    }
    return super.removedByPlayer(world, player, x, y, z);
  }

  @Override
  public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
    if(world.isRemote) {
      return;
    }
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCapBank)) {
      return;
    }

    TileCapBank cb = (TileCapBank) te;
    cb.addEnergy(PowerHandlerUtil.getStoredEnergyForItem(stack));

    world.markBlockForUpdate(x, y, z);
  }

  @Override
  public int quantityDropped(Random r) {
    return 0;
  }

  @Override
  public void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
    if(!world.isRemote && world.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
      //      TileEntity te = world.getTileEntity(x, y, z);
      //      if(!(te instanceof TileCapacitorBank)) {
      //        super.breakBlock(world, x, y, z, par5, par6);
      //        return;
      //      }
      //      TileCapacitorBank cb = (TileCapacitorBank) te;
      //      Util.dropItems(world, cb, x, y, z, true);
    }
    world.removeTileEntity(x, y, z);
  }

  @Override
  @SideOnly(Side.CLIENT)
  public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    TileEntity te = world.getTileEntity(x, y, z);
    if(!(te instanceof TileCapBank)) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }
    TileCapBank tr = (TileCapBank) te;
    CapBankClientNetwork network = ClientNetworkManager.getInstance().getNetwork(tr);

    if(!tr.getType().isMultiblock() || network == null) {
      return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }

    Vector3d min = new Vector3d(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
    Vector3d max = new Vector3d(-Double.MAX_VALUE, -Double.MAX_VALUE, -Double.MAX_VALUE);
    for (TileCapBank bc : network.getMembers()) {
      min.x = Math.min(min.x, bc.xCoord);
      max.x = Math.max(max.x, bc.xCoord + 1);
      min.y = Math.min(min.y, bc.yCoord);
      max.y = Math.max(max.y, bc.yCoord + 1);
      min.z = Math.min(min.z, bc.zCoord);
      max.z = Math.max(max.z, bc.zCoord + 1);
    }
    return AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x, max.y, max.z);
  }

  @Override
  public boolean hasComparatorInputOverride() {
    return true;
  }

  @Override
  public int getComparatorInputOverride(World w, int x, int y, int z, int side) {
    TileEntity te = w.getTileEntity(x, y, z);
    if(te instanceof TileCapBank) {
      return ((TileCapBank) te).getComparatorOutput();
    }
    return 0;
  }

  @Override
  public void getWailaInfo(List<String> tooltip, EntityPlayer player, World world, int x, int y, int z) {
    //    TileEntity te = world.getTileEntity(x, y, z);
    //    if (te instanceof TileCapBank) {
    //      TileCapBank cap = (TileCapBank) te;
    //      String format = Util.TAB + Util.ALIGNRIGHT + EnumChatFormatting.WHITE;
    //            
    //      tooltip.add(String.format("%s : %s%s%sRF/t ", Lang.localize("capbank.maxIO"),  format, fmt.format(cap.getMaxIO()), Util.TAB + Util.ALIGNRIGHT));
    //      tooltip.add(String.format("%s : %s%s%sRF/t ", Lang.localize("capbank.maxIn"),  format, fmt.format(cap.getMaxInput()), Util.TAB + Util.ALIGNRIGHT));
    //      tooltip.add(String.format("%s : %s%s%sRF/t ", Lang.localize("capbank.maxOut"), format, fmt.format(cap.getMaxOutput()), Util.TAB + Util.ALIGNRIGHT));
    //    }
  }

  @Override
  public int getDefaultDisplayMask(World world, int x, int y, int z) {
    return IWailaInfoProvider.BIT_DETAILED;
  }
}

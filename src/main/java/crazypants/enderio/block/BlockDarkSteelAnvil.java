package crazypants.enderio.block;

import net.minecraft.block.BlockAnvil;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAnvilBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;

public class BlockDarkSteelAnvil extends BlockAnvil implements IResourceTooltipProvider {

    private static final String[] anvilIconNames = new String[] { "anvil_0", "anvil_1", "anvil_2" };

    @SideOnly(Side.CLIENT)
    private IIcon[] anvilIcons;

    public static BlockDarkSteelAnvil create() {
        BlockDarkSteelAnvil res = new BlockDarkSteelAnvil();
        res.init();
        return res;
    }

    private BlockDarkSteelAnvil() {
        super();

        setHardness(5.0F);
        setStepSound(soundTypeAnvil);
        setResistance(2000.0F);

        setBlockName(ModObject.blockDarkSteelAnvil.unlocalisedName);
        setCreativeTab(EnderIOTab.tabEnderIO);
    }

    protected void init() {
        GameRegistry.registerBlock(this, ItemAnvilBlock.class, ModObject.blockDarkSteelAnvil.unlocalisedName);
        EnderIO.guiHandler.registerGuiHandler(GuiHandler.GUI_ID_ANVIL, new IGuiHandler() {

            @Override
            public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                return new ContainerDarkSteelAnvil(player.inventory, world, x, y, z, player);
            }

            @Override
            public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
                return new GuiRepair(player.inventory, world, x, y, z);
            }
        });
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return this.getUnlocalizedName();
    }

    @Override
    public boolean onBlockActivated(World w, int x, int y, int z, EntityPlayer p, int side, float hitX, float hitY,
            float hitZ) {
        p.openGui(EnderIO.instance, GuiHandler.GUI_ID_ANVIL, w, x, y, z);
        return true;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        if (this.anvilRenderSide == 3 && p_149691_1_ == 1) {
            int k = (p_149691_2_ >> 2) % this.anvilIcons.length;
            return this.anvilIcons[k];
        } else {
            return this.blockIcon;
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        this.blockIcon = register.registerIcon(EnderIO.DOMAIN + ":anvil_base");
        this.anvilIcons = new IIcon[anvilIconNames.length];

        for (int i = 0; i < this.anvilIcons.length; ++i) {
            this.anvilIcons[i] = register.registerIcon(EnderIO.DOMAIN + ":" + anvilIconNames[i]);
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
        return new ItemStack(this, 1, world.getBlockMetadata(x, y, z) >> 2);
    }
}

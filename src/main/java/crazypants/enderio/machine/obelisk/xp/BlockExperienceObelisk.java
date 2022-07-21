package crazypants.enderio.machine.obelisk.xp;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.machine.ContainerNoInv;
import crazypants.enderio.machine.obelisk.BlockObeliskAbstract;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockExperienceObelisk extends BlockObeliskAbstract<TileExperienceObelisk> {

    public static BlockExperienceObelisk create() {
        BlockExperienceObelisk res = new BlockExperienceObelisk();
        res.init();
        return res;
    }

    private BlockExperienceObelisk() {
        super(ModObject.blockExperienceObelisk, TileExperienceObelisk.class);
    }

    @Override
    protected boolean isActive(IBlockAccess blockAccess, int x, int y, int z) {
        return true; // always render as active
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int blockSide, int blockMeta) {
        return super.getIcon(blockSide + 6, blockMeta); // always render as active
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileExperienceObelisk) {
            return new ContainerNoInv((IInventory) te);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileExperienceObelisk) {
            return new GuiExperienceObelisk(player.inventory, (TileExperienceObelisk) te);
        }
        return null;
    }

    @Override
    protected int getGuiId() {
        return GuiHandler.GUI_ID_XP_OBELISK;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        ; // Has no particles
    }
}

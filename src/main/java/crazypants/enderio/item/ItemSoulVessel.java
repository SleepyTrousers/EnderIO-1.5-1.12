package crazypants.enderio.item;

import com.enderio.core.api.client.gui.IResourceTooltipProvider;
import com.enderio.core.common.util.DyeColor;
import com.enderio.core.common.util.EntityUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.ModObject;
import crazypants.enderio.config.Config;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockWall;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemSoulVessel extends Item implements IResourceTooltipProvider {

    public static ItemSoulVessel create() {
        ItemSoulVessel result = new ItemSoulVessel();
        result.init();
        return result;
    }

    private IIcon filledIcon;

    private List<String> blackList;

    protected ItemSoulVessel() {
        setCreativeTab(EnderIOTab.tabEnderIO);
        setUnlocalizedName(ModObject.itemSoulVessel.unlocalisedName);
        setMaxStackSize(64);
        blackList = new ArrayList<String>();
        for (String ent : Config.soulVesselBlackList) {
            blackList.add(ent);
        }
    }

    protected void init() {
        GameRegistry.registerItem(this, ModObject.itemSoulVessel.unlocalisedName);
    }

    public void addEntityToBlackList(String entityName) {
        blackList.add(entityName);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister IIconRegister) {
        itemIcon = IIconRegister.registerIcon("enderio:soulVessel");
        filledIcon = IIconRegister.registerIcon("enderio:soulVesselFilled");
    }

    @Override
    public IIcon getIcon(ItemStack item, int arg1, EntityPlayer arg2, ItemStack arg3, int arg4) {
        if (containsSoul(item)) {
            return filledIcon;
        }
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack item) {
        if (containsSoul(item)) {
            return filledIcon;
        }
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack item, int pass) {
        return containsSoul(item);
    }

    @Override
    public boolean onItemUse(
            ItemStack itemstack,
            EntityPlayer player,
            World world,
            int x,
            int y,
            int z,
            int side,
            float xOffset,
            float yOffset,
            float zOffset) {

        if (world.isRemote) {
            return true;
        }
        if (!containsSoul(itemstack)) {
            return false;
        }
        if (player == null) {
            return false;
        }

        Entity mob;
        NBTTagCompound root = itemstack.stackTagCompound;
        if (root.hasKey("isStub")) {
            String entityId = root.getString("id");
            mob = EntityList.createEntityByName(entityId, world);
        } else {
            mob = EntityList.createEntityFromNBT(root, world);
        }
        if (mob == null) {
            return true;
        }
        mob.readFromNBT(root);

        Block blk = world.getBlock(x, y, z);
        double spawnX = x + Facing.offsetsXForSide[side] + 0.5;
        double spawnY = y + Facing.offsetsYForSide[side];
        double spawnZ = z + Facing.offsetsZForSide[side] + 0.5;
        if (side == ForgeDirection.UP.ordinal() && (blk instanceof BlockFence || blk instanceof BlockWall)) {
            spawnY += 0.5;
        }
        mob.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0);

        boolean spaceClear = world.checkNoEntityCollision(mob.boundingBox)
                && world.getCollidingBoundingBoxes(mob, mob.boundingBox).isEmpty();
        if (!spaceClear) {
            return false;
        }

        if (itemstack.hasDisplayName() && mob instanceof EntityLiving) {
            ((EntityLiving) mob).setCustomNameTag(itemstack.getDisplayName());
        }

        world.spawnEntityInWorld(mob);
        if (mob instanceof EntityLiving) {
            ((EntityLiving) mob).playLivingSound();
        }

        Entity riddenByEntity = mob.riddenByEntity;
        while (riddenByEntity != null) {
            riddenByEntity.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(riddenByEntity);
            if (riddenByEntity instanceof EntityLiving) {
                ((EntityLiving) riddenByEntity).playLivingSound();
            }
            riddenByEntity = riddenByEntity.riddenByEntity;
        }

        if (!player.capabilities.isCreativeMode) {
            if (itemstack.stackSize > 1) {
                itemstack.stackSize--;
                player.inventory.addItemStackToInventory(new ItemStack(this));
                player.inventoryContainer.detectAndSendChanges();
            } else {
                itemstack.setTagCompound(null);
            }
        }

        return true;
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack item, EntityPlayer player, EntityLivingBase entity) {

        if (entity.worldObj.isRemote) {
            return false;
        }
        boolean isCreative = player != null && player.capabilities.isCreativeMode;
        if (containsSoul(item) && !isCreative) {
            return false;
        }
        if (entity instanceof EntityPlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }

        String entityId = EntityList.getEntityString(entity);
        if (isBlackListed(entityId)) {
            return false;
        }

        if (!Config.soulVesselCapturesBosses && entity instanceof IBossDisplayData) {
            return false;
        }

        NBTTagCompound root = new NBTTagCompound();
        root.setString("id", entityId);
        entity.writeToNBT(root);

        ItemStack capturedMobVessel = new ItemStack(EnderIO.itemSoulVessel);
        capturedMobVessel.setTagCompound(root);
        setDisplayNameFromEntityNameTag(capturedMobVessel, entity);

        player.swingItem();
        if (!isCreative) {
            entity.setDead();
            if (entity.isDead) {
                item.stackSize--;
                if (!player.inventory.addItemStackToInventory(capturedMobVessel)) {
                    entity.worldObj.spawnEntityInWorld(
                            new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, capturedMobVessel));
                }
                player.setCurrentItemOrArmor(0, item);
                ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
                return true;
            }
        } else {
            if (!player.inventory.addItemStackToInventory(capturedMobVessel)) // Inventory full, drop it in the world!
            {
                entity.worldObj.spawnEntityInWorld(
                        new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, capturedMobVessel));
            }
            ((EntityPlayerMP) player).sendContainerToPlayer(player.inventoryContainer);
            return true;
        }
        return false;
    }

    public ItemStack createVesselWithEntityStub(String entityId) {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("id", entityId);
        root.setBoolean("isStub", true);

        ItemStack res = new ItemStack(this);
        res.stackTagCompound = root;
        return res;
    }

    public ItemStack createVesselWithEntity(Entity ent) {

        String entityId = EntityList.getEntityString(ent);
        NBTTagCompound root = new NBTTagCompound();
        root.setString("id", entityId);
        ent.writeToNBT(root);

        ItemStack res = new ItemStack(this);
        res.stackTagCompound = root;

        setDisplayNameFromEntityNameTag(res, ent);
        return res;
    }

    private void setDisplayNameFromEntityNameTag(ItemStack item, Entity ent) {
        if (ent instanceof EntityLiving) {
            EntityLiving entLiv = (EntityLiving) ent;
            if (entLiv.hasCustomNameTag()) {
                String name = entLiv.getCustomNameTag();
                if (name.length() > 0) {
                    item.setStackDisplayName(name);
                }
            }
        }
    }

    public boolean containsSoul(ItemStack item) {
        if (item == null) {
            return false;
        }
        if (item.getItem() != this) {
            return false;
        }
        return item.stackTagCompound != null && item.stackTagCompound.hasKey("id");
    }

    public String getMobTypeFromStack(ItemStack item) {
        if (!containsSoul(item)) {
            return null;
        }
        if (item.stackTagCompound == null || !item.stackTagCompound.hasKey("id")) {
            return null;
        }
        return item.stackTagCompound.getString("id");
    }

    /** Support for displaying fluid name of captured Moo Fluids cow */
    private String getFluidNameFromStack(ItemStack item) {
        if (!containsSoul(item)) {
            return null;
        }
        if (!item.stackTagCompound.hasKey("FluidName")) {
            return null;
        }
        return item.stackTagCompound.getString("FluidName");
    }

    private DyeColor getColorFromStack(ItemStack item) {
        if (!containsSoul(item)) {
            return null;
        }
        if (!item.stackTagCompound.hasKey("Color")) {
            return null;
        }
        int colorIdx = item.stackTagCompound.getInteger("Color");
        if (colorIdx < 0 || colorIdx > 15) {
            return null;
        }
        return DyeColor.values()[15 - colorIdx];
    }

    private float getHealthFromStack(ItemStack item) {
        if (!containsSoul(item)) {
            return Float.NaN;
        }
        if (!item.stackTagCompound.hasKey("HealF")) {
            return Float.NaN;
        }
        return item.stackTagCompound.getFloat("HealF");
    }

    private NBTTagCompound getAttributeFromStack(ItemStack item, String name) {
        if (!containsSoul(item)) {
            return null;
        }
        NBTBase tag = item.stackTagCompound.getTag("Attributes");
        if (tag instanceof NBTTagList) {
            NBTTagList attributes = (NBTTagList) tag;
            for (int i = 0; i < attributes.tagCount(); i++) {
                NBTTagCompound attrib = attributes.getCompoundTagAt(i);
                if (attrib.hasKey("Name") && name.equals(attrib.getString("Name"))) {
                    return attrib;
                }
            }
        }
        return null;
    }

    private float getMaxHealthFromStack(ItemStack item) {
        NBTTagCompound maxHealthAttrib = getAttributeFromStack(item, "generic.maxHealth");
        if (maxHealthAttrib == null) {
            return Float.NaN;
        }
        if (!maxHealthAttrib.hasKey("Base")) {
            return Float.NaN;
        }
        return maxHealthAttrib.getFloat("Base");
    }

    private boolean isBlackListed(String entityId) {
        for (String str : blackList) {
            if (str != null && str.equals(entityId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getUnlocalizedNameForTooltip(ItemStack itemStack) {
        return getUnlocalizedName(itemStack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        if (par1ItemStack != null) {
            String mobName = getMobTypeFromStack(par1ItemStack);
            if (mobName != null) {
                par3List.add(EntityUtil.getDisplayNameForEntity(mobName));
            } else {
                par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.empty"));
            }

            float health = getHealthFromStack(par1ItemStack);
            if (health >= 0) {
                float maxHealth = getMaxHealthFromStack(par1ItemStack);
                String msg = EnderIO.lang.localize("item.itemSoulVessel.tooltip.health");
                if (maxHealth >= 0) {
                    par3List.add(String.format("%s %3.1f/%3.1f", msg, health, maxHealth));
                } else {
                    par3List.add(String.format("%s %3.1f", msg, health));
                }
            }

            String fluidName = getFluidNameFromStack(par1ItemStack);
            if (fluidName != null) {
                Fluid fluid = FluidRegistry.getFluid(fluidName);
                if (fluid != null) {
                    par3List.add(EnderIO.lang.localize("item.itemSoulVessel.tooltip.fluidname") + " "
                            + fluid.getLocalizedName());
                }
            }

            DyeColor color = getColorFromStack(par1ItemStack);
            if (color != null) {
                par3List.add(
                        EnderIO.lang.localize("item.itemSoulVessel.tooltip.color") + " " + color.getLocalisedName());
            }
        }
    }
}

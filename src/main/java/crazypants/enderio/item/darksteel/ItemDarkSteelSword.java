package crazypants.enderio.item.darksteel;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

import com.enderio.core.api.client.gui.IAdvancedTooltipProvider;
import com.enderio.core.common.util.ItemUtil;
import com.enderio.core.common.util.Util;

import cofh.api.energy.IEnergyContainerItem;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.EnderIO;
import crazypants.enderio.EnderIOTab;
import crazypants.enderio.api.teleport.IItemOfTravel;
import crazypants.enderio.api.teleport.TravelSource;
import crazypants.enderio.config.Config;
import crazypants.enderio.item.darksteel.upgrade.EnergyUpgrade;
import crazypants.enderio.item.darksteel.upgrade.TravelUpgrade;
import crazypants.enderio.teleport.TravelController;

public class ItemDarkSteelSword extends ItemSword
        implements IEnergyContainerItem, IAdvancedTooltipProvider, IDarkSteelItem, IItemOfTravel {

    private static final String ENDERZOO_ENDERMINY = "enderzoo.Enderminy";

    static final ToolMaterial MATERIAL = EnumHelper
            .addToolMaterial("darkSteel", Config.darkSteelPickMinesTiCArdite ? 5 : 3, 1561, 7, 2, 25);

    public static boolean isEquipped(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null) {
            return false;
        }
        return equipped.getItem() instanceof ItemDarkSteelSword;
    }

    public static boolean isEquippedAndPowered(EntityPlayer player, int requiredPower) {
        if (!isEquipped(player)) {
            return false;
        }
        return EnergyUpgrade.getEnergyStored(player.getCurrentEquippedItem()) >= requiredPower;
    }

    public static ItemDarkSteelSword create() {
        ItemDarkSteelSword res = new ItemDarkSteelSword();
        res.init();
        MinecraftForge.EVENT_BUS.register(res);
        return res;
    }

    protected final int powerPerDamagePoint = Config.darkSteelPowerStorageBase / MATERIAL.getMaxUses();
    protected long lastBlickTick = -1;
    protected String name;

    public ItemDarkSteelSword(String name, ToolMaterial mat) {
        super(mat);
        this.name = name;
        setCreativeTab(EnderIOTab.tabEnderIO);

        String str = name + "_sword";
        setUnlocalizedName(str);
        setTextureName(EnderIO.DOMAIN + ":" + str);
    }

    public ItemDarkSteelSword() {
        this("darkSteel", MATERIAL);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List par3List) {
        ItemStack is = new ItemStack(this);
        par3List.add(is);

        is = new ItemStack(this);
        EnergyUpgrade.EMPOWERED_FOUR.writeToItem(is);
        EnergyUpgrade.setPowerFull(is);
        TravelUpgrade.INSTANCE.writeToItem(is);
        par3List.add(is);
    }

    @Override
    public int getIngotsRequiredForFullRepair() {
        return 3;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @SubscribeEvent
    public void onEnderTeleport(EnderTeleportEvent evt) {
        if (evt.entityLiving.getEntityData().getBoolean("hitByDarkSteelSword")) {
            evt.setCanceled(true);
        }
    }

    // Set priorty to lowest in the hope any other mod adding head drops will have already added them
    // by the time this is called to prevent multiple head drops
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onEntityDrop(LivingDropsEvent evt) {

        if (!(evt.source.getEntity() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) evt.source.getEntity();
        // Handle TiC weapons with beheading differently
        if (handleBeheadingWeapons(player, evt)) {
            return;
        }

        double skullDropChance = getSkullDropChance(player, evt);
        if (player instanceof FakePlayer) {
            skullDropChance *= Config.fakePlayerSkullChance;
        }
        if (Math.random() <= skullDropChance) {
            dropSkull(evt, player);
        }

        // Special handling for ender pear drops
        if (isEquipped(player)) {
            String name = EntityList.getEntityString(evt.entityLiving);
            if (evt.entityLiving instanceof EntityEnderman || ENDERZOO_ENDERMINY.equals(name)) {
                int numPearls = 0;
                if (Math.random() <= Config.darkSteelSwordEnderPearlDropChance) {
                    numPearls++;
                }
                for (int i = 0; i < evt.lootingLevel; i++) {
                    if (Math.random() <= Config.darkSteelSwordEnderPearlDropChancePerLooting) {
                        numPearls++;
                    }
                }

                int existing = 0;
                for (EntityItem stack : evt.drops) {
                    if (stack.getEntityItem() != null && stack.getEntityItem().getItem() == Items.ender_pearl) {
                        existing += stack.getEntityItem().stackSize;
                    }
                }
                int toDrop = numPearls - existing;
                if (toDrop > 0) {
                    evt.drops.add(
                            Util.createDrop(
                                    player.worldObj,
                                    new ItemStack(Items.ender_pearl, toDrop, 0),
                                    evt.entityLiving.posX,
                                    evt.entityLiving.posY,
                                    evt.entityLiving.posZ,
                                    false));
                }
            }
        }
    }

    protected void dropSkull(LivingDropsEvent evt, EntityPlayer player) {
        ItemStack skull = getSkullForEntity(evt.entityLiving);
        if (skull != null && !containsDrop(evt, skull)) {
            evt.drops.add(
                    Util.createEntityItem(
                            player.worldObj,
                            skull,
                            evt.entityLiving.posX,
                            evt.entityLiving.posY,
                            evt.entityLiving.posZ));
        }
    }

    private boolean handleBeheadingWeapons(EntityPlayer player, LivingDropsEvent evt) {
        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null || equipped.stackTagCompound == null) {
            return false;
        }
        NBTTagCompound infiToolRoot = equipped.getTagCompound().getCompoundTag("InfiTool");
        if (infiToolRoot == null) {
            return false;
        }

        boolean isCleaver = "tconstruct.items.tools.Cleaver".equals(equipped.getItem().getClass().getName());
        boolean hasBeheading = infiToolRoot.hasKey("Beheading");
        if (!isCleaver && !hasBeheading) {
            // Use default behavior if it is not a cleaver and doesn't have beheading
            return false;
        }

        if (!(evt.entityLiving instanceof EntityEnderman)) {
            // If its not an enderman just let TiC do its thing
            // We wont modify head drops at all
            return true;
        }

        float fromWeapon;
        if (isCleaver) {
            fromWeapon = Config.ticCleaverSkullDropChance;
        } else {
            fromWeapon = Config.vanillaSwordSkullChance;
        }
        float fromLooting = 0;
        if (hasBeheading) {
            fromLooting = Config.ticBeheadingSkullModifier * infiToolRoot.getInteger("Beheading");
        }
        float skullDropChance = fromWeapon + fromLooting;
        if (Math.random() <= skullDropChance) {
            dropSkull(evt, player);
        }
        return true;
    }

    private double getSkullDropChance(EntityPlayer player, LivingDropsEvent evt) {
        if (isWitherSkeleton(evt)) {
            if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
                return Config.darkSteelSwordWitherSkullChance
                        + (Config.darkSteelSwordWitherSkullLootingModifier * evt.lootingLevel);
            } else {
                return 0.01;
            }
        }
        float fromWeapon;
        float fromLooting;
        if (isEquippedAndPowered(player, Config.darkSteelSwordPowerUsePerHit)) {
            fromWeapon = Config.darkSteelSwordSkullChance;
            fromLooting = Config.darkSteelSwordSkullLootingModifier * evt.lootingLevel;
        } else {
            fromWeapon = Config.vanillaSwordSkullChance;
            fromLooting = Config.vanillaSwordSkullLootingModifier * evt.lootingLevel;
        }
        return fromWeapon + fromLooting;
    }

    protected boolean isWitherSkeleton(LivingDropsEvent evt) {
        return evt.entityLiving instanceof EntitySkeleton && ((EntitySkeleton) evt.entityLiving).getSkeletonType() == 1;
    }

    private boolean containsDrop(LivingDropsEvent evt, ItemStack skull) {
        for (EntityItem ei : evt.drops) {
            if (ei != null && ei.getEntityItem() != null
                    && ei.getEntityItem().getItem() == skull.getItem()
                    && ei.getEntityItem().getItemDamage() == skull.getItemDamage()) {
                return true;
            }
        }
        return false;
    }

    private ItemStack getSkullForEntity(EntityLivingBase entityLiving) {
        if (entityLiving instanceof EntitySkeleton) {
            int type = ((EntitySkeleton) entityLiving).getSkeletonType();
            if (type == 1) {
                return new ItemStack(Items.skull, 1, 1);
            } else {
                return new ItemStack(Items.skull, 1, 0);
            }
        } else if (entityLiving instanceof EntityZombie) {
            return new ItemStack(Items.skull, 1, 2);
        } else if (entityLiving instanceof EntityCreeper) {
            return new ItemStack(Items.skull, 1, 4);
        } else if (entityLiving instanceof EntityEnderman) {
            return new ItemStack(EnderIO.blockEndermanSkull);
        }

        return null;
    }

    protected void init() {
        GameRegistry.registerItem(this, getUnlocalizedName());
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity, EntityLivingBase playerEntity) {

        if (playerEntity instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) playerEntity;
            ItemStack sword = player.getCurrentEquippedItem();

            // Durability damage
            EnergyUpgrade eu = EnergyUpgrade.loadFromItem(stack);
            if (eu != null && eu.isAbsorbDamageWithPower(stack) && eu.getEnergy() > 0) {
                eu.extractEnergy(powerPerDamagePoint, false);

            } else {
                super.hitEntity(stack, entity, playerEntity);
            }

            // sword hit
            if (eu != null) {
                eu.writeToItem(sword);

                if (eu.getEnergy() > Config.darkSteelSwordPowerUsePerHit) {
                    extractEnergy(player.getCurrentEquippedItem(), Config.darkSteelSwordPowerUsePerHit, false);
                    String name = EntityList.getEntityString(entity);
                    if (entity instanceof EntityEnderman || ENDERZOO_ENDERMINY.equals(name)) {
                        entity.getEntityData().setBoolean("hitByDarkSteelSword", true);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        return EnergyUpgrade.receiveEnergy(container, maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        return EnergyUpgrade.extractEnergy(container, maxExtract, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        return EnergyUpgrade.getEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return EnergyUpgrade.getMaxEnergyStored(container);
    }

    @Override
    public boolean getIsRepairable(ItemStack i1, ItemStack i2) {
        // return i2 != null && i2.getItem() == EnderIO.itemAlloy && i2.getItemDamage() == Alloy.DARK_STEEL.ordinal();
        return false;
    }

    @Override
    public void addCommonEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        DarkSteelRecipeManager.instance.addCommonTooltipEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addBasicEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        DarkSteelRecipeManager.instance.addBasicTooltipEntries(itemstack, entityplayer, list, flag);
    }

    @Override
    public void addDetailedEntries(ItemStack itemstack, EntityPlayer entityplayer, List list, boolean flag) {
        if (!Config.addDurabilityTootip) {
            list.add(ItemUtil.getDurabilityString(itemstack));
        }
        String str = EnergyUpgrade.getStoredEnergyString(itemstack);
        if (str != null) {
            list.add(str);
        }
        list.add(EnumChatFormatting.WHITE + EnderIO.lang.localize("item." + name + "_sword.tooltip.line1"));
        if (EnergyUpgrade.itemHasAnyPowerUpgrade(itemstack)) {
            list.add(EnumChatFormatting.WHITE + EnderIO.lang.localize("item." + name + "_sword.tooltip.line2"));
            list.add(EnumChatFormatting.WHITE + EnderIO.lang.localize("item." + name + "_sword.tooltip.line3"));
        }
        DarkSteelRecipeManager.instance.addAdvancedTooltipEntries(itemstack, entityplayer, list, flag);
    }

    public ItemStack createItemStack() {
        return new ItemStack(this);
    }

    @Override
    public boolean isActive(EntityPlayer ep, ItemStack equipped) {
        return isTravelUpgradeActive(ep, equipped);
    }

    @Override
    public void extractInternal(ItemStack equipped, int power) {
        extractEnergy(equipped, power, false);
    }

    @Override
    public int canExtractInternal(ItemStack equipped, int power) {
        return Math.min(getEnergyStored(equipped), power);
    }

    private boolean isTravelUpgradeActive(EntityPlayer ep, ItemStack equipped) {
        return isEquipped(ep) && ep.isSneaking() && TravelUpgrade.loadFromItem(equipped) != null;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (isTravelUpgradeActive(player, stack)) {
            if (world.isRemote) {
                if (TravelController.instance.activateTravelAccessable(stack, world, player, TravelSource.STAFF)) {
                    player.swingItem();
                    return stack;
                }
            }

            long ticksSinceBlink = EnderIO.proxy.getTickCount() - lastBlickTick;
            if (ticksSinceBlink < 0) {
                lastBlickTick = -1;
            }
            if (Config.travelStaffBlinkEnabled && world.isRemote
                    && ticksSinceBlink >= Config.travelStaffBlinkPauseTicks) {
                if (TravelController.instance.doBlink(stack, player)) {
                    player.swingItem();
                    lastBlickTick = EnderIO.proxy.getTickCount();
                }
            }
            return stack;
        }

        return super.onItemRightClick(stack, world, player);
    }
}

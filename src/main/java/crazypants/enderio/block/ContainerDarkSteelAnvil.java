package crazypants.enderio.block;

import java.lang.reflect.Field;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.ReflectionHelper;
import crazypants.enderio.EnderIO;
import crazypants.enderio.config.Config;

public class ContainerDarkSteelAnvil extends ContainerRepair {

    private int x, y, z;

    private final Field _outputSlot = ReflectionHelper.findField(ContainerRepair.class, "outputSlot", "field_82852_f");
    private final Field _inputSlots = ReflectionHelper.findField(ContainerRepair.class, "inputSlots", "field_82853_g");
    private final Field _materialCost = ReflectionHelper
            .findField(ContainerRepair.class, "materialCost", "stackSizeToBeUsedInRepair", "field_82856_l");

    @SuppressWarnings("unchecked")
    public ContainerDarkSteelAnvil(InventoryPlayer playerInv, final World world, final int x, final int y, final int z,
            EntityPlayer player) {
        super(playerInv, world, x, y, z, player);

        final IInventory outputSlot, inputSlots;
        final int materialCost;

        try {
            outputSlot = (IInventory) _outputSlot.get(this);
            inputSlots = (IInventory) _inputSlots.get(this);
            materialCost = _materialCost.getInt(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.x = x;
        this.y = y;
        this.z = z;

        this.inventorySlots.set(2, new Slot(outputSlot, 2, 134, 47) {

            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            public boolean canTakeStack(EntityPlayer stack) {
                return (stack.capabilities.isCreativeMode
                        || stack.experienceLevel >= ContainerDarkSteelAnvil.this.maximumCost)
                        && ContainerDarkSteelAnvil.this.maximumCost > 0
                        && this.getHasStack();
            }

            public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
                if (!player.capabilities.isCreativeMode) {
                    player.addExperienceLevel(-ContainerDarkSteelAnvil.this.maximumCost);
                }

                inputSlots.setInventorySlotContents(0, (ItemStack) null);

                if (materialCost > 0) {
                    ItemStack itemstack1 = inputSlots.getStackInSlot(1);

                    if (itemstack1 != null && itemstack1.stackSize > materialCost) {
                        itemstack1.stackSize -= materialCost;
                        inputSlots.setInventorySlotContents(1, itemstack1);
                    } else {
                        inputSlots.setInventorySlotContents(1, (ItemStack) null);
                    }
                } else {
                    inputSlots.setInventorySlotContents(1, (ItemStack) null);
                }

                ContainerDarkSteelAnvil.this.maximumCost = 0;

                if (!player.capabilities.isCreativeMode && !world.isRemote
                        && world.getBlock(x, y, z) == EnderIO.blockDarkSteelAnvil
                        && player.getRNG().nextFloat() < Config.darkSteelAnvilDamageChance) {
                    int i1 = world.getBlockMetadata(x, y, z);
                    int k = i1 & 3;
                    int l = i1 >> 2;
                    ++l;

                    if (l > 2) {
                        world.setBlockToAir(x, y, z);
                        world.playAuxSFX(1020, x, y, z, 0);
                    } else {
                        world.setBlockMetadataWithNotify(x, y, z, k | l << 2, 2);
                        world.playAuxSFX(1021, x, y, z, 0);
                    }
                } else if (!world.isRemote) {
                    world.playAuxSFX(1021, x, y, z, 0);
                }
            }
        });
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return player.worldObj.getBlock(this.x, this.y, this.z) == EnderIO.blockDarkSteelAnvil;
    }
}

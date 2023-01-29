package crazypants.enderio.item.darksteel;

public interface IDarkSteelItem {

    int getIngotsRequiredForFullRepair();

    public interface IEndSteelItem extends IDarkSteelItem {
    }

    public interface IStellarItem extends IEndSteelItem {
    }
}

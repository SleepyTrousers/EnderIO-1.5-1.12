package crazypants.enderio.integration.tic.book;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tconstruct.library.book.TinkerBook;

public class TicBook {

  @SideOnly(Side.CLIENT)
  public static void integrate() {
    TinkerBook.INSTANCE.addRepository(new FileRepository("enderio:book"));
    TinkerBook.INSTANCE.addTransformer(new OurBookTransformer());
  }

}

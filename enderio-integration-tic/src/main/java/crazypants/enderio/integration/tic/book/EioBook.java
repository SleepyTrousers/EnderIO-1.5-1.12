package crazypants.enderio.integration.tic.book;

import crazypants.enderio.base.EnderIO;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;

@SideOnly(Side.CLIENT)
public class EioBook extends BookData {

  public final static BookData INSTANCE = BookLoader.registerBook(EnderIO.DOMAIN, false, false);

  public static void integrate() {
    INSTANCE.addRepository(new FileRepository(EnderIO.DOMAIN + ":eiobook"));
    INSTANCE.addTransformer(BookTransformer.IndexTranformer());
  }

}

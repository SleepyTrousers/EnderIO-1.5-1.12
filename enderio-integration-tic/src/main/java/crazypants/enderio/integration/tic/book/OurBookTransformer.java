package crazypants.enderio.integration.tic.book;

import slimeknights.mantle.client.book.BookTransformer;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.data.PageData;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.content.PageContent;
import slimeknights.tconstruct.library.TinkerRegistry;
import slimeknights.tconstruct.library.book.content.ContentListing;
import slimeknights.tconstruct.library.book.content.ContentModifier;
import slimeknights.tconstruct.library.modifiers.IModifier;

public class OurBookTransformer extends BookTransformer {

  public OurBookTransformer() {
  }

  @Override
  public void transform(BookData book) {
    SectionData section1 = null, section2 = null;
    for (SectionData section : book.sections) {
      if (section.name.equals("modifiers")) {
        section1 = section;
      }
      if (section.name.equals("eiomodifiers")) {
        section2 = section;
      }
    }
    if (section1 != null && section2 != null) {
      for (PageData page : section2.pages) {
        page.parent = section1;
        section1.pages.add(page);
      }
      PageData pageData = section1.pages.get(0);
      PageContent content = pageData.content;
      if (content instanceof ContentListing) {
        ContentListing listing = (ContentListing) content;
        for (PageData page : section2.pages) {
          page.parent = section1;
          if (page.content instanceof ContentModifier) {
            IModifier modifier = TinkerRegistry.getModifier(((ContentModifier) page.content).modifierName);
            if (modifier != null) {
              page.name = "page-eio-" + modifier.getIdentifier();
              listing.addEntry(modifier.getLocalizedName(), page);
            }
          }
        }
      }
      section2.pages.clear();
      book.sections.remove(section2);
    }
  }

}

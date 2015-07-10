package main.plugins.ebay;

import java.util.List;

import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;

public class EbayContentPageConfig extends ContentMenuSubEntry {

  private EbaySearchTermHistory searchTermHistory;
  
  public EbayContentPageConfig(EbayAPI api) {
    super("Config");
    this.searchTermHistory = api.getSearchTermHistory();
  }

  @Override
  public ContentItem handleAPIRequest(String parameter) {
    if(parameter.equals("createGroup"))
      return showCreateGroupDialog();
    return getMainPage();
  }
  
  private ContentPage getMainPage() {
    return buildPageFromSearchTermGroup(searchTermHistory.getTerms());
  }
  
  private ContentPage buildPageFromSearchTermGroup(EbaySearchTermGroup group) {
    ContentPage page = new ContentPage();
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Ebay Config", ContentText.TextType.TITLE));
    titleBar.addContentItem(new ContentButton(200, 5, "create Group", "createGroup"));
    
    List<EbaySearchTermBase> list = group.getTerms();
    for(EbaySearchTermBase term : list) {
      ContentGroup contentGroup = page.createContentGroup();
      contentGroup.put(new ContentText(5, 5, term.toString()));
    }
    return page;
  }
  
  private ContentItem showCreateGroupDialog() {
    ContentGroup group = new ContentGroup();
    group.put(new ContentText(5, 5, "create"));
    return group;
  }
}

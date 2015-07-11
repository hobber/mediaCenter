package main.plugins.ebay;

import java.util.List;
import java.util.Map;

import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentInputForm;
import main.server.content.ContentItem;
import main.server.content.ContentOverlay;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Path;

public class EbayContentPageConfig extends ContentMenuSubEntry {

  private EbayAPI api;
  private EbaySearchTermHistory searchTermHistory;
  
  public EbayContentPageConfig(EbayAPI api) {
    super("Config");
    this.api = api;
    this.searchTermHistory = api.getSearchTermHistory();
  }

  @Override
  public ContentItem handleAPIRequest(Map<String, String> parameters) {
    if(parameters.size() >= 0 && parameters.containsKey("parameter")) {
      String parameter = parameters.remove("parameter");
      if(parameter.equals("createGroup"))
        return showCreateGroupDialog();
      else if(parameter.equals("addGroup"))
        return addGroup(parameters.get("name"));
    }
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
    ContentOverlay overlay = new ContentOverlay("create Group", 300, 100);
    ContentInputForm form = new ContentInputForm("addGroup", "add");
    overlay.put(form);
    form.addInput("name", "name");    
    return overlay;
  }
  
  private ContentItem addGroup(String name) {
    Path path = new Path();
    EbaySearchTermGroup group = new EbaySearchTermGroup(api, name);
    searchTermHistory.addSearchTerm(path, group);
    return getMainPage();
  }
}

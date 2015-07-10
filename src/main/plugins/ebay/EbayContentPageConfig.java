package main.plugins.ebay;

import main.server.content.ContentGroup;
import main.server.content.ContentItem;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.content.ContentTextTree;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;

public class EbayContentPageConfig extends ContentMenuSubEntry {

  private EbayAPI api;
  
  public EbayContentPageConfig(EbayAPI api) {
    super("Config");
    this.api = api;
  }

  @Override
  public ContentItem handleAPIRequest(String parameter) {
    System.out.println("parameter: " + parameter + " (" + parameter.length() + ")");
    if(parameter.length() > 0)
      return loadCategories(Long.parseLong(parameter));
    return getMainPage();
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage();
    
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Ebay Config", ContentText.TextType.TITLE));

    ContentGroup group = new ContentGroup();
    page.addContentGroup(group);
    ContentTextTree tree = new ContentTextTree(20, 30);
    group.put(tree);
    
    EbayCategory rootCategory = api.loadCategory(-1);
    for(EbayCategory category : rootCategory.getChildren())
      if(category.isLeaf())
        tree.addLeaf("" + category.getId(), category.getName());
      else
        tree.addNode("" + category.getId(), category.getName());
    
    return page;
  }
  
  private ContentTextTree loadCategories(long id) {
    EbayCategory parentCategory = api.loadCategory(id);
    ContentTextTree tree = new ContentTextTree();
    for(EbayCategory category : parentCategory.getChildren())
      if(category.isLeaf())
        tree.addLeaf("" + category.getId(), category.getName());
      else
        tree.addNode("" + category.getId(), category.getName());
    return tree;
  }
}

package main.plugins.ebay;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.server.content.ContentButton;
import main.server.content.ContentGroup;
import main.server.content.ContentInputForm;
import main.server.content.ContentItem;
import main.server.content.ContentOptions;
import main.server.content.ContentOverlay;
import main.server.content.ContentPage;
import main.server.content.ContentText;
import main.server.menu.ContentMenuSubEntry;
import main.utils.Path;

public class EbayContentPageConfig extends ContentMenuSubEntry {

  private EbayAPI api;
  private EbaySearchTermHistory searchTermHistory;
  private LinkedList<EbaySearchTermBase> termList = new LinkedList<EbaySearchTermBase>();
  
  public EbayContentPageConfig(EbayAPI api) {
    super("Config");
    this.api = api;
    this.searchTermHistory = api.getSearchTermHistory();
  }

  @Override
  public ContentItem handleAPIRequest(Map<String, String> parameters) {
    if(parameters.size() >= 0 && parameters.containsKey("parameter")) {
      String parameter = parameters.remove("parameter");
      if(parameter.equals("createGroupDialog"))
        return showCreateGroupDialog(parameters.get("selectionId"));
      else if(parameter.equals("createGroup"))
        return addGroup(parameters.get("name"), parameters.get("selectionId"));
      else if(parameter.equals("createTermDialog"))
        return showCreateTermDialog(parameters.get("selectionId"));
      else if(parameter.equals("createTerm"))
        return addTerm(parameters.get("name"), parameters.get("selectionId"));
      else if(parameter.equals("renameItemDialog"))
        return showRenameItemDialog(parameters.get("selectionId"));
      else if(parameter.equals("renameItem"))
        return renameItem(parameters.get("name"), parameters.get("selectionId"));
      else if(parameter.equals("deleteItemDialog"))
        return showDeleteItemDialog(parameters.get("selectionId"));
      else if(parameter.equals("deleteItem"))
        return deleteItem(parameters.get("selectionId"));
    }
    return getMainPage();
  }
  
  private ContentPage getMainPage() {
    ContentPage page = new ContentPage("Ebay Config");
    ContentGroup description = page.createContentGroup();    
    description.put(new ContentText(5, 5, "To perform one of the following operations on one oft the elements first click on the element."));
    ContentGroup actions = page.createContentGroup();
    actions.put(new ContentButton(5, 5, "add group", "createGroupDialog"));
    actions.put(new ContentButton(90, 5, "add search term", "createTermDialog"));
    actions.put(new ContentButton(210, 5, "rename", "renameItemDialog"));
    actions.put(new ContentButton(279, 5, "delete", "deleteItemDialog"));
    actions.setOptions(new ContentOptions("groupBoarder", "true"));
    
    termList.clear();
    page.addContentGroups(buildTreeFromSearchTermGroup(searchTermHistory.getTerms(), 0));
    return page;
  }
  
  private List<ContentGroup> buildTreeFromSearchTermGroup(EbaySearchTermGroup group, int level) {
    String indent = "";
    for(int i = 0; i < level; i++)
      indent += "&nbsp;&nbsp;&nbsp;&nbsp;";
    
    List<ContentGroup> list = new LinkedList<ContentGroup>();
    List<EbaySearchTermBase> terms = group.getTerms();    
    for(EbaySearchTermBase term : terms) {      
      ContentGroup contentGroup = new ContentGroup();
      list.add(contentGroup);
      if(term instanceof EbaySearchTermGroup) {
        ContentText text = new ContentText(5, 5, indent + "&bull; " + term.getName());
        contentGroup.put(text);
        EbaySearchTermGroup termGroup = (EbaySearchTermGroup)term;
        text.setOptions(new ContentOptions("selectionId", Integer.toString(termList.size())));
        termList.add(termGroup);
        list.addAll(buildTreeFromSearchTermGroup(termGroup, level + 1));
      }
      else {
        ContentText text = new ContentText(5, 5, indent + "&ndash; " + term.getName());
        contentGroup.put(text);
        text.setOptions(new ContentOptions("selectionId", Integer.toString(termList.size())));
        termList.add(term);
      }
    }
    
    return list;
  }
  
  private ContentItem showCreateGroupDialog(String itemIndex) {
    ContentOverlay overlay = new ContentOverlay("Create group", 300, 100);
    
    Integer index = null;
    if(itemIndex != null)
      index = Integer.parseInt(itemIndex);
    
    if(index == null || (index >= 0 && index < termList.size() - 1 && termList.get(index) instanceof EbaySearchTermGroup)) {
      ContentInputForm form = new ContentInputForm("createGroup", "add");
      overlay.put(form);
      form.addInput("name", "name");
    }
    else
      overlay.put(new ContentText(5, 5, "Groups can only be added to groups!"));    
    return overlay;
  }
  
  private ContentItem addGroup(String name, String itemIndex) {
    if(itemIndex == null)
      searchTermHistory.addSearchTerm(new Path(), api.createSearchTermGroup(name));
    else {
      Path path = new Path();
      int index = Integer.parseInt(itemIndex);
      EbaySearchTermBase term = termList.get(index);
      if(term != null && term instanceof EbaySearchTermGroup)
        ((EbaySearchTermGroup)term).add(path, api.createSearchTermGroup(name));
    }
    return getMainPage();
  }
  
  private ContentItem showCreateTermDialog(String itemIndex) {
    ContentOverlay overlay = new ContentOverlay("Create search term", 300, 100);
    
    Integer index = null;
    if(itemIndex != null)
      index = Integer.parseInt(itemIndex);
    
    if(index == null || (index >= 0 && index < termList.size() - 1 && termList.get(index) instanceof EbaySearchTermGroup)) {
      ContentInputForm form = new ContentInputForm("createTerm", "add");
      overlay.put(form);
      form.addInput("search term", "name");
    }
    else
      overlay.put(new ContentText(5, 5, "Search terms can only be added to groups!"));    
    return overlay;
  }
  
  private ContentItem addTerm(String name, String itemIndex) {
    if(itemIndex == null)
      searchTermHistory.addSearchTerm(new Path(), api.createSearchTerm(name));
    else {
      Path path = new Path();
      int index = Integer.parseInt(itemIndex);
      EbaySearchTermBase term = termList.get(index);
      if(term != null && term instanceof EbaySearchTermGroup)
        ((EbaySearchTermGroup)term).add(path, api.createSearchTerm(name));
    }
    return getMainPage();
  }
  
  private ContentItem showRenameItemDialog(String itemIndex) {
    ContentOverlay overlay = new ContentOverlay("Rename item", 300, 100);
    
    Integer index = null;
    if(itemIndex != null)
      index = Integer.parseInt(itemIndex);
    
    if(index != null) {
      ContentInputForm form = new ContentInputForm("renameItem", "rename");
      overlay.put(form);
      form.addInput("new name", "name");
    }
    else
      overlay.put(new ContentText(5, 5, "You must select an item to rename it!"));    
    return overlay;
  }
  
  private ContentItem renameItem(String name, String itemIndex) {
    if(itemIndex != null && name.length() > 0) {
      int index = Integer.parseInt(itemIndex);
      EbaySearchTermBase term = termList.get(index);
      if(term != null)
        term.rename(name);
    }
    return getMainPage();
  }

  private ContentItem showDeleteItemDialog(String itemIndex) {
    ContentOverlay overlay = new ContentOverlay("Delete item", 300, 100);
    
    Integer index = null;
    if(itemIndex != null)
      index = Integer.parseInt(itemIndex);
    
    if(index != null) {
      ContentGroup warning = new ContentGroup();
      overlay.put(warning);
      warning.put(new ContentText(5, 5, "Are you sure that you want to delete this item?"));
      ContentInputForm form = new ContentInputForm("deleteItem", "delete");
      overlay.put(form);
    }
    else
      overlay.put(new ContentText(5, 5, "You must select an item to delete it!"));    
    return overlay;
  }
  
  private ContentItem deleteItem(String itemIndex) {
    if(itemIndex != null) {
      int index = Integer.parseInt(itemIndex);
      EbaySearchTermBase term = termList.get(index);
      if(term != null)
        term.delete();
    }
    return getMainPage();
  }
}

package main.plugins.austrianCharts;

import java.util.LinkedList;

import main.server.content.ContentGroup;
import main.server.content.ContentImage;
import main.server.content.ContentItem;
import main.server.content.ContentOptions;
import main.server.content.ContentPage;
import main.server.content.ContentTable;
import main.server.content.ContentText;
import main.server.content.ContentTitleBar;
import main.server.menu.ContentMenuSubEntry;

class AustrianChartsCurrent extends ContentMenuSubEntry {
  
  private AustrianCharts charts;
  
  public AustrianChartsCurrent(AustrianCharts charts) {
    super("Single Charts");
    this.charts = charts;
  }

  @Override
  public ContentPage handleAPIRequest(String parameter) {
    ContentPage page = new ContentPage();
    
    ContentTitleBar titleBar = new ContentTitleBar();
    page.setTitleBar(titleBar);
    titleBar.addContentItem(new ContentText(5, 5, "Austrian Single Charts", ContentText.TextType.TITLE));
    
    ContentGroup group = new ContentGroup();
    page.addContentGroup(group);
    ContentTable table = new ContentTable(0, 0, 3, 50);
    group.add(table);
    table.setOption(new ContentOptions("fullWidth", "true"));
    LinkedList<String> widths = new LinkedList<String>();
    widths.add("5%");
    widths.add("40%");
    widths.add("47%");
    table.setWidths(widths);
    
    int i = 0;
    for(ChartEntry entry : charts.getCurrentCharts()) {
      LinkedList<ContentItem> columns = new LinkedList<ContentItem>();   
      ContentGroup interpret = new ContentGroup();
      if(entry.getImage().length() > 0)
        interpret.add(new ContentImage(0, 0, 50, 50, entry.getImage()));
      interpret.add(new ContentText(55, 12, entry.getInterpret()));
      interpret.setOptions(new ContentOptions("groupBoarder", "false"));
      columns.add(new ContentText(0, 12, "" + ++i));
      columns.add(interpret);
      columns.add(new ContentText(0, 12, entry.getTitle()));
      table.addRow(columns);
    }
    
    System.out.println("response: " + page.getContentString());
    
    return page;
  }
}

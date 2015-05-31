package main.plugins.austrianCharts;

import java.util.LinkedList;

import main.server.content.ContentGroup;
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
    ContentTable table = new ContentTable(0, 0, 5, 50);
    group.put(table);
    table.setOption(new ContentOptions("fullWidth", "true"));
    LinkedList<String> widths = new LinkedList<String>();
    widths.add("10%");
    widths.add("30%");
    widths.add("10%");
    widths.add("30%");
    widths.add("20%");
    table.setWidths(widths);
    
    LinkedList<ContentItem> columns = new LinkedList<ContentItem>();    
    columns.add(new ContentText(0, 0, "a"));
    columns.add(new ContentText(0, 0, "b"));
    columns.add(new ContentText(0, 0, "c"));
    columns.add(new ContentText(0, 0, "d"));
    columns.add(new ContentText(0, 0, "e"));
    table.addRow(columns);
    
    System.out.println("response: " + page.getContentString());
    
    return page;
  }
}

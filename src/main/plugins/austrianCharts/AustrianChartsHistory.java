package main.plugins.austrianCharts;

import main.plugins.Plugin;
import main.server.content.ContentPage;
import main.server.menu.ContentMenuSubEntry;

import org.json.JSONObject;

class AustrianChartsHistory extends ContentMenuSubEntry {

  public AustrianChartsHistory(Plugin plugin) {
    super(plugin, "History");
  }

  @Override
  public ContentPage handleAPIRequest(String parameter) {
    return new ContentPage();
  }
}

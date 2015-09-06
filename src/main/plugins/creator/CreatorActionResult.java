package main.plugins.creator;

import main.server.content.ContentPage;

public class CreatorActionResult {

  private ContentPage page = null;
  private String error = null;
  
  public CreatorActionResult() {
    
  }
  
  public CreatorActionResult(ContentPage page) {
    this.page = page;
  }
  
  public CreatorActionResult(String error) {
    this.error = error;
  }
  
  public boolean isEmpty() {
    return page == null && error == null;
  }
  
  public boolean failed() {
    return error != null;
  }
  
  public String getError() {
    return error;
  }
  
  public ContentPage getPage() {
    return page;
  }
}

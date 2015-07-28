package main.plugins.ebay;

import java.io.IOException;
import java.util.Calendar;

import main.data.DataObject;
import main.data.DataSchema;
import main.data.datatypes.MCByte;
import main.data.datatypes.MCFloat;
import main.data.datatypes.MCInteger;
import main.data.datatypes.MCLong;
import main.data.datatypes.MCString;
import main.plugins.ebay.EbayAPI.AuctionType;
import main.utils.FileReader;

public class EbayMinimalItem extends DataObject {

  private MCLong id;
  private MCByte type;
  private MCLong endTime;
  private MCFloat price;
  private MCLong categoryId;
  private MCString title;
  private MCInteger imageId;
  private String string;
  
  public EbayMinimalItem(String id, String title, AuctionType type, Calendar endTime, float price, int imageId) {
    this.title.set(title);
    this.id.set(Long.parseLong(id));
    this.type.set((byte)type.ordinal());
    this.endTime.set(endTime.getTimeInMillis());
    this.price.set(price);
    this.imageId.set(imageId);
    createString();
  }
  
  public EbayMinimalItem(FileReader file) throws IOException {
    super(file);
    createString();
  }
  
  private void createString() {
    Calendar date = Calendar.getInstance();
    date.setTimeInMillis(endTime.get());
    string = title.get() + "(" + id.get() + ") until " + EbayContentPageReport.convertToPrintDate(date) + " costs " + price + 
        " (" + EbayContentPageReport.getAuctionTypeString(AuctionType.values()[type.get()]) + ", image " + imageId.get() + ")";
  }

  @Override
  protected DataSchema createDataSchema() {
    id = new MCLong();
    type = new MCByte();
    endTime = new MCLong();
    price = new MCFloat();
    categoryId = new MCLong();
    title = new MCString();
    imageId = new MCInteger();
    
    DataSchema schema = new DataSchema();
    schema.addLong("id", id);
    schema.addByte("type", type);
    schema.addLong("endTime", endTime);
    schema.addFloat("price", price);
    schema.addLong("categoryId", this.categoryId);
    schema.addString("title", title);
    schema.addInt("imageId", imageId);
    return schema;
  }

  public Long getId() {
    return id.get();
  }
  
  public Long getEndTime() {
    return endTime.get();
  }
  
  public float getPrice() {
    return price.get();
  }
  
  public long getCategoryId() {
    return categoryId.get();
  }
  
  public String getTitle() {
    return title.get();
  }
  
  public int getImageId() {
    return imageId.get();
  }

  @Override
  public String toString() {
    return string;
  }
}

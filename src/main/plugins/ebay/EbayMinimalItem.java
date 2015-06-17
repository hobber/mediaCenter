package main.plugins.ebay;

import java.io.IOException;
import java.util.Calendar;

import main.data.DataObject;
import main.data.DataSchema;
import main.data.datatypes.MCByte;
import main.data.datatypes.MCFloat;
import main.data.datatypes.MCLong;
import main.plugins.ebay.EbayReporter.AuctionType;
import main.utils.FileReader;

public class EbayMinimalItem extends DataObject {

  private MCLong id;
  private MCByte type;
  private MCLong endTime;
  private MCFloat price;
  private String string;
  
  public EbayMinimalItem(String id, AuctionType type, Calendar endTime, float price) {
    this.id.set(Long.parseLong(id));
    this.type.set((byte)type.ordinal());
    this.endTime.set(endTime.getTimeInMillis());
    this.price.set(price);
    createString();
  }
  
  public EbayMinimalItem(FileReader file) throws IOException {
    super(file);
    createString();
  }
  
  private void createString() {
    Calendar date = Calendar.getInstance();
    date.setTimeInMillis(endTime.get());
    string = id.get() + " until " + EbayReport.convertToPrintDate(date) + " costs " + price + 
        " (" + EbayReport.getAuctionTypeString(AuctionType.values()[type.get()]) + ")";
  }

  @Override
  protected DataSchema createDataSchema() {
    id = new MCLong();
    type = new MCByte();
    endTime = new MCLong();
    price = new MCFloat();
    
    DataSchema schema = new DataSchema();
    schema.addLong("id", id);
    schema.addByte("type", type);
    schema.addLong("endTime", endTime);
    schema.addFloat("price", price);
    return schema;
  }

  public long getId() {
    return id.get();
  }

  @Override
  public String toString() {
    return string;
  }
}

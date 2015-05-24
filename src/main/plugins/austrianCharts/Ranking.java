package main.plugins.austrianCharts;

import java.io.IOException;

import main.data.DataSchemaObjectInterface;
import main.data.datatypes.MCList;
import main.utils.FileReader;
import main.utils.FileWriter;

class Ranking extends MCList<RankingEntry> implements DataSchemaObjectInterface {

  @Override
  public void readValue(FileReader file) throws IOException {
    int size = file.readInt();
    for(int i=0; i<size; i++)
      value.add(RankingEntry.createFromFile(file));
  }

  @Override
  public void writeValue(FileWriter file) throws IOException {
    file.writeInt(value.size());
    for(RankingEntry entry : value)
      entry.writeValue(file);
  }
}

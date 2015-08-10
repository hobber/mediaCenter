package main.server.content;


public class ContentImage extends ContentItem {
  
	public ContentImage(int x, int y, int width, int height, String src) {
	  super("image");
	  setAttribute("x", x);
	  setAttribute("y", y);
	  setAttribute("width", width);
	  setAttribute("height", height);
	  setAttribute("src", src);
	}
}

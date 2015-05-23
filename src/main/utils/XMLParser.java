package main.utils;

import java.util.Stack;

public class XMLParser {
  public static XMLElement parse(String body, boolean printErrors) {    
    XMLElement rootElement = new XMLElement();  
    byte[] bytes = body.getBytes();
    final int length = bytes.length;        
    Stack<XMLElement> stack = new Stack<XMLElement>();
    stack.push(rootElement);
    boolean isScript = false;
    int scriptStartIndex = -1;
    
    for(int i=0; i<length; i++) {     
      //update line counter, ignore spaces
      if(bytes[i] == '\r')
        i++;      
      if(bytes[i] == '\n')      
        continue;     
      if(bytes[i] == ' ' || bytes[i] == '\t')
        continue;
      
      //search start of a tag
      int startIndex = i;
      if(bytes[startIndex] != '<') {
        int contentStartIndex = startIndex;
        while(startIndex < length - 1 && bytes[startIndex] != '<')
          startIndex++;
        
        if(isScript == false) {
          String content = body.substring(contentStartIndex, startIndex);
          stack.peek().addChild(new XMLElement(XMLElement.Type.VALUE, content));
        }
      }
      
      if(startIndex >= length - 2)
        break;
      
      //check if end tag
      int nameStartIndex = startIndex + 1;
      boolean isEndTag = false;
      if(bytes[nameStartIndex] == '/') {
        isEndTag = true;
        nameStartIndex++;
      }
      
      //check if comment
      else if(bytes[nameStartIndex] == '!') {
        if(nameStartIndex < length - 3 && bytes[nameStartIndex + 1] == '-' && bytes[nameStartIndex + 2] == '-') {
          int commentEndIndex = nameStartIndex + 3;         
          while(commentEndIndex < length - 3 && (bytes[commentEndIndex] != '-' || bytes[commentEndIndex + 1] != '-' || bytes[commentEndIndex + 2] != '>'))
            commentEndIndex++;
          String comment = body.substring(nameStartIndex-1, commentEndIndex + 3);
          stack.peek().addChild(new XMLElement(XMLElement.Type.COMMENT, comment));
          i = commentEndIndex + 2;
          continue;
        }
      }
      
      //check if it was less operator in script
      if(isScript && isEndTag == false)
        continue;     
      
      //search end of tag name      
      int nameEndIndex = nameStartIndex + 1;
      while(nameEndIndex < length - 1 && bytes[nameEndIndex] != ' ' && bytes[nameEndIndex] != '>')
        nameEndIndex++;
      
      //read tag name
      String tagName = body.substring(nameStartIndex, nameEndIndex);      
      if(isScript && tagName.toLowerCase().equals("script") == false)         
        continue;       
      
      //search end of tag
      int endIndex = nameEndIndex;
      while(endIndex < length - 1 && bytes[endIndex] != '>')
        endIndex++;
      
      if(tagName.toLowerCase().equals("script")) {
        if(isScript == false) {
          isScript = true;
          scriptStartIndex = endIndex + 1;
        }
        else {
          if(scriptStartIndex >= 0) {
            String script = body.substring(scriptStartIndex, startIndex);
            XMLElement newTag = new XMLElement(XMLElement.Type.SCRIPT, script);
            stack.peek().addChild(newTag);
          }
          isScript = false;
          scriptStartIndex = -1;        
        }
      }
      
      //check if it is a singleton element
      boolean isSingleton = bytes[endIndex - 1] == '/';     
      
      i = endIndex;

      String tag = body.substring(startIndex, endIndex + 1);
      if(isEndTag) {
        //check for stray end tag
        if(stack.contains(new XMLElement(tag, tagName)) == false) {
          if(printErrors)
            Logger.error("XMLParser: found stray tag " + tag + " at index " + startIndex);
          continue;
        }
        
        //pop from stack until according open tag is found
        do {
          XMLElement top = stack.pop();
          if(top.getType() == XMLElement.Type.ELEMENT &&  top.getTagName().equals(tagName)) {
            top.setEndTag(tag);
            break;
          }
        } while(stack.isEmpty() == false);
      }
      else {
        XMLElement newTag = new XMLElement(tag, tagName);
        stack.peek().addChild(newTag);
        if(isSingleton == false && isVoidElement(tagName) == false)
          stack.push(newTag);
      }     
    }
    return rootElement;
  }
  
  //http://webdesign.about.com/od/htmltags/qt/html-void-elements.htm
  private static boolean isVoidElement(String tagName) {
    if(tagName.equalsIgnoreCase("area") ||
       tagName.equalsIgnoreCase("base") ||
       tagName.equalsIgnoreCase("br") ||
       tagName.equalsIgnoreCase("col") ||
       tagName.equalsIgnoreCase("command") ||
       tagName.equalsIgnoreCase("embed") ||
       tagName.equalsIgnoreCase("hr") ||
       tagName.equalsIgnoreCase("img") ||
       tagName.equalsIgnoreCase("input") ||
       tagName.equalsIgnoreCase("link") ||
       tagName.equalsIgnoreCase("meta") ||
       tagName.equalsIgnoreCase("param") ||
       tagName.equalsIgnoreCase("source") ||
       tagName.equalsIgnoreCase("!doctype"))
      return true;
    return false;
  }
}

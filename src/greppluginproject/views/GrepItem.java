package greppluginproject.views;

import java.nio.file.Path;

public class GrepItem {

  /**
   * The class to hold the grep results.  It stores the line number where
   * the result came from and the specific line number.
   * 
   * The class currently only contains constructors, getters, and setters.
   * 
   */
  
  Path path;
  int lineNumber;
  String line;
  
  public GrepItem(){
  }

  public GrepItem(Path path, int lineNumber, String line) {
    super();
    this.path = path;
    this.lineNumber = lineNumber;
    this.line = line;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public Path getPath() {
    return path;
  }

  public void setPath(Path path) {
    this.path = path;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }
  
  public String toString()
  {
    return line;
  }
}

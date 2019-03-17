package ch.heigvd.res.labio.impl.filters;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

  private int lineNumber = 1;
  private boolean backslashR = false;

  private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());

  public FileNumberingFilterWriter(Writer out) {
    super(out);
  }

  @Override
  public void write(String str, int off, int len) throws IOException {
    write(str.toCharArray(), off, len);
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    for (int i = off; i < off + len; ++i)
      write(cbuf[i]);
  }

  @Override
  public void write(int c) throws IOException {
    char tabChar = '\t';

    //Première ligne (1 fois)
    if (lineNumber == 1) { //Si on est à la première ligne, on insére le numéro
      super.write(String.valueOf(lineNumber++) + tabChar);
      super.write(c);

    //Si le caractére est \n ou \r suivi de \n
    } else if ('\n' == (char) c) {
      backslashR = false;
      super.write(c);
      super.write(String.valueOf(lineNumber++) + tabChar);

    //Si le caractére est \r (mais on ne sait pas ce qu'il ya après)
    } else if ('\r' == (char) c) {
      backslashR = true;
      super.write(c);

    //Caractére quelquonque qprès un \r
    } else if (backslashR) {
      backslashR = false;
      super.write(String.valueOf(lineNumber++) + tabChar);
      super.write(c);

     //Caractère quelquonque
    } else
      super.write(c);
  }
}

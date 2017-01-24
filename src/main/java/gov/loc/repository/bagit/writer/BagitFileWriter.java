package gov.loc.repository.bagit.writer;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagit.domain.Version;

public final class BagitFileWriter {
  private static final Logger logger = LoggerFactory.getLogger(BagitFileWriter.class);
  
  private static final Version ONE_DOT_ZERO = new Version(1, 0);
  
  private BagitFileWriter(){
    //intentionally left empty
  }
  
  /**
   * Write the bagit.txt file in required UTF-8 encoding.
   * 
   * @param version the version of the bag to write out
   * @param encoding the encoding of the tag files
   * @param outputDir the root of the bag
   * 
   * @throws IOException if there was a problem writing the file
   */
  public static void writeBagitFile(final Version version, final Charset encoding, final Path outputDir) throws IOException{
    writeBagitFileInternal(version, encoding, null, null, outputDir);
  }
  
  /**
   * Write the bagit.txt file in required UTF-8 encoding for versions 1.0+
   * 
   * @param version the version of the bag to write out
   * @param encoding the encoding of the tag files
   * @param payloadByteCount the total number of bytes for all files in the payload directory
   * @param payloadFileCount the total number of files in the payload directory
   * @param outputDir the root of the bag
   * 
   * @throws IOException if there was a problem writing the file
   */
  public static void writeBagitFile(final Version version, final Charset encoding, final Long payloadByteCount, 
      final Long payloadFileCount, final Path outputDir) throws IOException{
    writeBagitFileInternal(version, encoding, payloadByteCount, payloadFileCount, outputDir);
  }
  
  private static void writeBagitFileInternal(final Version version, final Charset encoding, final Long payloadByteCount, 
      final Long payloadFileCount, final Path outputDir) throws IOException{
    final Path bagitPath = outputDir.resolve("bagit.txt");
    logger.debug("Writing bagit.txt file to [{}]", outputDir);
    
    
    final String firstLine = "BagIt-Version : " + version + System.lineSeparator();
    logger.debug("Writing line [{}] to [{}]", firstLine, bagitPath);
    Files.write(bagitPath, firstLine.getBytes(StandardCharsets.UTF_8), 
        StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
    
    final String secondLine = "Tag-File-Character-Encoding : " + encoding + System.lineSeparator();
    logger.debug("Writing line [{}] to [{}]", secondLine, bagitPath);
    Files.write(bagitPath, secondLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    
    if(version.compareTo(ONE_DOT_ZERO) >= 0 && payloadByteCount != null && payloadFileCount != null){ //if it is 1.0 or greater
      final String thirdLine = "Payload-Byte-Count : " + payloadByteCount + System.lineSeparator();
      Files.write(bagitPath, thirdLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
      
      final String fourthLine = "Payload-File-Count : " + payloadFileCount + System.lineSeparator();
      Files.write(bagitPath, fourthLine.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
    }
  }
}

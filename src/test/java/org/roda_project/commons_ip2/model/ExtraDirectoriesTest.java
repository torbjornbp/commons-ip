/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/commons-ip
 */
package org.roda_project.commons_ip2.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.roda_project.commons_ip.utils.IPException;
import org.roda_project.commons_ip.utils.IPEnums;
import org.roda_project.commons_ip2.model.impl.eark.EARKSIP;
import org.roda_project.commons_ip2.model.impl.eark.out.writers.strategy.WriteStrategy;
import org.roda_project.commons_ip2.cli.utils.SIPBuilderUtils;
import org.roda_project.commons_ip2.cli.model.enums.WriteStrategyEnum;
import org.roda_project.commons_ip2.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraDirectoriesTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExtraDirectoriesTest.class);
  private static Path tempFolder;

  @BeforeClass
  public static void setup() throws Exception {
    tempFolder = Files.createTempDirectory("extradirs-test");
  }

  @AfterClass
  public static void cleanup() throws Exception {
    Utils.deletePath(tempFolder);
  }

  @Test
  public void testExtraDirectories() throws Exception {
    // Create a simple SIP
    SIP sip = new EARKSIP("SIP_WITH_EXTRA_DIRS", IPContentType.getMIXED(), 
        IPContentInformationType.getMIXED(), "2.1.0");
    
    // Add an extra directory at the IP level
    sip.addExtraDirectory("extra_dir_1");
    List<String> extraDirs = sip.getExtraDirectories();
    assertEquals(1, extraDirs.size());
    assertEquals("extra_dir_1", extraDirs.get(0));
    
    // Add a file to the extra directory
    IPFile extraFile = new IPFile(Paths.get("src/test/resources/data/descriptive.txt"));
    extraFile.setRenameTo("renamed.txt");
    sip.addFileToExtraDirectory("extra_dir_1", extraFile);
    
    // Verify the file was added correctly
    List<IPFileInterface> extraDirFiles = sip.getExtraDirectoryFiles("extra_dir_1");
    assertEquals(1, extraDirFiles.size());
    assertEquals("renamed.txt", extraDirFiles.get(0).getFileName());
    
    // Add another file to the same directory
    IPFile extraFile2 = new IPFile(Paths.get("src/test/resources/data/data.txt"));
    sip.addFileToExtraDirectory("extra_dir_1", extraFile2);
    assertEquals(2, sip.getExtraDirectoryFiles("extra_dir_1").size());
    
    // Create representation and test extra directories there too
    IPRepresentation representation = new IPRepresentation("rep1");
    representation.addExtraDirectory("rep_extra_dir");
    assertEquals(1, representation.getExtraDirectories().size());
    
    // Add file to representation extra directory
    IPFile repExtraFile = new IPFile(Paths.get("src/test/resources/data/earkweb.log"));
    representation.addFileToExtraDirectory("rep_extra_dir", repExtraFile);
    assertEquals(1, representation.getExtraDirectoryFiles("rep_extra_dir").size());
    
    // Add representation to SIP
    sip.addRepresentation(representation);
    
    // Build SIP with extra directories
    WriteStrategy writeStrategy = SIPBuilderUtils.getWriteStrategy(WriteStrategyEnum.ZIP, tempFolder);
    Path zipSIP = sip.build(writeStrategy);
    
    // Verify SIP was created successfully
    assertTrue(Files.exists(zipSIP));
    
    // Parse SIP and verify everything is there
    EARKSIP parsedSip = new EARKSIP();
    SIP parsed = parsedSip.parse(zipSIP, tempFolder);
    
    // Verify IP-level extra directories
    assertEquals(1, parsed.getExtraDirectories().size());
    assertEquals("extra_dir_1", parsed.getExtraDirectories().get(0));
    assertEquals(2, parsed.getExtraDirectoryFiles("extra_dir_1").size());
    
    // Verify representation extra directories
    List<IPRepresentation> parsedReps = parsed.getRepresentations();
    assertEquals(1, parsedReps.size());
    IPRepresentation parsedRep = parsedReps.get(0);
    assertEquals(1, parsedRep.getExtraDirectories().size());
    assertEquals("rep_extra_dir", parsedRep.getExtraDirectories().get(0));
    assertEquals(1, parsedRep.getExtraDirectoryFiles("rep_extra_dir").size());
  }
}
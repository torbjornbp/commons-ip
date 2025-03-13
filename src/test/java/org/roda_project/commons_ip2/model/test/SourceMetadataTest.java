package org.roda_project.commons_ip2.model.test;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.roda_project.commons_ip2.model.IPFile;
import org.roda_project.commons_ip2.model.IPMetadata;
import org.roda_project.commons_ip2.model.IPRepresentation;
import org.roda_project.commons_ip2.model.MetadataType;
import org.roda_project.commons_ip2.model.SIP;
import org.roda_project.commons_ip2.model.impl.eark.EARKSIP;
import org.roda_project.commons_ip2.model.impl.eark.out.writers.factory.ZipWriteStrategyFactory;
import org.roda_project.commons_ip2.model.impl.eark.out.writers.strategy.WriteStrategy;

/**
 * Test class for verifying source metadata functionality.
 */
public class SourceMetadataTest {

  public static void main(String[] args) throws Exception {
    // Create SIP
    SIP sip = new EARKSIP("sip1", null, null, "2.2.0");
    sip.setDescription("Test SIP with representation-level source metadata");

    // Create representation
    IPRepresentation representation = new IPRepresentation("primary");
    sip.addRepresentation(representation);

    // Add representation data
    representation.addFile(new IPFile(Paths.get("/Users/torbjornpedersen/testpakke/primary/Untitled01.mov")));

    // Create and add source metadata to representation
    Path metadataPath = Paths.get("/Users/torbjornpedersen/testpakke/dummy_source.xml");
    IPMetadata sourceMetadata = new IPMetadata(new IPFile(metadataPath), new MetadataType("other"));
    representation.addSourceMetadata(sourceMetadata);

    // Build the SIP
    WriteStrategy writeStrategy = new ZipWriteStrategyFactory().create(Paths.get("/tmp"));
    Path sipPath = sip.build(writeStrategy);

    System.out.println("Created SIP at: " + sipPath);
  }
}
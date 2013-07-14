package co.leantechniques.maven.buildtime;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MojoTimerTest {

    private FakeLogger logger;

    @Before
    public void setUp() throws Exception {
        logger = new FakeLogger();
    }

    @Test
    public void outputWithSubsecond() {
        new MojoTimer("1234567890", 100,200).write(logger);

                    //coefficient-core .................................. SUCCESS [0.846s]
                    //coefficient-core .................................. SUCCESS [100.846s]
        assertEquals("  1234567890 ............................................... [0.100s]", logger.output());
    }

    @Test
    public void outputWith100Seconds() {
        new MojoTimer("1234567890", 0,100100).write(logger);

        assertEquals("  1234567890 ............................................... [100.100s]", logger.output());
    }

    @Test
    public void outputWithLongPluginName() {
        new MojoTimer("Some really long project name", 0,100100).write(logger);
        assertEquals("  Some really long project name ............................ [100.100s]", logger.output());
    }
}

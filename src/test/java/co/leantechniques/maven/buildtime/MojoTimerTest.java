package co.leantechniques.maven.buildtime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MojoTimerTest {
    @Mock
    private Logger logger;

    @Test
    public void outputWithSubsecond() {
        new MojoTimer("1234567890", 100,200).write(logger);

                    //coefficient-core .................................. SUCCESS [0.846s]
                    //coefficient-core .................................. SUCCESS [100.846s]
        verify(logger).info("  1234567890 ............................................... [0.100s]");
    }

    @Test
    public void outputWith100Seconds() {
        new MojoTimer("1234567890", 0,100100).write(logger);
        verify(logger).info("  1234567890 ............................................... [100.100s]");
    }

    @Test
    public void outputWithLongPluginName() {
        new MojoTimer("Some really long project name", 0,100100).write(logger);
        verify(logger).info("  Some really long project name ............................ [100.100s]");
    }
}

package co.leantechniques.maven.buildtime;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class MojoTimerTest {
    @Mock
    private Logger logger;

    private LogOutput logOutput;

    @Before
    public void setUp() {
        logOutput = new LogOutput(logger, true);
    }

    @Test
    public void outputWithSubsecond() {
        new MojoTimer("1234567890", 100,200).write(logOutput);

                    //coefficient-core .................................. SUCCESS [0.846s]
                    //coefficient-core .................................. SUCCESS [100.846s]
        verify(logger).info("  1234567890 ............................................... [0.100s]");
    }

    @Test
    public void outputWith100Seconds() {
        new MojoTimer("1234567890", 0,100100).write(logOutput);
        verify(logger).info("  1234567890 ............................................... [100.100s]");
    }

    @Test
    public void outputWithLongPluginName() {
        new MojoTimer("Some really long project name", 0,100100).write(logOutput);
        verify(logger).info("  Some really long project name ............................ [100.100s]");
    }


    @Test
    public void outputToDebug() {
        logOutput = new LogOutput(logger, false);
        new MojoTimer("Some really long project name", 0,100100).write(logOutput);
        verify(logger).debug("  Some really long project name ............................ [100.100s]");
    }

    @Test
    public void outputWithNameAtTheMaxLength() {
        new MojoTimer("Some really,  really,  really,  really,  long project name", 0,100100).write(logOutput);
        verify(logger).info("  Some really,  really,  really,  really,  long project name [100.100s]");
    }

    @Test
    public void outputWithNameOverTheMaxLength() {
        new MojoTimer("Some really,  really,  really,  really,  really,  long project name", 0,100100).write(logOutput);
        verify(logger).info("  Some really,  really,  really,  really,  really,  long pro [100.100s]");
    }
}

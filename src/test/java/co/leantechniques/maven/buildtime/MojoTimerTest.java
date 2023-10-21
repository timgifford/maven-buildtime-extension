package co.leantechniques.maven.buildtime;

import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import co.leantechniques.maven.buildtime.output.LogReporter;

@RunWith(MockitoJUnitRunner.class)
public class MojoTimerTest {
    @Mock
    private Logger logger;

    private LogOutput logOutput;
    LogReporter.LogReportVisitor reportVisitor;

    @Before
    public void setUp() {
        logOutput = new LogOutput(logger, true);
        reportVisitor = new LogReporter.LogReportVisitor(logOutput);
    }

    @Test
    public void outputWithSubsecond() {
        new MojoTimer("proj", "1234567890", 100,200).accept(reportVisitor);

                    //coefficient-core .................................. SUCCESS [0.846s]
                    //coefficient-core .................................. SUCCESS [100.846s]
        verify(logger).info("  1234567890 ............................................... [0.100s]");
    }

    @Test
    public void outputWith100Seconds() {
        new MojoTimer("proj", "1234567890", 0,100100).accept(reportVisitor);
        verify(logger).info("  1234567890 ............................................... [100.100s]");
    }

    @Test
    public void outputWithLongPluginName() {
        new MojoTimer("proj", "Some really long project name", 0,100100).accept(reportVisitor);
        verify(logger).info("  Some really long project name ............................ [100.100s]");
    }


    @Test
    public void outputToDebug() {
        logOutput = new LogOutput(logger, false);
        reportVisitor = new LogReporter.LogReportVisitor(logOutput);
        new MojoTimer("proj", "Some really long project name", 0,100100).accept(reportVisitor);
        verify(logger).debug("  Some really long project name ............................ [100.100s]");
    }

    @Test
    public void outputWithNameAtTheMaxLength() {
        new MojoTimer("proj", "Some really,  really,  really,  really,  long project name", 0,100100).accept(reportVisitor);
        verify(logger).info("  Some really,  really,  really,  really,  long project name [100.100s]");
    }

    @Test
    public void outputWithNameOverTheMaxLength() {
        new MojoTimer("proj", "Some really,  really,  really,  really,  really,  long project name", 0,100100).accept(reportVisitor);
        verify(logger).info("  Some really,  really,  really,  really,  really,  long pro [100.100s]");
    }
}

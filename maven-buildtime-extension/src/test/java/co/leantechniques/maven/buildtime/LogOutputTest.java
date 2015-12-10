package co.leantechniques.maven.buildtime;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LogOutputTest {

    private Logger logger;

    @Before
    public void setUp() throws Exception {
        logger = mock(Logger.class);
    }

    @Test
    public void outputToDebug() {
        new LogOutput(logger, false).log("some message");

        verify(logger).debug(anyString());
        verify(logger, never()).info(anyString());
    }

    @Test
    public void outputToInfo() {
        new LogOutput(logger, true).log("some message");

        verify(logger, never()).debug(anyString());
        verify(logger).info(anyString());
    }
}
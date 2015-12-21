package co.leantechniques.maven.buildtime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import co.leantechniques.maven.buildtime.publishers.MavenPublisher;
import org.apache.maven.execution.ExecutionEvent;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class MavenPublisherTest {
    private MavenPublisher mavenPublisher;
    private org.slf4j.Logger mockLogger;

    @Before
    public void setUp() {
        mockLogger = mock(org.slf4j.Logger.class);
        mavenPublisher = new MavenPublisher(null, mockLogger);
    }

    @Test
    public void noPlugin(){
        ExecutionEvent event = mock(ExecutionEvent.class);
        Map<String, ProjectTimer> projects = new LinkedHashMap<String, ProjectTimer>();
        SystemClock mockClock = mock(SystemClock.class);
        SessionTimer sessionTimer = new SessionTimer(projects, mockClock);

        mavenPublisher.publish(event, sessionTimer);


    }

    @Test
    public void outputWithSubsecond() {
        String actual = mavenPublisher.getFormattedMojoTimeLine(new MojoTimer("1234567890", 100, 200));
        String expected = "  1234567890 ............................................... [0.100s]";
        assertThat(actual, is(expected));
    }

    @Test
    public void outputWith100Seconds() {
        String actual = mavenPublisher.getFormattedMojoTimeLine(new MojoTimer("1234567890", 0, 100100));
        String expected = "  1234567890 ............................................... [100.100s]";
        assertThat(actual, is(expected));
    }

    @Test
    public void outputWithLongPluginName() {
        String actual = mavenPublisher.getFormattedMojoTimeLine(new MojoTimer("Some really long project name", 0, 100100));
        String expected = "  Some really long project name ............................ [100.100s]";
        assertThat(actual, is(expected));
    }

    @Test
    public void outputWithNameAtTheMaxLength() {
        String actual = mavenPublisher.getFormattedMojoTimeLine(new MojoTimer("Some really,  really,  really,  really,  long project name", 0, 100100));
        String expected = "  Some really,  really,  really,  really,  long project name [100.100s]";
        assertThat(actual, is(expected));
    }

    @Test
    public void outputWithNameOverTheMaxLength() {
        String actual = mavenPublisher.getFormattedMojoTimeLine(new MojoTimer("Some really,  really,  really,  really,  really,  long project name", 0, 100100));
        String expected = "  Some really,  really,  really,  really,  really,  long pro [100.100s]";
        assertThat(actual, is(expected));
    }
}

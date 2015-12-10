package co.leantechniques.maven.buildtime;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import co.leantechniques.maven.buildtime.publisher.MavenPublisher;
import org.junit.Before;
import org.junit.Test;

public class MavenPublisherTest {
    private MavenPublisher mavenPublisher;

    @Before
    public void setUp() {
        mavenPublisher = new MavenPublisher(null);
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

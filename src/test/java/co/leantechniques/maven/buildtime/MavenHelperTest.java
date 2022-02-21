package co.leantechniques.maven.buildtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MavenHelperTest {

	@Mock
	private ExecutionEvent sessionEndEvent;

	@Mock
	private MavenSession session;

	@BeforeEach
	public void setUp() {
		when( sessionEndEvent.getSession() ).thenReturn( session );
	}

	@Test
	void testPropertyPriorityUser() {
		Properties systemProperties = new Properties();
		systemProperties.setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "systemProperty" );
		MavenProject mavenProject = new MavenProject();
		mavenProject.getProperties().setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "projectProperty" );
		Properties userProperties = new Properties();
		userProperties.setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "userProperty" );

		when( session.getSystemProperties() ).thenReturn( systemProperties );
		when( session.getUserProperties() ).thenReturn( userProperties );
		when( session.getTopLevelProject() ).thenReturn( mavenProject );

		assertEquals( "userProperty", MavenHelper.getExecutionProperty( sessionEndEvent, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "default" ) );
	}

	@Test
	void testPropertyPriorityProject() {
		Properties systemProperties = new Properties();
		systemProperties.setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "systemProperty" );
		MavenProject mavenProject = new MavenProject();
		mavenProject.getProperties().setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "projectProperty" );
		Properties userProperties = new Properties();

		when( session.getSystemProperties() ).thenReturn( systemProperties );
		when( session.getUserProperties() ).thenReturn( userProperties );
		when( session.getTopLevelProject() ).thenReturn( mavenProject );

		assertEquals( "projectProperty", MavenHelper.getExecutionProperty( sessionEndEvent, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "default" ) );
	}


	@Test
	void testPropertyPrioritySystem() {
		Properties systemProperties = new Properties();
		systemProperties.setProperty( Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "systemProperty" );
		MavenProject mavenProject = new MavenProject();
		Properties userProperties = new Properties();

		when( session.getSystemProperties() ).thenReturn( systemProperties );
		when( session.getUserProperties() ).thenReturn( userProperties );
		when( session.getTopLevelProject() ).thenReturn( mavenProject );

		assertEquals( "systemProperty", MavenHelper.getExecutionProperty( sessionEndEvent, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "default" ) );
	}

	@Test
	void testPropertyPriorityDefault() {
		Properties systemProperties = new Properties();
		MavenProject mavenProject = new MavenProject();
		Properties userProperties = new Properties();

		when( session.getSystemProperties() ).thenReturn( systemProperties );
		when( session.getUserProperties() ).thenReturn( userProperties );
		when( session.getTopLevelProject() ).thenReturn( mavenProject );

		assertEquals( "default", MavenHelper.getExecutionProperty( sessionEndEvent, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "default" ) );
	}

}

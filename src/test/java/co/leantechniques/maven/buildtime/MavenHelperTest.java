/*
 * Copyright 2018 8D Technologies, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of 8D Technologies, Inc.
 * Use is subject to license terms.
 */

package co.leantechniques.maven.buildtime;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MavenHelperTest {

	@Mock
	private ExecutionEvent sessionEndEvent;

	@Mock
	private MavenSession session;

	@Before
	public void setUp() throws Exception {
		when( sessionEndEvent.getSession() ).thenReturn( session );
	}

	@Test
	public void testPropertyPriorityUser() throws Exception {
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
	public void testPropertyPriorityProject() throws Exception {
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
	public void testPropertyPrioritySystem() throws Exception {
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
	public void testPropertyPriorityDefault() throws Exception {
		Properties systemProperties = new Properties();
		MavenProject mavenProject = new MavenProject();
		Properties userProperties = new Properties();

		when( session.getSystemProperties() ).thenReturn( systemProperties );
		when( session.getUserProperties() ).thenReturn( userProperties );
		when( session.getTopLevelProject() ).thenReturn( mavenProject );

		assertEquals( "default", MavenHelper.getExecutionProperty( sessionEndEvent, Constants.BUILDTIME_OUTPUT_LOG_PROPERTY, "default" ) );
	}

}

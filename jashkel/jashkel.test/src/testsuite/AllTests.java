package testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses (
		{WaitForObjectsTest.class,
		 ThreadItInterfaceTest.class,
		 ThreadItTest.class})
public class AllTests 
{
} // class AllTests

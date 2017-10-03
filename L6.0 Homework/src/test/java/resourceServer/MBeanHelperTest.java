package resourceServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class MBeanHelperTest {
    @Before
    public void setUp() throws Exception {
    }


    @After
    public void tearDown() throws Exception {
        Thread.sleep(60 * 1000);
    }

    /**
     * Для проверки через jconsole
     *
     * Приконнектиться к com.intellij.rt.execution.junit.JUnitStarter
     *
     * Врямя на проверку ~60 секунд -- указано в методе tearDown().
     */
    @Test
    @Ignore
    public void registerMBean() throws Exception {
        MBeanHelper.registerMBean("data" + File.separator + "resource4parse.xml");
    }

    // низя вводить через консоль при тесте
//    private void waitForEnterPressed() {
//        try {
//            System.out.println("Press to continue...");
//            System.in.read();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
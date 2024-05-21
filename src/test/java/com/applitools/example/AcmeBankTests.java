import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.ClassicRunner;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class AcmeBankTests {
    private final static boolean USE_ULTRAFAST_GRID = false;
    private final static boolean USE_EXECUTION_CLOUD = false;

    private static String applitoolsApiKey;
    private static boolean headless;

    private static BatchInfo batch;
    private static Configuration config;
    private static EyesRunner runner;

    private WebDriver driver;
    private Eyes eyes;

    @BeforeAll
    public static void setUpConfigAndRunner() {
        applitoolsApiKey = System.getenv("APPLITOOLS_API_KEY");
        headless = Boolean.parseBoolean(System.getenv().getOrDefault("HEADLESS", "false"));

        if (USE_ULTRAFAST_GRID) {
            runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        }
        else {
            runner = new ClassicRunner();
        }

        String runnerName = (USE_ULTRAFAST_GRID) ? "Ultrafast Grid" : "Classic runner";
        batch = new BatchInfo("Trying to set Scroll Root Element ");

        config = new Configuration();
        config.setApiKey(applitoolsApiKey);
        config.setBatch(batch);

        if (USE_ULTRAFAST_GRID) {
            config.addBrowser(800, 600, BrowserType.CHROME);
            config.addBrowser(1600, 1200, BrowserType.FIREFOX);
            config.addBrowser(1024, 768, BrowserType.SAFARI);
            config.addDeviceEmulation(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT);
            config.addDeviceEmulation(DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE);
        }
    }

    @BeforeEach
    public void openBrowserAndEyes(TestInfo testInfo) throws MalformedURLException {

        ChromeOptions options = new ChromeOptions().setHeadless(headless);

        if (USE_EXECUTION_CLOUD) {
            driver = new RemoteWebDriver(new URL(Eyes.getExecutionCloudURL()), options);
        }
        else {
            driver = new ChromeDriver(options);
        }

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        eyes = new Eyes(runner);
        eyes.setConfiguration(config);

        eyes.open(
                driver,
                "IBX with scroll root element",
                "IBX with scroll root element"
        );

    }

    @Test
    public void ibxTest() {

        driver.get("C:\\Users\\Valan\\Applitools\\java-ibx\\src\\main\\resources\\MDXCSPortalWeb.html");


        /*
         * This is our workaround. It DOES work.
         */
//        ((JavascriptExecutor) driver).executeScript("        document.querySelector('div.container-wrap').style.height = '100%';\n"
//            + "        document.querySelector('div.navigation-bar-wrapper').style.height = '100%';\n"
//            + "        document.querySelector('html').style.height = 'auto';");



//        eyes.check(Target.window().fully());

        /*
         * This won't scroll the page.
         * It takes a region screenshot of the this element, but will not scroll.
         */
        eyes.check(Target.window().fully().scrollRootElement(By.id("scrollable-outlet")));
    }

    @AfterEach
    public void cleanUpTest() {
        eyes.closeAsync();
        driver.quit();
    }

    @AfterAll
    public static void printResults() {
        TestResultsSummary allTestResults = runner.getAllTestResults();
        System.out.println(allTestResults);
    }
}

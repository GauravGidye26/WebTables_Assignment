import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortTable {

    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest test;
    private WebDriverWait wait;

    @BeforeSuite
    public void setUp() {
        ExtentSparkReporter spark = new ExtentSparkReporter("HTML_Reports/SortTable.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);

        System.setProperty("webdriver.chrome.driver", "C:/Users/gauravg/Documents/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        test = extent.createTest("Sort Table Test").assignAuthor("Gaurav").assignCategory("Functional Test Case").assignDevice("Windows");
        test.info("Browser launched and maximized");

        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void sortTableTest() throws IOException {
        driver.get("https://letcode.in/table");
        test.info("Navigated to the table page");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)");
        test.info("Scrolled down to the table");

        String columnName = "Dessert (100g)";
        int columnIndex = 0;

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table[class*='mat-sort']")));
        test.info("Table located and ready for interaction");

        WebElement table = driver.findElement(By.cssSelector("table[class*='mat-sort']"));
        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        List<String> columnData = getColumnData(rows, columnIndex);

        test.info("Checking initial state (none)");
        verifySortingState(columnData, "none");

        test.addScreenCaptureFromPath(captureScreenshot(driver));

        WebElement columnHeader = driver.findElement(By.xpath("//th//div//div[.='" + columnName + "']"));
        columnHeader.click();
        test.info("Clicked on column header to sort in ascending order");

        waitForTableUpdate(table, columnIndex);
        List<WebElement> sortedRowsAsc = table.findElements(By.xpath(".//tr"));
        List<String> sortedColumnDataAsc = getColumnData(sortedRowsAsc, columnIndex);

        test.info("Checking ascending state");
        verifySortingState(sortedColumnDataAsc, "ascending");

        test.addScreenCaptureFromPath(captureScreenshot(driver));

        columnHeader.click();
        test.info("Clicked on column header to sort in descending order");

        waitForTableUpdate(table, columnIndex);
        List<WebElement> sortedRowsDesc = table.findElements(By.xpath(".//tr"));
        List<String> sortedColumnDataDesc = getColumnData(sortedRowsDesc, columnIndex);

        test.info("Checking descending state");
        verifySortingState(sortedColumnDataDesc, "descending");

        test.addScreenCaptureFromPath(captureScreenshot(driver));

        columnHeader.click();
        test.info("Clicked on column header to reset sorting");

        waitForTableUpdate(table, columnIndex);
        List<WebElement> resetRows = table.findElements(By.xpath(".//tr"));
        List<String> resetColumnData = getColumnData(resetRows, columnIndex);

        test.info("Checking reset (none) state");
        verifySortingState(resetColumnData, "none");

        test.addScreenCaptureFromPath(captureScreenshot(driver));
    }

    @AfterSuite
    public void tearDown() {
        driver.quit();
        test.info("Browser closed");

        extent.flush();
    }

    private List<String> getColumnData(List<WebElement> rows, int columnIndex) {
        List<String> columnData = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) { // Skip header row
            List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
            if (cells.size() > columnIndex) {
                String cellValue = cells.get(columnIndex).getText();
                columnData.add(cellValue);
            }
        }
        return columnData;
    }

    private void verifySortingState(List<String> data, String state) {
        switch (state) {
            case "ascending":
                Assert.assertTrue(isSortedAscending(data), "Column is NOT sorted in ascending order.");
                test.pass("Column is sorted in ascending order: " + data);
                break;
            case "descending":
                Assert.assertTrue(isSortedDescending(data), "Column is NOT sorted in descending order.");
                test.pass("Column is sorted in descending order: " + data);
                break;
            case "none":
                test.info("No sorting applied, raw data: " + data);
                break;
            default:
                test.warning("Invalid sorting state.");
                break;
        }
    }

    private boolean isSortedAscending(List<String> data) {
        List<String> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData);
        return data.equals(sortedData);
    }

    private boolean isSortedDescending(List<String> data) {
        List<String> sortedData = new ArrayList<>(data);
        sortedData.sort(Collections.reverseOrder());
        return data.equals(sortedData);
    }

    private void waitForTableUpdate(WebElement table, int columnIndex) {
        wait.until(driver -> {
            List<WebElement> rows = table.findElements(By.xpath(".//tr"));
            List<String> currentData = getColumnData(rows, columnIndex);
            return currentData.size() > 0;
        });
    }

    public static String captureScreenshot(WebDriver driver) throws IOException {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFilePath = new File("src/../Images/Screenshot" + System.currentTimeMillis() + ".png");
        String absolutePathLocation = destFilePath.getAbsolutePath();
        FileUtils.copyFile(srcFile, destFilePath);
        return absolutePathLocation;
    }
}


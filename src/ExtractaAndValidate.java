import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ExtractaAndValidate {

    WebDriver driver;
    ExtentReports extent;
    ExtentTest test;

    @BeforeClass
    public void setup() {
        extent = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter("HTML_Reports/ExtractAndValidate.html");
        extent.attachReporter(spark);
        test = extent.createTest("Extract and Validate Table Test").assignAuthor("Gaurav").assignCategory("Functional Test Case").assignDevice("Windows");

        System.setProperty("webdriver.chrome.driver", "C:/Users/gauravg/Documents/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        test.info("Browser launched and maximized");

        driver.get("https://letcode.in/table");
        test.info("Navigated to the table page");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)");
        test.info("Scrolled down to the table");
    }

    @Test
    public void validateTableRows() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table[class*='mat-sort']")));
        test.info("Table located");

        WebElement table = driver.findElement(By.cssSelector("table[class*='mat-sort']"));

        String columnName = "Protein (g)";
        String valueToMatch = "4";
        int columnIndex = getColumnIndex(table, columnName);
        test.info("Column index identified for '" + columnName + "'");

        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        List<WebElement> filteredRows = getRowsWithValue(rows, columnIndex, valueToMatch);

        Assert.assertFalse(filteredRows.isEmpty(), "No rows match the provided value.");
        test.pass("Rows with value '" + valueToMatch + "' are found.");
        try {
            test.addScreenCaptureFromPath(captureScreenshot(driver));
        } catch (IOException e) {
            test.fail("Failed to capture screenshot: " + e.getMessage());
        }

        List<String> headers = getTableHeaders(table);
        printTableHeader(headers);

        for (WebElement row : filteredRows) {
            printRow(row, headers);
        }
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
        test.info("Browser closed");
        extent.flush();
    }

    private int getColumnIndex(WebElement table, String columnName) {
        List<WebElement> headers = table.findElements(By.xpath(".//thead//th"));
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("Column with name '" + columnName + "' not found.");
    }

    private List<WebElement> getRowsWithValue(List<WebElement> rows, int columnIndex, String valueToMatch) {
        List<WebElement> matchingRows = new ArrayList<>();
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() > columnIndex) {
                String cellValue = cells.get(columnIndex).getText();
                if (cellValue.equals(valueToMatch) || isInteger(valueToMatch) && isInteger(cellValue) && Integer.parseInt(cellValue) == Integer.parseInt(valueToMatch)) {
                    matchingRows.add(row);
                }
            }
        }
        return matchingRows;
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private List<String> getTableHeaders(WebElement table) {
        List<WebElement> headerElements = table.findElements(By.xpath(".//thead//th"));
        List<String> headers = new ArrayList<>();
        for (WebElement headerElement : headerElements) {
            headers.add(headerElement.getText());
        }
        return headers;
    }

    private void printTableHeader(List<String> headers) {
        System.out.println("\nTable:");
        for (String header : headers) {
            System.out.printf("%-20s", header);
        }
        System.out.println();
        System.out.println(new String(new char[headers.size() * 20]).replace('\0', '-'));
    }

    private void printRow(WebElement row, List<String> headers) {
        List<WebElement> cells = row.findElements(By.tagName("td"));
        int numOfColumns = headers.size();
        for (int i = 0; i < numOfColumns; i++) {
            if (i < cells.size()) {
                System.out.printf("%-20s", cells.get(i).getText());
            } else {
                System.out.printf("%-20s", "");
            }
        }
        System.out.println();
    }

    public static String captureScreenshot(WebDriver driver) throws IOException {
        File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        File destFilePath = new File("src/../Images/Screenshot" + System.currentTimeMillis() + ".png");
        String absolutePathLocation = destFilePath.getAbsolutePath();
        FileUtils.copyFile(srcFile, destFilePath);
        return absolutePathLocation;
    }
}

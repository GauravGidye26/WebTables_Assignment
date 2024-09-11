import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortTable {

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:/Users/gauravg/Documents/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://letcode.in/table");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)");

        String columnName = "Dessert (100g)";
        int columnIndex = 0;

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table[class*='mat-sort']")));

        WebElement table = driver.findElement(By.cssSelector("table[class*='mat-sort']"));

        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        List<String> columnData = getColumnData(rows, columnIndex);

        System.out.println("Checking initial state (none):");
        verifySortingState(columnData, "none");

        WebElement columnHeader = driver.findElement(By.xpath("//th//div//div[.='" + columnName + "']"));
        columnHeader.click();

        waitForTableUpdate(wait, table, columnIndex);

        List<WebElement> sortedRowsAsc = table.findElements(By.xpath(".//tr"));
        List<String> sortedColumnDataAsc = getColumnData(sortedRowsAsc, columnIndex);
        System.out.println("Checking ascending state:");
        verifySortingState(sortedColumnDataAsc, "ascending");

        columnHeader.click();

        waitForTableUpdate(wait, table, columnIndex);

        List<WebElement> sortedRowsDesc = table.findElements(By.xpath(".//tr"));
        List<String> sortedColumnDataDesc = getColumnData(sortedRowsDesc, columnIndex);
        System.out.println("Checking descending state:");
        verifySortingState(sortedColumnDataDesc, "descending");

        columnHeader.click();

        waitForTableUpdate(wait, table, columnIndex);

        List<WebElement> resetRows = table.findElements(By.xpath(".//tr"));
        List<String> resetColumnData = getColumnData(resetRows, columnIndex);
        System.out.println("Checking reset (none) state:");
        verifySortingState(resetColumnData, "none");

        driver.quit();
    }

    private static List<String> getColumnData(List<WebElement> rows, int columnIndex) {
        List<String> columnData = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) { // Skip header row
            List<WebElement> cells = rows.get(i).findElements(By.tagName("td"));
            if (cells.size() > columnIndex) {
                String cellValue = cells.get(columnIndex).getText();
                columnData.add(cellValue);
            }
        }
        return columnData;
    }

    private static void verifySortingState(List<String> data, String state) {
        switch (state) {
            case "ascending":
                if (isSortedAscending(data)) {
                    System.out.println("Column is sorted in ascending order: " + data);
                } else {
                    System.out.println("Column is NOT sorted in ascending order.");
                }
                break;
            case "descending":
                if (isSortedDescending(data)) {
                    System.out.println("Column is sorted in descending order: " + data);
                } else {
                    System.out.println("Column is NOT sorted in descending order.");
                }
                break;
            case "none":
                System.out.println("No sorting applied, raw data: " + data);
                break;
            default:
                System.out.println("Invalid sorting state.");
                break;
        }
    }

    private static boolean isSortedAscending(List<String> data) {
        List<String> sortedData = new ArrayList<>(data);
        Collections.sort(sortedData);
        return data.equals(sortedData);
    }

    private static boolean isSortedDescending(List<String> data) {
        List<String> sortedData = new ArrayList<>(data);
        sortedData.sort(Collections.reverseOrder());
        return data.equals(sortedData);
    }

    private static void waitForTableUpdate(WebDriverWait wait, WebElement table, int columnIndex) {
        wait.until(driver -> {
            List<WebElement> rows = table.findElements(By.xpath(".//tr"));
            List<String> currentData = getColumnData(rows, columnIndex);
            return currentData.size() > 0; 
        });
    }
}

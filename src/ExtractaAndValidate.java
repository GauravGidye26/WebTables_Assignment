import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ExtractaAndValidate {

    public static void main(String[] args) {

        System.setProperty("webdriver.chrome.driver", "C:/Users/gauravg/Documents/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://letcode.in/table");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0,700)");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table[class*='mat-sort']")));

        WebElement table = driver.findElement(By.cssSelector("table[class*='mat-sort']"));

        String columnName = "Protein (g)";
        String valueToMatch = "4";  
        int columnIndex = getColumnIndex(table, columnName);
//        System.out.println(columnIndex);

        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        List<WebElement> filteredRows = getRowsWithValue(rows, columnIndex, valueToMatch);
        
        System.out.println("\nExtracted rows with value '" + valueToMatch + "':");

        List<String> headers = getTableHeaders(table);
        printTableHeader(headers);

        for (WebElement row : filteredRows) {
            printRow(row, headers);
        }

        driver.quit();
    }

    private static int getColumnIndex(WebElement table, String columnName) {
        List<WebElement> headers = table.findElements(By.xpath(".//thead//th"));
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).getText().equals(columnName)) {
                return i;
            }
        }
        throw new RuntimeException("Column with name '" + columnName + "' not found.");
    }

    private static List<WebElement> getRowsWithValue(List<WebElement> rows, int columnIndex, String valueToMatch) {
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

    private static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static List<String> getTableHeaders(WebElement table) {
        List<WebElement> headerElements = table.findElements(By.xpath(".//thead//th"));
        List<String> headers = new ArrayList<>();
        for (WebElement headerElement : headerElements) {
            headers.add(headerElement.getText());
        }
        return headers;
    }

    private static void printTableHeader(List<String> headers) {
        System.out.println("\nTable:");
        for (String header : headers) {
            System.out.printf("%-20s", header);
        }
        System.out.println();
        System.out.println(new String(new char[headers.size() * 20]).replace('\0', '-'));  
    }

    private static void printRow(WebElement row, List<String> headers) {
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
}

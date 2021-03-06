package com.odde.guess.acceptancetest.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public class SeleniumWebDriver implements UiDriver {

    private static final int DEFAULT_TIME_OUT_IN_SECONDS = 10;
    private final WebDriver webDriver = new ChromeDriver();

    @Override
    public void close() {
        webDriver.close();
    }

    @Override
    public void navigateTo(String url) {
        webDriver.get(url);
        webDriver.switchTo().window(webDriver.getWindowHandle());
    }

    @Override
    public void navigateToWithParams(String url, Params params) {
        webDriver.get(url + params.getQuery());
    }

    @Override
    public void waitForTextPresent(String text) {
        new WebDriverWait(webDriver, DEFAULT_TIME_OUT_IN_SECONDS).until(
                (ExpectedCondition<Boolean>) webDriver -> getAllTextInPage().contains(text));
    }

    @Override
    public void inputTextByName(String text, String name) {
        elementByName(name).sendKeys(text);
    }

    @Override
    public void clickByText(String text) {
        firstElementByText(text).click();
    }

    @Override
    public void selectOptionByTextAndElementName(String text, String elementName) {
        new Select(elementByName(elementName)).selectByVisibleText(text);
    }

    @Override
    public String getAllTextInPage() {
        return elementByTag().getText();
    }

    private WebElement elementByName(String name) {
        return webDriver.findElement(By.name(name));
    }

    private WebElement firstElementByText(String text) {
        return elementsByText(text)
                .findFirst().<NoSuchElementException>orElseThrow(() -> {
                    throw new NoSuchElementException(String.format("no element can be found by text: %s", text));
                });
    }

    private Stream<WebElement> elementsByText(String text) {
        return Stream.of(
                elementsByXPath(String.format("//input[@value='%s']", text)),
                elementsByXPath(String.format("//button[text()='%s']", text)),
                elementsByLinkText(text))
                .flatMap(Collection::stream);
    }

    private List<WebElement> elementsByLinkText(String text) {
        return webDriver.findElements(By.linkText(text));
    }

    private List<WebElement> elementsByXPath(String xpath) {
        return webDriver.findElements(By.xpath(xpath));
    }

    private WebElement elementByTag() {
        return webDriver.findElement(By.tagName("body"));
    }

}

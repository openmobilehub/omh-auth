/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package screens.authsampleapp;

import general.BaseScreen;
import general.ErrorsManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.Collections;

public class WebViewBrowserScreen extends BaseScreen {

    public WebViewBrowserScreen(AndroidDriver driver) {
        super(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    @Override
    public boolean verifyLoads() {return false;}


    /*
    UI ELEMENTS - Since they are in browser view, the interactions will be handled as web actions rather than mobile's
     */

    @AndroidFindBy(id="com.huawei.browser:id/close_button")
    private WebElement closeTabBtn;

    @AndroidFindBy(id="com.huawei.browser:id/url_text")
    private WebElement urlBar;

    @AndroidFindBy(xpath="//android.view.View[@content-desc=\"Neiger PK Drake neiger.drake@gmail.com\"]")
    private WebElement loggedAccount;


    /*
    METHODS
     */



    public boolean verifySignPageLoads() {
        return waitForMobElementToBeVisible(closeTabBtn)
                    && waitForMobElementToBeVisible(urlBar)
                    && printCustomTabElements();

    }

    private boolean printCustomTabElements() {
        boolean flag = false;
        try {
            System.out.println(getTextFromMobElement(urlBar));
            flag = true;
        }catch (Exception e) {ErrorsManager.errNExpManager(e);}
        return flag;
    }

    public boolean clickLoggedInAccountXY(int getX, int getY){
        return tapOnScreenXY(getX, getY);
    }

    public boolean clickTheXOnBrowser() {
        return tapMobElement(closeTabBtn);
    }
    /*
    RETURN-REDIRECT PAGE CALLS
     */

    public AuthSampleLoginScreen returnAsSignInState(int getX, int getY) {
        if(clickLoggedInAccountXY(getX, getY)) {
            return new AuthSampleLoginScreen(driver);
        } else {return null;}
    }

}

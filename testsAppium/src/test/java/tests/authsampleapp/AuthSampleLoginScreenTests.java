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

package tests.authsampleapp;

import general.MobileDriverManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import screens.authsampleapp.AuthSampleLoginScreen;
import screens.authsampleapp.WebViewBrowserScreen;

public class AuthSampleLoginScreenTests extends MobileDriverManager {

    private AuthSampleLoginScreen authSampleLoginScreen;

    @BeforeMethod
    public void setAuthSampleLoginScreen() {
        authSampleLoginScreen = new AuthSampleLoginScreen(getDriver());
        assertTrue(authSampleLoginScreen.verifyLoads(), basicErrorMsg("Unable to load the screen"));
        assertAll();
    }

    @Test
    public void FW_6_FW_7_FW_86_verifyThatAuthButtonIsDisplayed() {
        assertTrue(authSampleLoginScreen.verifySignInBtnDisplayed(), basicErrorMsg("Unable to get the button info"));
        assertAll();
    }


    @Test @Parameters({"deviceType"})
    public void FW_10_FW_11_verifyThatUserIsReturnedToSampleAppInLoggedInState(String deviceType) {
        if(deviceType.equals("nonGMS")) {
            WebViewBrowserScreen webViewBrowserScreen = authSampleLoginScreen.signInFromBrowser();
            assertTrue(webViewBrowserScreen.verifySignPageLoads(), basicErrorMsg("The signIn web view was not loaded correctly"));
            assertTrue(webViewBrowserScreen.clickLoggedInAccountXY(540,700), basicErrorMsg("Unable to click on the XY location given"));
            authSampleLoginScreen = webViewBrowserScreen.returnAsSignInState(800,2025);
        } else {
            assertTrue(authSampleLoginScreen.verifySignInPopUpShown(), basicErrorMsg("Unable to shown the pop up account"));
        }
        assertTrue(authSampleLoginScreen.verifySignInState(), basicErrorMsg("The signed in state fails the validation"));
        assertAll();
    }

    @Test
    public void FW_35_verifyThatUserDataIsRetrievedCorrectly() {
        assertTrue(authSampleLoginScreen.getUsersDataInformationPrint(),basicErrorMsg("The data from the signed in user fails to be retrieved"));
        assertAll();
    }

    @Test
    public void FW_84_FW_57_verifyThatUserCanRefreshToken() {
        assertTrue(authSampleLoginScreen.tapRefreshToken(), basicErrorMsg("Unable to tap on the RefreshToken button"));
        assertAll();
    }

    @Test
    public void FW_12_FW_58_verifyTheUserIsAbleToLogOutFromTheSession() {
        assertTrue(authSampleLoginScreen.signOutTheApp(), basicErrorMsg("Unable to tap on LOGOUT button"));
        assertAll();
    }

    @Test @Parameters({"deviceType"})
    public void FW_60_FW_63verifyThatUserCanTapXInBrowserOrTapOusideModal(String deviceType) {
        if(deviceType.equals("GMS")) {
            assertTrue(authSampleLoginScreen.tapOutsideModal(), basicErrorMsg("It can't be tapped outside"));
        } else {
            WebViewBrowserScreen webViewBrowserScreen = authSampleLoginScreen.signInFromBrowser();
            assertTrue(webViewBrowserScreen.verifySignPageLoads(), basicErrorMsg("The signIn web view was not loaded correctly"));
            assertTrue(webViewBrowserScreen.clickTheXOnBrowser(), basicErrorMsg("Unable to tap on the X close browser"));
            assertTrue(authSampleLoginScreen.checkAlerMsgPrint(), basicErrorMsg(""));
        }
        assertAll();
    }

    @Test  @Parameters({"deviceType"})
    public void FW_85_verifyThatUserCanRevokeToken(String deviceType) {
        if(deviceType.equals("nonGMS")) {
            WebViewBrowserScreen webViewBrowserScreen = authSampleLoginScreen.signInFromBrowser();
            assertTrue(webViewBrowserScreen.verifySignPageLoads(), basicErrorMsg("The signIn web view was not loaded correctly"));
            assertTrue(webViewBrowserScreen.clickLoggedInAccountXY(540,700), basicErrorMsg("Unable to click on the XY location given"));
            authSampleLoginScreen = webViewBrowserScreen.returnAsSignInState(800,2025);
        } else {
            assertTrue(authSampleLoginScreen.verifySignInPopUpShown(), basicErrorMsg("Unable to shown the pop up account"));
        }
        assertTrue(authSampleLoginScreen.verifySignInState(), basicErrorMsg("The signed in state fails the validation"));
        assertTrue(authSampleLoginScreen.tapRevokeToken(), basicErrorMsg("Unable to tap on the RevokeToken button"));
        assertAll();
    }

}

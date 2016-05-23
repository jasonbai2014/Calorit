package edu.uw.tacoma.team5.calorit;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

/**
 * This is a test class for login class
 *
 * Qing Bai
 * Levi Bingham
 * 2016/05/25
 */
public class LogInActivityTest extends ActivityInstrumentationTestCase2<LogInActivity> {

    private Solo mSolo;

    public LogInActivityTest() {
        super(LogInActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        mSolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getActivity().
                getString(R.string.login_prefs), Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();
        mSolo.finishOpenedActivities();

    }

    /**
     * This tests whether or not SignInFragment shows up
     */
    public void testSignInFragment() {
        boolean fragmentLoaded = mSolo.searchText("Enter your email");
        assertTrue("Sign in fragment loaded", fragmentLoaded);
    }

    /**
     * This tests whether or not SignUpFragment shows up when Sign Up button is clicked
     */
    public void testSignUpFragment() {
        mSolo.clickOnButton("Sign Up");
        boolean fragmentLoaded = mSolo.searchText("Create Account");
        assertTrue("Sign up fragment loaded", fragmentLoaded);
    }

    /**
     * This tests whether or not a user can sign in without using email account
     */
    public void testSignInWithoutEmail() {
        mSolo.enterText(1, "01234567890");
        mSolo.clickOnButton("Sign In");
        boolean toastShowed = mSolo.searchText("Please enter your email.");
        assertTrue("Didn't log in without an email", toastShowed);
    }

    /**
     * This tests whether or not a user can sign in without typing password
     */
    public void testSignInWithoutPassword() {
        mSolo.enterText(0, "test@gmail.com");
        mSolo.clickOnButton("Sign In");
        boolean toastShowed = mSolo.searchText("Please enter your password.");
        assertTrue("Didn't log in without a password", toastShowed);
    }

    /**
     * This tests whether or not a user can sign in with an invalid email
     */
    public void testSignInWithoutValidEmail() {
        mSolo.enterText(0, "testhelloworld1234567890");
        mSolo.enterText(1, "1234567890");
        mSolo.clickOnButton("Sign In");
        boolean toastShowed = mSolo.searchText("Please enter a valid email.");
        assertTrue("Didn't log in with an invalid email", toastShowed);
    }

    /**
     * This tests whether or not a user can sign  in with an incorrect password
     */
    public void testSignInWithoutCorrectPassword() {
        mSolo.enterText(0, "test@gmail.com");
        mSolo.enterText(1, "1234566789");
        mSolo.clickOnButton("Sign In");
        boolean toastShowed = mSolo.searchText("Failed! Incorrect password.");
        assertTrue("Didn't log in with an incorrect password", toastShowed);
    }

    /**
     * This tests whether or not a user can sign in with correct email and password
     */
    public void testSignIn() {
        mSolo.enterText(0, "test@gmail.com");
        mSolo.enterText(1, "1234567890");
        mSolo.clickOnButton("Sign In");
        boolean fragmentLoad = mSolo.searchText("Calories Left");
        assertTrue("Home fragment loaded", fragmentLoad);
    }

    /**
     * This tests whether or not a user can register an account that already exists
     */
    public void testSignUpWithExistAccount() {
        mSolo.clickOnButton("Sign Up");
        mSolo.enterText(0, "test@gmail.com");
        mSolo.enterText(1, "1234567891");
        mSolo.enterText(2, "1234567891");
        mSolo.clickOnButton("Create Account");
        boolean toastShowed = mSolo.searchText("Failed! That email address has already " +
                "been registered");
        assertTrue("Can't sign up with an exist account", toastShowed);
    }

    /**
     * This tests whether or not a user can sign up with an email longer than the requirement
     */
    public void testSignUpWithLongEmail() {
        mSolo.clickOnButton("Sign Up");
        mSolo.enterText(0, "helloworldTcss450@gmail.com");
        mSolo.enterText(1, "1234567890");
        mSolo.enterText(2, "1234567890");
        mSolo.clickOnButton("Create Account");
        boolean toastShowed = mSolo.searchText("Please enter a valid email (less than 20" +
                " characters");
        assertTrue("Can't sign up with a long email", toastShowed);
    }

    /**
     * This tests whether or not a user can sign up with an invalid email
     */
    public void testSignUpWithInvalidEmail() {
        mSolo.clickOnButton("Sign Up");
        mSolo.enterText(0, "helloworld");
        mSolo.enterText(1, "1234567890");
        mSolo.enterText(2, "1234567890");
        mSolo.clickOnButton("Create Account");
        boolean toastShowed = mSolo.searchText("Please enter a valid email.");
        assertTrue("Can't sign up with an invalid email", toastShowed);
    }

    /**
     * This tests whether or not a user can sign up when passwords don't match
     */
    public void testSignUpWithDifferentPasswords() {
        mSolo.clickOnButton("Sign Up");
        mSolo.enterText(0, "hello@gmail.com");
        mSolo.enterText(1, "1234567890");
        mSolo.enterText(2, "0987654321");
        mSolo.clickOnButton("Create Account");
        boolean toastShowed = mSolo.searchText("Please make sure you enter two same passwords");
        assertTrue("Can't sign up with matched passwords", toastShowed);
    }

    /**
     * This tests whether or not a user can sign up with valid email and password. But the email
     * needs to be changed whenever runs a new test.
     */
    public void testSignUp() {
        mSolo.clickOnButton("Sign Up");
        mSolo.enterText(0, "thistest3@gmail.com");
        mSolo.enterText(1, "1234567890");
        mSolo.enterText(2, "1234567890");
        mSolo.clickOnButton("Create Account");
        boolean fragmentLoaded = mSolo.searchText("Height in feet:");
        assertTrue("Successfully sign up", fragmentLoaded);
    }
}

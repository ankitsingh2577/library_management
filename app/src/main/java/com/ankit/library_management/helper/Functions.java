package com.ankit.library_management.helper;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

public class Functions {

    //Main URL
    private static String MAIN_URL = "https://ankitsingh2577.000webhostapp.com/Library_Management/";

    // Login URL
    public static String LOGIN_URL = MAIN_URL + "login.php";

    public static String BLOCK_STUDNETS_URL = MAIN_URL + "BlockStudents.php";

    public static String STUDNET_RETURN_BOOK_URL = MAIN_URL + "ReturnBook.php";

    public static String UNBLOCK_STUDNETS_URL = MAIN_URL + "UnblockStudents.php";

    public static String Add_BOOK_URL = MAIN_URL + "AddBook.php";

    public static String VIEW_UNBLOCK_STUDNETS_URL = MAIN_URL + "ViewUnblockStudents.php";

    public static String VIEW_BLOCK_STUDNETS_URL = MAIN_URL + "ViewBlockStudents.php";

    public static String VERIFY_USER_URL = MAIN_URL + "VerifyStudents.php";

    public static String Initiate_BOOK_URL = MAIN_URL + "BookBooking.php";

    public static String Cancel_Issue_BOOK_URL = MAIN_URL + "CancelBookIssue.php";

    public static String Cancel_Return_BOOK_URL = MAIN_URL + "CancelBookReturn.php";

    public static String Search_Pending_Issue_BOOK_URL = MAIN_URL + "SearchIssuePending.php";

    public static String Search_Pending_Return_BOOK_URL = MAIN_URL + "SearchReturnPending.php";

    public static String FETCH_BOOKS_URL = MAIN_URL + "SearchBooks.php";

    public static String LIBRARIAN_VIEW_ISSUED_BOOKS_URL = MAIN_URL + "LibrarianViewIssuedBooks.php";

    public static String STUDENT_VIEW_ISSUED_BOOKS_URL = MAIN_URL + "StudentViewIssuedBooks.php";

    public static String STUDENT_VIEW_RETURNED_BOOKS_URL = MAIN_URL + "StudentViewReturnedBooks.php";

    public static String RESET_PASS_URL = MAIN_URL + "reset-password.php";

    public static String Issue_Book_URL = MAIN_URL + "ApproveBookIssue.php";

    public static String Return_Book_URL = MAIN_URL + "ApproveBookReturn.php";


    public boolean logoutUser(Context context){
        DatabaseHandler db = new DatabaseHandler(context);
        db.resetTables();
        return true;
    }

    /**
     *  Email Address Validation
     */
    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     *  Hide Keyboard
     */
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}

package com.example.abhishek.financetracker.expensemanager.neopark.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.abhishek.financetracker.expensemanager.neopark.R;
import com.example.abhishek.financetracker.expensemanager.neopark.user.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private AppCompatButton mLoginButton;
    private LinearLayout mGoogleLoginButton;
    private EditText mUserId, mUserPassword;
    private FirebaseAuth mFirebaseAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;
    private static final String DATABASE_PATH = "users";
    private TextView mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Adjust insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeUI();
        configureGoogleSignIn();

        // Email/Password Login
        mLoginButton.setOnClickListener(v -> handleEmailPasswordLogin());

        // Google Login
        mGoogleLoginButton.setOnClickListener(v -> signInWithGoogle());

        // Navigate to Register Screen
        mRegisterButton.setOnClickListener(v -> buttonForRegister());
    }

    private void buttonForRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void initializeUI() {
        mLoginButton = findViewById(R.id.loginbutton);
        mGoogleLoginButton = findViewById(R.id.imgsLogin);
        mUserId = findViewById(R.id.userId);
        mUserPassword = findViewById(R.id.userPassword);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mRegisterButton = findViewById(R.id.registerbutton);
    }


    private void handleEmailPasswordLogin() {
        String emailAddress = mUserId.getText().toString();
        String password = mUserPassword.getText().toString();

        if (TextUtils.isEmpty(emailAddress)) {
            showError(mUserId, "Enter your registered email");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            showError(mUserId, "Enter a valid email address");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showError(mUserPassword, "Enter your password");
            return;
        }
        if (password.length() < 6) {
            showError(mUserPassword, "Password must be at least 6 characters");
            return;
        }

        mFirebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser userFirebase = mFirebaseAuth.getCurrentUser();
                saveUserData(userFirebase); // Save user data to Firebase
                navigateToHome();
            } else {
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signInWithGoogle() {
        //Intent: An Android object that represents an operation to be performed (here, launching a new activity).
        //signInIntent: The variable that will store the intent.
        //mGoogleSignInClient.getSignInIntent(): Calls the method on the sign-in client to get the intent that will launch the Google sign-in UI.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        //The method startActivityForResult launches the activity and waits for it to finish.
        //RC_SIGN_IN is a constant (typically an integer) that uniquely identifies this sign-in request.
        //    When the sign-in process completes, the result will be returned to the onActivityResult() method, where you can handle success or failure.
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Method Name: Describes that this method sets up (configures) Google Sign-In for the app.
    private void configureGoogleSignIn() {
        //GoogleSignInOptions: A class from the Google Sign-In API that holds setup(Builder) options.
        //(GoogleSignInOptions.DEFAULT_SIGN_IN): The constructor parameter,
        // a constant that tells the builder to use the default sign-in settings (e.g., basic profile information).
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //Method Call on Builder:
                //.requestIdToken(...): Instructs the builder to request an ID token during sign-in.
                //getString(R.string.default_web_client_id): Retrieves a string resource (your web client ID) from the app’s resources.
                //R.string.default_web_client_id: A reference to the string resource containing your web client ID.
                .requestIdToken(getString(R.string.default_web_client_id))
                //Method Call on Builder:
                //Adds a request for the user’s email address to be retrieved during sign-in.
                .requestEmail()
                .build();
        //GoogleSignIn.getClient(...): A static method that creates a GoogleSignInClient using the current context (this) and the options (gso).
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


//    The token ID is associated with a specific token (often found in an access token or an ID token).
//    Token Id is used to uniquely identify that particular token instance.

//    The client ID is a unique identifier assigned to your application when you register it with an identity provider
//    (like Google, Facebook, or any OAuth2 provider). It tells the provider which app is making the authentication or API request.




    /*
(int requestCode, int resultCode, Intent data):
These are the parameters:
requestCode: An integer identifying the request. This lets you know which activity is sending data back.
resultCode: An integer indicating the result of the activity (e.g., RESULT_OK or RESULT_CANCELED).
Intent data: The Intent object that carries the data from the completed activity.



Task<GoogleSignInAccount>:
This is a generic type representing an asynchronous operation that returns a GoogleSignInAccount when completed.
task:
The variable that will hold the asynchronous result.
GoogleSignIn.getSignedInAccountFromIntent(data):
A static method from the GoogleSignIn class that extracts a Task containing the
GoogleSignInAccount from the returned Intent data. This task will eventually hold the sign-in result.

task.getResult(Exception.class):
Retrieves the result from the task. If the task failed, it throws an exception of the type specified (in this case, a generic Exception).

account.getIdToken():
Retrieves the ID token from the GoogleSignInAccount. The ID token is used to prove the identity of the user to Firebase.
*/



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (Exception e) {
                Toast.makeText(this, "Google Sign-In Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    /*
GoogleAuthProvider.getCredential(idToken, null)
A static method from the GoogleAuthProvider class.
getCredential: This method creates an AuthCredential object.
idToken: The first argument is the Google ID token passed into the method.
null: The second argument is null because Google authentication does not require an access token in this context.


*/




    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mFirebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                FirebaseUser userFirebase = mFirebaseAuth.getCurrentUser();
                saveUserData(userFirebase); // Save user data to Firebase
                Toast.makeText(this, "Welcome " + (userFirebase != null ? userFirebase.getDisplayName() : ""), Toast.LENGTH_SHORT).show();
                navigateToHome();
            } else {
                Toast.makeText(this, "Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


        /*
        getReference("users")
Method Call: Gets a DatabaseReference that points to the node named "users" in your database.
"users" is a string literal specifying the database path.


Ternairy Operator
 fullName != null ? fullName : "Anonymous"

.child(uid)
Method Call:
child(: Accesses a child node of the current database reference.
uid: A variable (likely a unique identifier for the user) passed as a parameter.

.setValue(user)
Method Call:
setValue(: A method that writes data to the referenced location.
user: The User object we created.

*/
    private void saveUserData(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        String uid = firebaseUser.getUid();
        String fullName = firebaseUser.getDisplayName(); // Can be null for email login
        String email = firebaseUser.getEmail();
        String mobileNumber = firebaseUser.getPhoneNumber();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_PATH);

        User singleUser = new User(fullName != null ? fullName : "Anonymous", email, mobileNumber != null ? mobileNumber : "Not Registered");

        databaseReference.child(uid).setValue(singleUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User data saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

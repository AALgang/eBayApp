package m2.miage.ebay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseConnectActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_connect)
        createSignInIntent()
    }

    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.MicrosoftBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_Ebay)
            .setLogo(R.drawable.my_great_logo) // Set logo drawable
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {

        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser

            user?.let {
                // Name, email address
                val name = user.displayName
                val email = user.email

                if (email != null && name != null) {
                    addUser(name, email, user.uid)
                }
            }

            // Launch acticity
            startActivity(Intent(this, StartActivity::class.java))
            this.finish();
        } else {

        }
    }

    private fun addUser(mail:String, name:String, pseudo:String){
        var exist = true;
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore

        db.collection("users").whereEqualTo("email", mail)
            .get().addOnSuccessListener { document ->
                if(document.isEmpty){
                    exist = false;
                }
            }

        if(!exist) {
            // Create a new user with a first and last name
            val user = hashMapOf(
                "mail" to mail,
                "name" to name,
                "pseudo" to pseudo
            )

            // Add a new document with a generated ID
            db.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Log.d("Add", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("Add", "Error adding document", e)
                }
        }
    }

    private fun signOut() {

        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    private fun delete() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
    }
}
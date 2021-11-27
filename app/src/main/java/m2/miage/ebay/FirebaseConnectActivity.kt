package m2.miage.ebay

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseConnectActivity : AppCompatActivity() {

    companion object {
        fun signOut(context: Context) {
            AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener {
                    startActivity(context, Intent(context, FirebaseConnectActivity::class.java), null)

                }
        }
    }

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
                    addUser(email, name, user.uid)
                }
            }

            // Launch acticity
            startActivity(Intent(this, StartActivity::class.java))
            this.finish()
        } else {
        }
    }

    private fun addUser(mail:String, name:String, uid:String){
        var exist: Boolean = false
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore

        db.collection("users").whereEqualTo("mail", mail)
            .get().addOnSuccessListener { documents ->
                exist = !documents.isEmpty

                if(exist.not()) {
                    // Create a new user with a first and last name
                    val user = hashMapOf(
                        "mail" to mail,
                        "name" to name,
                        "pseudo" to uid
                    )

                    // Add a new document with a generated ID
                    db.collection("users").document(uid)
                        .set(user)
                        .addOnSuccessListener { Log.d("FBA", "DocumentSnapshot successfully written!") }
                        .addOnFailureListener { e -> Log.w("FBA", "Error writing document", e) }
                }
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
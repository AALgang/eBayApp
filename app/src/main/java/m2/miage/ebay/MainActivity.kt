package m2.miage.ebay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(FirebaseAuth.getInstance().currentUser == null) {
            startActivity(Intent(this, FirebaseConnectActivity::class.java))
            this.finish()
        } else {
            startActivity(Intent(this, StartActivity::class.java))
            this.finish()
        }
    }
}
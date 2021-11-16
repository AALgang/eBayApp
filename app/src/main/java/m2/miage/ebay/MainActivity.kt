package m2.miage.ebay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import m2.miage.ebay.home.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, HomeActivity::class.java))
    }
}
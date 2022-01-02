package m2.miage.ebay.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_profile.*
import m2.miage.ebay.FirebaseConnectActivity
import m2.miage.ebay.R
import android.os.Build
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_publish.*


class ProfileFragment : Fragment() {

    val PICK_IMAGE_REQUEST = 234
    var image_uri = ""
    val db = Firebase.firestore
    //val storage = Firebase.storage
    val storage = Firebase.storage("gs://miage-ebay.appspot.com")

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.
                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.
                    }
                    else -> {
                        // No location access granted.
                    }
                }
            }
            reload()
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    fun reload(){
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            Firebase.firestore.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if(document.data?.get("avatar_uri") != null && (document.data?.get("avatar_uri") as String).isNotEmpty()) {
                            Picasso.get().load(document.data!!["avatar_uri"] as String)
                                .placeholder(R.drawable.ic_person)
                                .error(R.drawable.my_great_logo)
                                .into(avatar)
                        } else {
                            avatar.setImageResource(R.drawable.my_great_logo)
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // I'M GETTING THE URI OF THE IMAGE AS DATA AND SETTING IT TO THE IMAGEVIEW
            Log.i("TEST",data?.data.toString())
            //uploadImageToFirebase(data?.data)
            //data?.data?.let { uploadImageToFirebase(it) }
            pushPicture(data)

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_disconnect.setOnClickListener {
            FirebaseConnectActivity.signOut(requireContext())
            activity?.finish()
        }

        btn_localiser.setOnClickListener {
            updateLocation()
        }

        avatar.setOnClickListener {
            openGalleryForImage()

            // Add a new document with a generated ID
            FirebaseAuth.getInstance().currentUser?.let {
                Firebase.firestore.collection("users").document(it.uid)
                    .update("avatar_uri", image_uri)
                    .addOnSuccessListener { Log.d("FB", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("FB", "Error updating document", e) }
            }
        }
        btn_change_avatar.setOnClickListener{
            openGalleryForImage()

            // Add a new document with a generated ID
            FirebaseAuth.getInstance().currentUser?.let {
                Firebase.firestore.collection("users").document(it.uid)
                    .update("avatar_uri", image_uri)
                    .addOnSuccessListener { Log.d("FB", "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w("FB", "Error updating document", e) }
            }
        }

        btn_updatePseudo.setOnClickListener {

            val builder: AlertDialog.Builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Nouveau pseudo")

// Set up the input
            val input = EditText(context)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setHint("Entrez un nouveau pseudonyme unique")
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)

// Set up the buttons
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                Firebase.firestore.collection("users").whereEqualTo("pseudo", input.text.toString()).get().addOnSuccessListener{ doc ->
                    if(doc.documents.size <= 0) {
                        // Add a new document with a generated ID
                        FirebaseAuth.getInstance().currentUser?.let {
                            Firebase.firestore.collection("users").document(it.uid)
                                .update("pseudo", input.text.toString())
                                .addOnSuccessListener {
                                    Log.d(
                                        "FB",
                                        "DocumentSnapshot successfully updated!"
                                    )
                                    Toast.makeText(context, "Pseudo mis à jour", Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener { e ->
                                    Log.w(
                                        "FB",
                                        "Error updating document",
                                        e
                                    )
                                }
                        }
                    } else {
                        Toast.makeText(context, "Pseudo déja utilisé", Toast.LENGTH_LONG).show()
                    }
                }

            })
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(requireContext(), "Vous devez autoriser l'accés a la position", Toast.LENGTH_LONG)
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Add a new document with a generated ID
                FirebaseAuth.getInstance().currentUser?.let {
                    if (location != null) {
                        var coordonnees = location.latitude.toString() + "," + location.longitude.toString()
                        Firebase.firestore.collection("users").document(it.uid)
                            .update("location", coordonnees)
                            .addOnSuccessListener { Log.d("FB", "DocumentSnapshot successfully updated!") }
                            .addOnFailureListener { e -> Log.w("FB", "Error updating document", e) }
                    }
                }
            }
            .addOnFailureListener{
                Toast.makeText(requireContext(), "Vous devez autoriser l'accés a la position", Toast.LENGTH_LONG)
            }

        reload()
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        //startActivityForResult(intent, 1000)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    fun pushPicture(data: Intent?) {
        val storageRef = storage.reference
        val selectedImageUri = data!!.data
        val imgageIdInStorage = selectedImageUri!!.lastPathSegment!! //here you can set whatever Id you need
        storageRef.child(imgageIdInStorage).putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                val urlTask = taskSnapshot.storage.downloadUrl
                Log.i("TEST", taskSnapshot.storage.downloadUrl.toString())
                urlTask.addOnSuccessListener { uri ->
                    Log.i("TEST", "POUET ça marche pour l'uri ${uri}")
                    image_uri = uri.toString()
                    // Add a new document with a generated ID
                    FirebaseAuth.getInstance().currentUser?.let {
                        Firebase.firestore.collection("users").document(it.uid)
                            .update("avatar_uri", image_uri)
                            .addOnSuccessListener {
                                Log.d(
                                    "FB",
                                    "DocumentSnapshot successfully updated!"
                                )
                                reload()
                            }
                            .addOnFailureListener { e -> Log.w("FB", "Error updating document", e) }
                    }
                }
                    .addOnFailureListener { e ->
                        // Handle unsuccessful upload
                        Toast.makeText(context, "Raté nullos ", Toast.LENGTH_LONG).show()
                        Log.i("TEST", e.toString())
                        Log.i("TEST", e.message.toString())

                    }
            }
    }
}
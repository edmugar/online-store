package com.robote.onlinestore

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.robote.onlinestore.databinding.ActivityOpcionesLoginsBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.robote.onlinestore.Opciones_login.Login_email

class OpcionesLogins : AppCompatActivity() {

    private lateinit var binding: ActivityOpcionesLoginsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var aGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesLoginsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        firebaseAuth = FirebaseAuth.getInstance()
        sessionCheck()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        aGoogleSignInClient = GoogleSignIn.getClient(this, gso)



        binding.buttonEmail.setOnClickListener {
            startActivity(Intent(this@OpcionesLogins, Login_email::class.java))
        }

        binding.buttonGoogleEmail.setOnClickListener {
            googleLogin()
        }
    }

    private fun googleLogin() {
        val googleSignInIntent = aGoogleSignInClient.signInIntent
        googleSingInARL.launch(googleSignInIntent)
    }

    private val googleSingInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                checkGoogle(account.idToken)
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                if (authResult.additionalUserInfo!!.isNewUser) {
                    callInfoDB()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    null,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun callInfoDB() {

        val time = Constants.getDeviceTime()
        val userEmail = firebaseAuth.currentUser!!.email
        val uIdUsuario = firebaseAuth.uid
        val userName = firebaseAuth.currentUser?.displayName

        val hashMap = HashMap<String, Any>()

        hashMap["nombres"] = "${userName}"
        hashMap["codigoTelefono"] = ""
        hashMap["Telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Google"
        hashMap["escribiendo"] = ""
        hashMap["tiempo"] = time
        hashMap["online"] = true
        hashMap["email"] = "${userEmail}"
        hashMap["uid"] = "${uIdUsuario}"
        hashMap["fecha_nac"] = ""

        var ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child(uIdUsuario!!)
            .setValue(hashMap)
            .addOnSuccessListener {

                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    this,
                    "No se registro debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }


    private fun sessionCheck() {
        if (firebaseAuth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}
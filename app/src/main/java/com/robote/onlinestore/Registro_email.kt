package com.robote.onlinestore

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.robote.onlinestore.databinding.ActivityRegistroEmailBinding

class Registro_email : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setTitle("Espere por favor")


        binding.btnSignUp.setOnClickListener {
            infoValidate()
        }
    }

    private var email = ""
    private var password = ""
    private var repeatPass = ""

    private fun infoValidate() {
        email = binding.inputEmailNewUser.text.toString().trim()
        password = binding.inputPassNewUser.text.toString().trim()
        repeatPass = binding.inputPassCheckNewUser.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.inputEmailNewUser.error = "Email invalido"
            binding.inputEmailNewUser.requestFocus()
        } else if (email.isEmpty()) {
            binding.inputEmailNewUser.error = "Ingrese email"
            binding.inputEmailNewUser.requestFocus()
        } else if (password.isEmpty()) {
            binding.inputPassNewUser.error = "Ingrese contraseñal"
            binding.inputPassNewUser.requestFocus()
        } else if (repeatPass.isEmpty()) {
            binding.inputPassCheckNewUser.error = "Repita la contraseña"
            binding.inputPassCheckNewUser.requestFocus()
        } else if (password != repeatPass) {
            binding.inputPassCheckNewUser.error = "No coinciden"
            binding.inputPassCheckNewUser.requestFocus()
        } else {
            signUpUser()
        }
    }

    private fun addInfoDB() {
        progressDialog.setMessage("Guardado información")

        val time = Constants.getDeviceTime()
        val userEmail = firebaseAuth.currentUser!!.email
        val uIdUsuario = firebaseAuth.uid

        var hashMap = HashMap<String, Any>()

        hashMap["nombres"] = ""
        hashMap["codigoTelefono"] = ""
        hashMap["Telefono"] = ""
        hashMap["urlImagenPerfil"] = ""
        hashMap["proveedor"] = "Email"
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
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se registro debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
    }

    private fun signUpUser() {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                addInfoDB()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se registró el usuario debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }
}
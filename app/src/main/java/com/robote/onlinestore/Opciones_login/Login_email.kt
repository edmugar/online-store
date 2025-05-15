package com.robote.onlinestore.Opciones_login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.robote.onlinestore.MainActivity
import com.robote.onlinestore.Registro_email
import com.robote.onlinestore.databinding.ActivityLoginEmailBinding

class Login_email : AppCompatActivity() {
    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebase: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebase = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.btnIngresar.setOnClickListener() {
            checkInfo()
        }

        binding.txtRegistry.setOnClickListener {
            startActivity(Intent(this@Login_email, Registro_email::class.java))
        }
    }

    private var email = ""
    private var pass = ""
    private fun checkInfo() {
        email = binding.textEmail.text.toString().trim()
        pass = binding.textPassword.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textEmail.error = "Email invalido"
            binding.textEmail.requestFocus()
        } else if (email.isEmpty()) {
            binding.textEmail.error = "Ingrese la email"
            binding.textEmail.requestFocus()
        } else if (pass.isEmpty()) {
            binding.textPassword.error = "Ingrse contraseña"
            binding.textEmail.requestFocus()
        } else {
            loginUser()
        }
    }

    private fun loginUser() {
        progressDialog.setTitle("Ingresando")
        progressDialog.show()

        firebase.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
                Toast.makeText(
                    this,
                    "Bienvenido",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "No se pudo iniciar sesión debido a ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
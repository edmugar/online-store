package com.robote.onlinestore.Fragments

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.robote.onlinestore.R
import com.robote.onlinestore.databinding.ActivityEditProfileBinding

class EditProfile : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        uploadInfo()

        binding.fabChangeImage.setOnClickListener {
            selectImage()
        }

        binding.btnUpdateProfile.setOnClickListener {
            validateInfo()
        }
    }

    private var names = ""
    private var bornDate = ""
    private var codePhone = ""
    private var phoneNumber = ""

    private fun validateInfo() {
        names = binding.txtInputNames.text.toString().trim()
        bornDate = binding.txtInputDateBorn.text.toString().trim()
        codePhone = binding.selectorCode.selectedCountryCode
        phoneNumber = binding.txtInputPhone.text.toString().trim()

        if (names.isEmpty()) {
            Toast.makeText(this, "Ingrese su nombre", Toast.LENGTH_SHORT).show()
        } else if (bornDate.isEmpty()) {
            Toast.makeText(this, "Ingrese su fecha de nacimiento", Toast.LENGTH_SHORT).show()
        } else if (codePhone.isEmpty()) {
            Toast.makeText(this, "Seleccione un código", Toast.LENGTH_SHORT).show()
        } else if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Ingrese un telefono", Toast.LENGTH_SHORT).show()
        } else {
            updateInfo()
        }
    }

    private fun updateInfo() {
        progressDialog.setMessage("Actualizando información")
        ProgressBar(this)

        val hastMap: HashMap<String, Any> = HashMap()

        hastMap["nombres"] = names
        hastMap["fecha_nac"] = bornDate
        hastMap["codigoTelefono"] = codePhone
        hastMap["Telefono"] = phoneNumber

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hastMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    this,
                    "Se actualizó su información",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                )

            }

    }


    private fun uploadImage() {
        progressDialog.setMessage("Subiendo imagen")
        progressDialog.show()

        val ruteImage = "profileImages/" + firebaseAuth.uid
        val ref = FirebaseStorage.getInstance().getReference(ruteImage)
        ref.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapShot ->
                val uriTask = taskSnapShot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val urlUploadedImage = uriTask.result.toString()
                if (uriTask.isSuccessful) {
                    updateDBImage(urlUploadedImage)
                }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext, "${e.message}}", Toast.LENGTH_SHORT

                ).show()

            }
    }

    private fun updateDBImage(urlUploadedImage: String) {
        progressDialog.setMessage("Actualizando imagen")
        progressDialog.show()

        val hashMap: HashMap<String, Any> = HashMap()

        if (imageUri != null) {
            hashMap["urlImagenPerfil"] = urlUploadedImage
        }

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Se ha actualizado la imagem",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    }

    private fun uploadInfo() {
        val ref = FirebaseDatabase.getInstance().getReference("Users")

        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = "${snapshot.child("nombres").value}"
                    val image = "${snapshot.child("urlImagenPerfil").value}"
                    val bornDate = "${snapshot.child("fecha_nac").value}"
                    val phoneNumber = "${snapshot.child("Telefono").value}"
                    val codePhone = "${snapshot.child("codigoTelefono").value}"

                    //Setters
                    binding.txtInputNames.setText(name)
                    binding.txtInputDateBorn.setText(bornDate)
                    binding.txtInputPhone.setText(phoneNumber)

                    try {
                        Glide.with(applicationContext)
                            .load(image)
                            .placeholder(R.drawable.ic_profile_user)
                            .into(binding.imageProfile)
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@EditProfile,
                            "${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    try {
                        val code = codePhone.replace("+", "").toInt()
                        binding.selectorCode.setCountryForPhoneCode(code)
                    } catch (e: Exception) {
//                        Toast.makeText(
//                            this@EditProfile,
//                            "${e.message}",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            });
    }

    private fun selectImage() {
        val popupMenu = PopupMenu(this, binding.fabChangeImage)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    accessCamera.launch(arrayOf(Manifest.permission.CAMERA))
                } else {
                    accessCamera.launch(
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }
            } else if (itemId == 2) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imageGallery()
                } else {
                    allowPersonalStorage.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    private val accessCamera =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            var allowAll = true
            for (allowP in result.values) {
                allowAll = allowAll && allowP
            }

            if (allowAll) {
                cameraImage()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de cámara o galería fue denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun cameraImage() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Image_title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Image_Description")
        imageUri =
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        cameraResultARL.launch(intent)
    }

    private val cameraResultARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                uploadImage()
//                try{
//                    Glide.with(this)
//                        .load(imageUri)
//                        .placeholder(R.drawable.ic_profile_user)
//                        .into(binding.imageProfile)
//                }
//                catch(e:Exception){
//                }
            } else {
                Toast.makeText(
                    null,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private val allowPersonalStorage =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { allowed ->
            if (allowed) {
                imageGallery()
            } else {
                Toast.makeText(
                    this,
                    "El permiso de almacenamiento ha sido denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private fun imageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultGalleryARL.launch(intent)
    }

    private val resultGalleryARL =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data!!.data

                uploadImage()
//                try{
//                    Glide.with(this)
//                        .load(imageUri)
//                        .placeholder(R.drawable.ic_profile_user)
//                        .into(binding.imageProfile)
//                }
//                catch(e:Exception){
//                }
            } else {
                Toast.makeText(
                    null,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}



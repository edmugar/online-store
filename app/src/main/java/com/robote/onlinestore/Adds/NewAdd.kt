package com.robote.onlinestore.Adds

import android.Manifest
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.robote.onlinestore.Constants
import com.robote.onlinestore.Model.ModelSelectedImage
import com.robote.onlinestore.R
import com.robote.onlinestore.adapters.AdapterSelectedImage
import com.robote.onlinestore.databinding.ActivityNewAddBinding

class NewAdd : AppCompatActivity() {
    private lateinit var binding: ActivityNewAddBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private var imageUri: Uri? = null
    private lateinit var modelSelectedImages: ArrayList<ModelSelectedImage>
    private lateinit var adapterSelectedImage: AdapterSelectedImage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere")
        progressDialog.setCanceledOnTouchOutside(false)

        val adapterCategory = ArrayAdapter(this, R.layout.item_category, Constants.categories)
        binding.etCategoryFragment.setAdapter(adapterCategory)

        val adapterCondition = ArrayAdapter(this, R.layout.item_condition, Constants.condition)
        binding.etConditionFragment.setAdapter(adapterCondition)


        modelSelectedImages = ArrayList()
        chargeImages()

        binding.addImage.setOnClickListener {
            showOptions()
        }
        binding.btnNewPublication.setOnClickListener {
            checkData()
        }
    }

    private var marca = ""
    private var category = ""
    private var condition = ""
    private var address = ""
    private var price = ""
    private var title = ""
    private var description = ""
    private var latitude = 0.0
    private var longitude = 0.0

    private fun checkData() {
        marca = binding.etMarcaFragment.text.toString().trim()
        category = binding.etCategoryFragment.text.toString().trim()
        condition = binding.etConditionFragment.text.toString().trim()
        price = binding.etPriceFragment.text.toString().trim()
        title = binding.etTitleFragment.text.toString().trim()
        description = binding.etDescriptionFragment.text.toString().trim()
        address = binding.etLocationFragment.text.toString().trim()

        if (marca.isEmpty()) {
            binding.etMarcaFragment.error = "Ingrese una marca"
            binding.etMarcaFragment.requestFocus()
        } else if (category.isEmpty()) {
            binding.etCategoryFragment.error = "Ingrese una categoría"
            binding.etCategoryFragment.requestFocus()
        } else if (condition.isEmpty()) {
            binding.etConditionFragment.error = "Ingrese una condición"
            binding.etConditionFragment.requestFocus()
        } else if (price.isEmpty()) {
            binding.etPriceFragment.error = "Ingrese un precio"
            binding.etPriceFragment.requestFocus()
        } else if (title.isEmpty()) {
            binding.etTitleFragment.error = "Ingrese un precio"
            binding.etTitleFragment.requestFocus()
        } else if (description.isEmpty()) {
            binding.etDescriptionFragment.error = "Ingrese un descripción"
            binding.etDescriptionFragment.requestFocus()
        } else if (imageUri == null) {
            Toast.makeText(
                this,
                "Agregue al menos una imagen",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            addPublication()
        }

    }

    private fun addPublication() {
        progressDialog.setMessage("Agregando anuncio")
        progressDialog.show()

        val time = Constants.getDeviceTime()
        val ref = FirebaseDatabase.getInstance().getReference("Products")
        val keyId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keyId}"
        hashMap["uid"] = "${firebaseAuth.uid}"
        hashMap["marca"] = "${marca}"
        hashMap["category"] = "${category}"
        hashMap["condition"] = "${condition}"
        hashMap["address"] = "${address}"
        hashMap["price"] = "${price}"
        hashMap["title"] = "${title}"
        hashMap["description"] = "${description}"
        hashMap["state"] = "${Constants.onSale}"
        hashMap["time"] = "${time}"
        hashMap["latitude"] = "${latitude}"
        hashMap["longitude"] = "${longitude}"

        ref.child(keyId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                chargeImagesToStorage(keyId)
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun chargeImagesToStorage(keyId: String) {
        for (i in modelSelectedImages.indices) {
            val modelImageSelect = modelSelectedImages[i]
            val nameImage = modelImageSelect.id

            val pathNameImage = "Anuncios/$nameImage"

            val storageReference = FirebaseStorage.getInstance().getReference(pathNameImage)
            storageReference.putFile(modelImageSelect.imageUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImageUploaded = uriTask.result

                    if (uriTask.isSuccessful) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = "{${modelImageSelect.imageUri}}"
                        hashMap["urlImage"] = "${urlImageUploaded}"

                        val ref = FirebaseDatabase.getInstance().getReference("Products")
                        ref.child(keyId).child("Images")
                            .child(nameImage)
                            .updateChildren(hashMap)

                    }
                    progressDialog.dismiss()
                    onBackPressedDispatcher.onBackPressed()
                    Toast.makeText(
                        this,
                        "Se publicó su producto",
                        Toast.LENGTH_SHORT
                    ).show()

                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun showOptions() {
        val popupMenu = PopupMenu(this, binding.addImage)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Cámara")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galería")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val itemId = item.itemId
            if (itemId == 1) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    cameraPermission.launch(arrayOf(Manifest.permission.CAMERA))
                } else {
                    cameraPermission.launch(
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
                    accessPermissionGallery.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            true
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
                val time = "${Constants.getDeviceTime()}"
                val modelSelectedImage = ModelSelectedImage(
                    time, imageUri, null, false
                )
                modelSelectedImages.add(modelSelectedImage)
                chargeImages()
            } else {
                Toast.makeText(
                    null,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private val accessPermissionGallery = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAllowed ->
        if (isAllowed) {
            imageGallery()
        } else {
            Toast.makeText(
                this,
                "El permiso de almacenamiento ha sido denegado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        var allAllowed = true
        for (isAllowed in result.values) {
            allAllowed = allAllowed && isAllowed
        }
        if (allAllowed) {
            cameraImage()
        } else {
            Toast.makeText(
                this,
                "El permiso fue denegado",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun chargeImages() {
        adapterSelectedImage = AdapterSelectedImage(this, modelSelectedImages)
        binding.rvImages.adapter = adapterSelectedImage
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

                val time = "${Constants.getDeviceTime()}"
                val modelImageSelected = ModelSelectedImage(
                    time, imageUri, null, false
                )
                modelSelectedImages.add(modelImageSelected)
                chargeImages()

            } else {
                Toast.makeText(
                    null,
                    "Cancelado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
}
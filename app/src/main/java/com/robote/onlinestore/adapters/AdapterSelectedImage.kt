package com.robote.onlinestore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.robote.onlinestore.Model.ModelSelectedImage
import com.robote.onlinestore.R
import com.robote.onlinestore.databinding.ItemImagesSelectedBinding

class AdapterSelectedImage(
    private val context: Context,
    private val imageSelectedImageArrayList: ArrayList<ModelSelectedImage>
) :
    Adapter<AdapterSelectedImage.HolderSelectedImage>() {
    private lateinit var binding: ItemImagesSelectedBinding

    inner class HolderSelectedImage(itemView: View) : ViewHolder(itemView) {
        var itemImage = binding.itemImage
        var btnClose = binding.itemClose
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSelectedImage {
        binding = ItemImagesSelectedBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderSelectedImage(binding.root)
    }

    override fun getItemCount(): Int {
        return imageSelectedImageArrayList.size
    }

    override fun onBindViewHolder(holder: HolderSelectedImage, position: Int) {
        val model = imageSelectedImageArrayList[position]

        val imageUri = model.imageUri

        try {
            Glide.with(context)
                .load(imageUri)
                .placeholder(R.drawable.ic_profile_user)
                .into(holder.itemImage)
        } catch (e: Exception) {

        }

        holder.btnClose.setOnClickListener {
            imageSelectedImageArrayList.remove(model)
            notifyDataSetChanged()
        }
    }

}
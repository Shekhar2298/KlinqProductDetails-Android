package com.klinq.productdetails.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import com.klinq.productdetails.R

class ProductImageAdapter(
    private var images: List<String>,
) : RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {
    private var cachedImageReference: String? = null

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.productImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.image.load(ImageUrlOptimizer.cachedVariantUrl(images[position], cachedImageReference)) {
            crossfade(true)
            memoryCachePolicy(CachePolicy.ENABLED)
            diskCachePolicy(CachePolicy.ENABLED)
            placeholder(R.drawable.bg_image_placeholder)
            error(R.drawable.bg_image_placeholder)
        }
    }

    override fun getItemCount(): Int = images.size

    fun submitImages(newImages: List<String>, cachedReference: String? = cachedImageReference) {
        images = newImages
        cachedImageReference = cachedReference
        notifyDataSetChanged()
    }
}
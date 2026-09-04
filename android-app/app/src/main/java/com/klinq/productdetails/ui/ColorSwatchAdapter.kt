package com.klinq.productdetails.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import com.klinq.productdetails.R
import com.klinq.productdetails.data.ProductOption

class ColorSwatchAdapter(
    private var options: List<ProductOption>,
    private val onOptionSelected: (ProductOption) -> Unit,
) : RecyclerView.Adapter<ColorSwatchAdapter.SwatchViewHolder>() {

    private var selectedOptionId: String? = null

    class SwatchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ShapeableImageView = view.findViewById(R.id.swatchImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwatchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_color_swatch, parent, false)
        return SwatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SwatchViewHolder, position: Int) {
        val option = options[position]
        val imageUrl = ImageUrlOptimizer.cachedVariantUrl(
            option.swatchUrl.ifBlank { option.images.firstOrNull().orEmpty() },
            cachedImageReference,
        )
        val swatchSize = holder.image.resources.getDimensionPixelSize(R.dimen.swatch_size)
        holder.image.load(imageUrl) {
            crossfade(true)
            size(swatchSize, swatchSize)
            placeholder(R.drawable.bg_image_placeholder)
            error(R.drawable.bg_image_placeholder)
        }
        holder.image.contentDescription =
            holder.itemView.context.getString(R.string.select_color_accessibility, option.value)
        holder.itemView.setOnClickListener {
            selectedOptionId = option.optionId
            notifyItemRangeChanged(0, options.size)
            onOptionSelected(option)
        }
    }

    override fun getItemCount(): Int = options.size

    fun submitOptions(
        newOptions: List<ProductOption>,
        selectedId: String?,
        cachedReference: String? = null,
    ) {
        options = newOptions
        selectedOptionId = selectedId
        cachedImageReference = cachedReference
        notifyDataSetChanged()
    }

    private var cachedImageReference: String? = null
}
package com.klinq.productdetails.data

import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val status: Int? = null,
    val message: String? = null,
    val data: Product? = null,
)

data class Product(
    val id: String = "",
    val sku: String = "",
    val name: String = "",
    @SerializedName("brand_name")
    val brandName: String = "",
    val price: String = "",
    @SerializedName("final_price")
    val finalPrice: String = "",
    @SerializedName("is_salable")
    val isSalable: Boolean = false,
    @SerializedName("remaining_qty")
    val remainingQty: Int = 0,
    val image: String = "",
    val images: List<String> = emptyList(),
    val description: String = "",
    @SerializedName("web_url")
    val webUrl: String = "",
    @SerializedName("configurable_option")
    val configurableOptions: List<ProductOptionGroup> = emptyList(),
)

data class ProductOptionGroup(
    val attributeId: Int? = null,
    val type: String? = null,
    @SerializedName("attribute_code")
    val attributeCode: String? = null,
    val attributes: List<ProductOption> = emptyList(),
)

data class ProductOption(
    val value: String = "",
    @SerializedName("option_id")
    val optionId: String = "",
    val price: String = "",
    val images: List<String> = emptyList(),
    @SerializedName("color_code")
    val colorCode: String? = null,
    @SerializedName("swatch_url")
    val swatchUrl: String = "",
)
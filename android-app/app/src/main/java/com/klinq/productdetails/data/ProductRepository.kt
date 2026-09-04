package com.klinq.productdetails.data

class ProductRepository(
    private val api: ProductApi = RetrofitClient.productApi,
) {
    suspend fun getProduct(): Product {
        return api.getProductDetails(
            productId = "6701",
            variantId = "253620",
        ).data ?: error("The product response did not include product data.")
    }
}
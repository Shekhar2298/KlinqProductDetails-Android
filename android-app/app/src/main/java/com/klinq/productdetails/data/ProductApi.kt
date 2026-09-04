package com.klinq.productdetails.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("rest/V1/productdetails/{productId}/{variantId}")
    suspend fun getProductDetails(
        @Path("productId") productId: String,
        @Path("variantId") variantId: String,
        @Query("lang") language: String = "en",
        @Query("store") store: String = "KWD",
    ): ProductResponse
}
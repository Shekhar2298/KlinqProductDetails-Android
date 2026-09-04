package com.klinq.productdetails.ui

/**
 * The product endpoint returns full-size variant images for color options.
 * The same Magento media files are also available from the cache path used
 * by the product's primary image, which is much smaller on the device.
 */
object ImageUrlOptimizer {
    private const val PRODUCT_MEDIA_MARKER = "/media/catalog/product/"
    private const val CACHE_MARKER = "/media/catalog/product/cache/"

    fun cachedVariantUrl(url: String, cachedReference: String?): String {
        if (url.isBlank() || url.contains(CACHE_MARKER)) return url

        val reference = cachedReference.orEmpty()
        val cacheStart = reference.indexOf(CACHE_MARKER)
        if (cacheStart < 0) return url

        val cachePath = reference.substring(cacheStart + CACHE_MARKER.length)
        val cacheId = cachePath.substringBefore('/').takeIf { it.isNotBlank() } ?: return url
        val mediaStart = url.indexOf(PRODUCT_MEDIA_MARKER)
        if (mediaStart < 0) return url

        return url.substring(0, mediaStart) +
            "/media/catalog/product/cache/$cacheId/" +
            url.substring(mediaStart + PRODUCT_MEDIA_MARKER.length)
    }
}
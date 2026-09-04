package com.klinq.productdetails.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.klinq.productdetails.R
import com.klinq.productdetails.data.Product
import com.klinq.productdetails.data.ProductOption
import com.klinq.productdetails.data.ProductRepository
import kotlinx.coroutines.launch
import java.util.Locale

class ProductDetailsActivity : AppCompatActivity() {
    private val viewModel: ProductDetailsViewModel by viewModels {
        ProductDetailsViewModelFactory(ProductRepository())
    }

    private lateinit var contentScroll: View
    private lateinit var loadingState: View
    private lateinit var errorState: View
    private lateinit var viewPager: ViewPager2
    private lateinit var galleryDots: LinearLayout
    private lateinit var headerTitle: TextView
    private lateinit var brandName: TextView
    private lateinit var productName: TextView
    private lateinit var price: TextView
    private lateinit var sku: TextView
    private lateinit var installmentSubtitle: TextView
    private lateinit var selectedColor: TextView
    private lateinit var informationContent: TextView
    private lateinit var informationChevron: ImageButton
    private lateinit var swatches: androidx.recyclerview.widget.RecyclerView
    private lateinit var quantityValue: TextView
    private lateinit var addToBag: MaterialButton
    private lateinit var favoriteButton: ImageButton

    private val imageAdapter = ProductImageAdapter(emptyList())
    private lateinit var colorAdapter: ColorSwatchAdapter
    private var currentProduct: Product? = null
    private var selectedOption: ProductOption? = null
    private var quantity = 1
    private var isFavorite = false
    private var isInformationExpanded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        window.statusBarColor = getColor(R.color.white)
        window.navigationBarColor = getColor(R.color.ink)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        bindViews()
        configureGallery()
        configureColors()
        configureActions()
        observeProduct()
    }

    private fun bindViews() {
        contentScroll = findViewById(R.id.contentScroll)
        loadingState = findViewById(R.id.loadingState)
        errorState = findViewById(R.id.errorState)
        viewPager = findViewById(R.id.productImagePager)
        galleryDots = findViewById(R.id.galleryDots)
        headerTitle = findViewById(R.id.headerTitle)
        brandName = findViewById(R.id.brandName)
        productName = findViewById(R.id.productName)
        price = findViewById(R.id.productPrice)
        sku = findViewById(R.id.productSku)
        installmentSubtitle = findViewById(R.id.installmentSubtitle)
        selectedColor = findViewById(R.id.selectedColor)
        informationContent = findViewById(R.id.informationContent)
        informationChevron = findViewById(R.id.informationChevron)
        swatches = findViewById(R.id.colorSwatches)
        quantityValue = findViewById(R.id.quantityValue)
        addToBag = findViewById(R.id.addToBagButton)
        favoriteButton = findViewById(R.id.favoriteButton)
    }

    private fun configureGallery() {
        viewPager.adapter = imageAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
            }
        })
    }

    private fun configureColors() {
        colorAdapter = ColorSwatchAdapter(emptyList()) { option ->
            selectColor(option)
        }
        swatches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        swatches.adapter = colorAdapter
    }

    private fun configureActions() {
        findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.shareButton).setOnClickListener { shareProduct() }
        findViewById<ImageButton>(R.id.bagButton).setOnClickListener {
            Toast.makeText(this, R.string.bag_ready, Toast.LENGTH_SHORT).show()
        }
        favoriteButton.setOnClickListener {
            isFavorite = !isFavorite
            favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border,
            )
        }
        addToBag.setOnClickListener {
            Toast.makeText(this, R.string.added_to_bag, Toast.LENGTH_SHORT).show()
            addToBag.setText(R.string.added_to_bag)
            addToBag.setIconResource(R.drawable.ic_check)
        }
        findViewById<MaterialButton>(R.id.shareBottomButton).setOnClickListener {
            shareProduct()
        }
        findViewById<ImageButton>(R.id.decreaseQuantity).setOnClickListener {
            quantity = (quantity - 1).coerceAtLeast(1)
            updateQuantity()
        }
        findViewById<ImageButton>(R.id.increaseQuantity).setOnClickListener {
            quantity += 1
            updateQuantity()
        }
        findViewById<View>(R.id.informationHeader).setOnClickListener {
            isInformationExpanded = !isInformationExpanded
            informationContent.isVisible = isInformationExpanded
            informationChevron.setImageResource(
                if (isInformationExpanded) R.drawable.ic_chevron_up
                else R.drawable.ic_chevron_down,
            )
        }
        findViewById<View>(R.id.retryButton).setOnClickListener { viewModel.retry() }
    }

    private fun observeProduct() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        ProductUiState.Loading -> showLoading()
                        is ProductUiState.Success -> showProduct(state.product)
                        is ProductUiState.Error -> showError()
                    }
                }
            }
        }
    }

    private fun showLoading() {
        loadingState.isVisible = true
        errorState.isVisible = false
        contentScroll.isVisible = false
    }

    private fun showError() {
        loadingState.isVisible = false
        errorState.isVisible = true
        contentScroll.isVisible = false
    }

    private fun showProduct(product: Product) {
        currentProduct = product
        loadingState.isVisible = false
        errorState.isVisible = false
        contentScroll.isVisible = true

        val options = product.configurableOptions.firstOrNull()?.attributes.orEmpty()
        selectedOption = options.firstOrNull { option ->
            option.images.firstOrNull() == product.image
        } ?: options.firstOrNull()

        renderText(product)
        renderGallery(product.images.ifEmpty {
            selectedOption?.images.orEmpty()
        }.ifEmpty {
            listOf(product.image)
        }, product.image)
        colorAdapter.submitOptions(options, selectedOption?.optionId, product.image)
    }

    private fun renderText(product: Product) {
        brandName.text = product.brandName.uppercase(Locale.getDefault())
        headerTitle.text = product.name
        productName.text = product.name
        price.text = getString(
            R.string.price_format,
            formatPrice(selectedOption?.price ?: product.finalPrice),
        )
        sku.text = getString(R.string.sku_format, product.sku)
        installmentSubtitle.text = HtmlCompat.fromHtml(
            getString(R.string.installment_subtitle),
            HtmlCompat.FROM_HTML_MODE_LEGACY,
        )
        selectedColor.text = selectedOption?.value
            ?: getString(R.string.select_color)
        informationContent.text = HtmlCompat.fromHtml(
            product.description,
            HtmlCompat.FROM_HTML_MODE_LEGACY,
        )
    }

    private fun renderGallery(images: List<String>, cachedReference: String? = currentProduct?.image) {
        imageAdapter.submitImages(images, cachedReference)
        renderDots(images.size)
        viewPager.setCurrentItem(0, false)
    }

    private fun renderDots(count: Int) {
        galleryDots.removeAllViews()
        repeat(count.coerceAtMost(20)) { index ->
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(
                        if (index == 0) R.dimen.gallery_dot_active else R.dimen.gallery_dot,
                    ),
                    resources.getDimensionPixelSize(R.dimen.gallery_dot),
                ).also { params ->
                    params.marginStart = resources.getDimensionPixelSize(R.dimen.gallery_dot_gap)
                    params.marginEnd = resources.getDimensionPixelSize(R.dimen.gallery_dot_gap)
                }
                background = getDrawable(
                    if (index == 0) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive,
                )
            }
            galleryDots.addView(dot)
        }
    }

    private fun updateDots(selectedIndex: Int) {
        for (index in 0 until galleryDots.childCount) {
            val dot = galleryDots.getChildAt(index)
            val active = index == selectedIndex
            val params = dot.layoutParams as LinearLayout.LayoutParams
            params.width = resources.getDimensionPixelSize(
                if (active) R.dimen.gallery_dot_active else R.dimen.gallery_dot,
            )
            dot.layoutParams = params
            dot.background = getDrawable(
                if (active) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive,
            )
        }
    }

    private fun selectColor(option: ProductOption) {
        selectedOption = option
        val product = currentProduct ?: return
        selectedColor.text = option.value
        price.text = getString(R.string.price_format, formatPrice(option.price))
        renderGallery(option.images.ifEmpty { listOf(product.image) }, product.image)
    }

    private fun updateQuantity() {
        quantityValue.text = quantity.toString()
        findViewById<ImageButton>(R.id.decreaseQuantity).isEnabled = quantity > 1
    }

    private fun shareProduct() {
        val product = currentProduct ?: return
        startActivity(
            Intent.createChooser(
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "${product.brandName} ${product.name} · ${formatPrice(product.finalPrice)} KWD\n${product.webUrl}",
                    )
                },
                getString(R.string.share_product),
            ),
        )
    }

    private fun formatPrice(value: String): String {
        return value.toDoubleOrNull()?.let { String.format(Locale.US, "%.2f", it) } ?: value
    }
}
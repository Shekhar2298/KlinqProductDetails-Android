# Klinq Product Details — Android Assignment

This is the native Android submission for the product-details assignment.

## Required stack

- Kotlin
- XML layouts
- Retrofit with Gson converter
- MVVM with `ViewModel`, `StateFlow`, and a repository layer
- ViewPager2 for the product gallery
- Coil for remote image loading

## Open and run

1. Open the `android-app` folder in Android Studio.
2. Let Android Studio sync the Gradle project.
3. Run the `app` configuration on an emulator or Android device.

The app reads the supplied product endpoint directly:

`https://klinq.com/rest/V1/productdetails/6701/253620?lang=en&store=KWD`

The app needs internet access to load the product and image gallery.

## Assignment behavior covered

- Product name, brand, price, SKU, variants, images, stock-backed product data, and HTML product description
- Image ratio is preserved with a square `ViewPager2` and `centerInside` image scale type
- Color selection switches the gallery and price
- Image pagination dots
- Favorite, share, bag, add-to-bag, quantity, retry, and expandable product-information actions
- Loading and error states

## Submission

Zip the complete `android-app` folder and send it as the source-code attachment. Do not include the Gradle `build/` directory or Android Studio `.idea/` directory if Android Studio created them locally.
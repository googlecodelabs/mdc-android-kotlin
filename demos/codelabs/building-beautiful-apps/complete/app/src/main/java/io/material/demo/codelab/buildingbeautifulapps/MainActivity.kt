/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.material.demo.codelab.buildingbeautifulapps

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.shr_main.*
import java.io.IOException
import java.util.*

/**
 * Main activity for Shrine that displays a listing of available products.
 */
class MainActivity : AppCompatActivity() {

    private var adapter: ProductAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shr_main)

        setSupportActionBar(app_bar)

        val products = readProductsList()
        val imageRequester = ImageRequester.getInstance(this)

        val headerProduct = getHeaderProduct(products)
        imageRequester.setImageFromUrl(app_bar_image, headerProduct.url)

        product_list.setHasFixedSize(true)
        product_list.layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.shr_column_count))
        adapter = ProductAdapter(products, imageRequester)
        product_list.adapter = adapter

        bottom_navigation.setOnNavigationItemSelectedListener {
            val layoutManager = product_list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(0, 0)
            shuffleProducts()
            true
        }

        bottom_navigation.setOnNavigationItemReselectedListener {
            val layoutManager = product_list.layoutManager as GridLayoutManager
            layoutManager.scrollToPositionWithOffset(0, 0)
        }

        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.category_home
        }
    }

    private fun getHeaderProduct(products: List<ProductEntry>): ProductEntry {
        if (products.isEmpty()) {
            throw IllegalArgumentException("There must be at least one product")
        }

        for (i in products.indices) {
            if ("Perfect Goldfish Bowl" == products[i].title) {
                return products[i]
            }
        }
        return products[0]
    }

    private fun shuffleProducts() {
        val products = readProductsList()
        Collections.shuffle(products)
        adapter?.setProducts(products)
    }

    private fun readProductsList(): ArrayList<ProductEntry> {
        val inputStream = resources.openRawResource(R.raw.products)
        val productListType = object : TypeToken<ArrayList<ProductEntry>>() {

        }.type
        try {
            return JsonReader.readJsonStream<ArrayList<ProductEntry>>(inputStream, productListType)
        } catch (e: IOException) {
            Log.e(TAG, "Error reading JSON product list", e)
            return ArrayList()
        }

    }

    private class ProductAdapter internal constructor(private var products: List<ProductEntry>, private val imageRequester: ImageRequester) : RecyclerView.Adapter<ProductViewHolder>() {

        internal fun setProducts(products: List<ProductEntry>) {
            this.products = products
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ProductViewHolder {
            return ProductViewHolder(viewGroup)
        }

        override fun onBindViewHolder(viewHolder: ProductViewHolder, i: Int) {
            viewHolder.bind(products[i], imageRequester)
        }

        override fun getItemCount(): Int {
            return products.size
        }
    }

    private class ProductViewHolder internal constructor(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.shr_product_entry, parent, false)) {
        private val imageView: NetworkImageView
        private val priceView: TextView

        private val clickListener = View.OnClickListener { v ->
            val product = v.getTag(R.id.tag_product_entry) as ProductEntry
            // TODO: show product details
        }

        init {
            imageView = itemView.findViewById(R.id.image) as NetworkImageView
            priceView = itemView.findViewById(R.id.price) as TextView
            itemView.setOnClickListener(clickListener)
        }


        internal fun bind(product: ProductEntry, imageRequester: ImageRequester) {
            itemView.setTag(R.id.tag_product_entry, product)
            imageRequester.setImageFromUrl(imageView, product.url)
            priceView.text = product.price
        }
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}

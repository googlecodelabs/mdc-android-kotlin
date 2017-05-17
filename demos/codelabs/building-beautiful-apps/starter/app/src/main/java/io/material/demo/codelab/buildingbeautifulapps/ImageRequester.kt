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

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.NetworkImageView
import com.android.volley.toolbox.Volley

/**
 * Singleton class that handles image requests using Volley.
 */
class ImageRequester private constructor(context: Context) {
    private val requestQueue: RequestQueue
    private val imageLoader: ImageLoader
    private val maxByteSize: Int

    init {
        this.requestQueue = Volley.newRequestQueue(context.applicationContext)
        this.requestQueue.start()
        this.maxByteSize = calculateMaxByteSize(context)
        this.imageLoader = ImageLoader(
                requestQueue,
                object : ImageLoader.ImageCache {
                    private val lruCache = object : LruCache<String, Bitmap>(maxByteSize) {
                        override fun sizeOf(url: String, bitmap: Bitmap): Int {
                            return bitmap.byteCount
                        }
                    }

                    @Synchronized override fun getBitmap(url: String): Bitmap? {
                        return lruCache.get(url)
                    }

                    @Synchronized override fun putBitmap(url: String, bitmap: Bitmap) {
                        lruCache.put(url, bitmap)
                    }
                })
    }

    fun setImageFromUrl(networkImageView: NetworkImageView, url: String) {
        networkImageView.setImageUrl(url, imageLoader)
    }

    companion object {
        @Volatile private var instance: ImageRequester? = null

        private fun calculateMaxByteSize(context: Context): Int {
            val displayMetrics = context.resources.displayMetrics
            val screenBytes = displayMetrics.widthPixels * displayMetrics.heightPixels * 4
            return screenBytes * 3
        }

        /**
         * Returns the [ImageRequester] that is associated with the specified context,
         * instantiating one if it has not been created yet.

         * @param context the activity that will use the ImageRequester
         */
        fun getInstance(context: Context): ImageRequester {
            var result = instance
            if (result == null) {
                synchronized(ImageRequester::class.java) {
                    result = instance
                    if (result == null) {
                        instance = ImageRequester(context)
                        result = instance
                    }
                }
            }

            return result!!
        }
    }
}

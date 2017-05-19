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

import com.google.gson.Gson
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type

/**
 * Utilities for reading JSON input into Java objects.
 */
object JsonReader {
    private val TAG = JsonReader::class.java.simpleName

    @Throws(IOException::class)
    fun <T> readJsonStream(inputStream: InputStream, typeOfT: Type): T {
        val jsonString = inputStream.bufferedReader().use{ it.readText() }
        val gson = Gson()
        return gson.fromJson<T>(jsonString, typeOfT)
    }
}

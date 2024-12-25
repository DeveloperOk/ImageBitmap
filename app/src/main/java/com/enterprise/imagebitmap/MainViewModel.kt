package com.enterprise.imagebitmap

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    val imageBitmap = mutableStateOf<Bitmap?>(null)

}
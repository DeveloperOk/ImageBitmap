package com.enterprise.imagebitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.enterprise.imagebitmap.ui.theme.ImageBitmapTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream


class MainActivity : ComponentActivity() {

    lateinit var mainViewModel: MainViewModel

    val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        // Handle the returned Uri

        mainViewModel.viewModelScope.launch(Dispatchers.IO) {

            uri?.let{nonNullUri ->

                val inputStream: InputStream? = contentResolver.openInputStream(nonNullUri)

                inputStream?.let { nonNullInputStream ->

                    val readBitmap: Bitmap? = BitmapFactory.decodeStream(nonNullInputStream)

                    mainViewModel.viewModelScope.launch(Dispatchers.Main) {

                        mainViewModel.imageBitmap.value = readBitmap

                    }

                }
            }

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            ImageBitmapTheme {

                ImageBitmapApp(mainViewModel = mainViewModel, getContent = getContent)

            }
        }
    }
}

@Composable
fun ImageBitmapApp(mainViewModel: MainViewModel, getContent: ActivityResultLauncher<String>) {

    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
         modifier = Modifier
             .fillMaxSize()
             .background(color = Color.Green)){

        Scaffold(modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()) { innerPadding ->

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .background(color = Color.White)){


                val bitmap = mainViewModel.imageBitmap.value?.asImageBitmap()
                if(bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = stringResource(id = R.string.main_activity_main_image_content_description),
                        modifier = Modifier.width(300.dp).height(240.dp),
                        contentScale = ContentScale.FillBounds
                    )
                }else{

                    Text(text = stringResource(id = R.string.main_activity_no_image_selected))

                }

                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    onClick = {

                        getContent.launch("image/*")

                    })
                {
                    Text(text = stringResource(id = R.string.main_activity_load_image_button_text))
                }

            }

        }

    }

}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {

}
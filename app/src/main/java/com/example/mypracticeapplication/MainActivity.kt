package com.example.mypracticeapplication

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.example.mypracticeapplication.ui.theme.MyPracticeApplicationTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import coil.compose.AsyncImage

// Define your data class representing the expected response structure.
data class Post(
    val copyright: String,
    val explanation: String,
    val title: String,
    val url: String
)

// Define your Retrofit API service interface.
interface ApiService {
    @GET("apod")
//    suspend fun getPosts(): Response<List<Post>> // Note the Response wrapper.
    suspend fun getPosts(
        @Query("api_key") apiKey: String,
        @Query("start_date") startDate: String
    ): Response<List<Post>>
}

@Composable
fun MyApp() {
    var posts by remember { mutableStateOf(emptyList<Post>()) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch posts from the API when the app starts
    LaunchedEffect(key1 = Unit) {
        coroutineScope.launch(Dispatchers.IO) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/planetary/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            val apiKey = "DEMO_KEY"
            val startDate = "2023-09-08"

            val response = apiService.getPosts(apiKey, startDate)

            if (response.isSuccessful) {
                posts = response.body() ?: emptyList()
                Log.d("ABC", "Posts size: ${posts.size}")
                Log.d("ABC", "$posts")
            } else {
                // Handle API error here
                Log.d("ABC", "Posts size: ${posts.size}")
                Log.d("ABC", "$posts")
                Log.d("ABC", "Failed to retrieve!")
            }
        }
    }

    // Display the list of posts
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = "",
            onValueChange = { /* TODO: Implement search */ },
            modifier = Modifier.padding(16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(posts) { post ->
                Column {
                    Text(text = post.title)
                    if(post.copyright != null) {
                        Text(text = post.copyright)
                    }
//                    Text(text = post.url)
                    AsyncImage(
                        model = post.url,
                        contentDescription = post.title
                    )
                    Divider()


                }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMyApp() {
    MyPracticeApplicationTheme {
        MyApp()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPracticeApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}
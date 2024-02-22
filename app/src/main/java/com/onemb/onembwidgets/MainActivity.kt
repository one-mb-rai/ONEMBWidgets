package com.onemb.onembwidgets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository


class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                Scaffold {
                    Box(Modifier.padding(it)) {

                    }
                }
            }
        }

}


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val widgetManager = AppWidgetManager.getInstance(this)
//        val widgetProviders = widgetManager.getInstalledProvidersForPackage(packageName, null)
//        setContent {
//            ONEMBWidgetsTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background
//                ) {
//                    Scaffold {
//                        LazyColumn(contentPadding = it) {
//                            items(widgetProviders) { providerInfo ->
//                                WidgetInfoCard(providerInfo)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//private fun WidgetInfoCard(providerInfo: AppWidgetProviderInfo) {
//    val context = LocalContext.current
//    val label = providerInfo.loadLabel(context.packageManager)
//    val description = "Screen unlocking\n counter widget"
//    val preview = painterResource(id = providerInfo.previewImage)
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        onClick = {
////            providerInfo.pin(context)
//        }
//    ) {
//        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
//            Column {
//                Text(
//                    text = label,
//                    style = MaterialTheme.typography.bodyLarge
//                )
//                Text(
//                    text = description,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//            Image(painter = preview, contentDescription = description)
//        }
//    }
//}
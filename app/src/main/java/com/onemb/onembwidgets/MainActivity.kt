package com.onemb.onembwidgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.onemb.onembwidgets.db.ScreenUnlockCounterDb
import com.onemb.onembwidgets.ui.theme.ONEMBWidgetsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetManager = AppWidgetManager.getInstance(this)
        val widgetProviders = widgetManager.getInstalledProvidersForPackage(packageName, null)
        setContent {
            ONEMBWidgetsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        LazyColumn(contentPadding = it) {
                            items(widgetProviders) { providerInfo ->
                                WidgetInfoCard(providerInfo)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetInfoCard(providerInfo: AppWidgetProviderInfo) {
    val context = LocalContext.current
    val label = providerInfo.loadLabel(context.packageManager)
    val description = "Screen unlocking\n counter widget"
    val preview = painterResource(id = providerInfo.previewImage)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        onClick = {
//            providerInfo.pin(context)
        }
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Image(painter = preview, contentDescription = description)
        }
    }
}
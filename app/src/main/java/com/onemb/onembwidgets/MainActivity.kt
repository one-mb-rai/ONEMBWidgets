package com.onemb.onembwidgets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository
import com.onemb.onembwidgets.repository.ScreenTimeoutSettingsRepository.contentResolver


class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)


            setContent {
                Scaffold {
                    Box(Modifier.padding(it)) {
                        var isPermissionAvailable = remember {mutableStateOf(false)}

                        if (Settings.System.canWrite(MyApplication.getAppContext())) {
                            isPermissionAvailable.value = true
                            Settings.System.putInt(ScreenTimeoutSettingsRepository.contentResolver, Settings.System.SCREEN_OFF_TIMEOUT, 300000);
                            val text = "You have the permission now"
                            val duration = Toast.LENGTH_SHORT

                            val toast = Toast.makeText(MyApplication.getAppContext(), text, duration)
                            toast.show()
                        }
                        MainActivityView(isPermissionAvailable)
                    }
                }
            }
        }

}

@Composable
fun MainActivityView(isPermissionAvailable: MutableState<Boolean>) {
    Column {
        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 24.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Hi, this app needs permission",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Text(
                    text = "to be able to change timeout of screen based on quick tiles click.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Spacer(modifier = Modifier.height(16.dp))  // Add spacing after text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isPermissionAvailable.value) "Permission Granted" else "Permission Required",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isPermissionAvailable.value) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.error
                    )
                }
                if(!isPermissionAvailable.value) {
                    Button(
                        onClick = {
                            if (Settings.System.canWrite(MyApplication.getAppContext())) {
                                isPermissionAvailable.value = true;
                                Settings.System.putInt(
                                    contentResolver,
                                    Settings.System.SCREEN_OFF_TIMEOUT,
                                    300000
                                );
                                val text = "You have the permission now"
                                val duration = Toast.LENGTH_SHORT

                                val toast =
                                    Toast.makeText(MyApplication.getAppContext(), text, duration)
                                toast.show()
                            } else {
                                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.setData(Uri.parse("package:" + MyApplication.getAppContext().packageName))
                                MyApplication.getAppContext().startActivity(intent)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(text = "Check Permission", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 24.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primaryContainer,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "We also have a widget. Our widget ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Text(
                    text = "Screen unlock Counter",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "helps user to record number of times user has unlocked their phone's screen",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.inverseSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
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
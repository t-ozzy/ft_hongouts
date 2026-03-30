package com.tozeki.ft_hongouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tozeki.ft_hongouts.ui.theme.Ft_hongoutsTheme
import kotlinx.serialization.Serializable

// --- ナビゲーション用のルート定義 ---
@Serializable
object ContactListRoute

@Serializable
data class ContactDetailRoute(val contactId: Long)

@Serializable
data class ContactEditRoute(val contactId: Long? = null) // null の場合は新規作成

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ft_hongoutsTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ContactListRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 連絡先一覧画面
                        composable<ContactListRoute> {
                            ContactListScreen(
                                onContactClick = { id ->
                                    navController.navigate(ContactDetailRoute(contactId = id))
                                },
                                onAddContactClick = {
                                    navController.navigate(ContactEditRoute())
                                }
                            )
                        }
                        // 連絡先詳細画面
                        composable<ContactDetailRoute> { backStackEntry ->
                            val route: ContactDetailRoute = backStackEntry.toRoute()
                            ContactDetailScreen(
                                contactId = route.contactId,
                                onEditClick = { id ->
                                    navController.navigate(ContactEditRoute(contactId = id))
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        // 連絡先編集・作成画面
                        composable<ContactEditRoute> { backStackEntry ->
                            val route: ContactEditRoute = backStackEntry.toRoute()
                            ContactEditScreen(
                                contactId = route.contactId,
                                onSaveClick = { navController.popBackStack() },
                                onCancelClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- 各画面のモック Composable ---

@Composable
fun ContactListScreen(onContactClick: (Long) -> Unit, onAddContactClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "連絡先一覧画面 (モック)")
        Button(onClick = { onContactClick(1) }, modifier = Modifier.padding(8.dp)) {
            Text("連絡先 ID:1 を見る")
        }
        Button(onClick = onAddContactClick, modifier = Modifier.padding(8.dp)) {
            Text("新規連絡先を追加")
        }
    }
}

@Composable
fun ContactDetailScreen(contactId: Long, onEditClick: (Long) -> Unit, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "連絡先詳細画面 (ID: $contactId)")
        Button(onClick = { onEditClick(contactId) }, modifier = Modifier.padding(8.dp)) {
            Text("この連絡先を編集")
        }
        Button(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
            Text("戻る")
        }
    }
}

@Composable
fun ContactEditScreen(contactId: Long?, onSaveClick: () -> Unit, onCancelClick: () -> Unit) {
    val title = if (contactId == null) "新規作成" else "編集 (ID: $contactId)"
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "連絡先 $title 画面")
        Button(onClick = onSaveClick, modifier = Modifier.padding(8.dp)) {
            Text("保存して戻る")
        }
        Button(onClick = onCancelClick, modifier = Modifier.padding(8.dp)) {
            Text("キャンセル")
        }
    }
}
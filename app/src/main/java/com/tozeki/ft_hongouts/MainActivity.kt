package com.tozeki.ft_hongouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper
import com.tozeki.ft_hongouts.ui.screens.ContactDetailScreen
import com.tozeki.ft_hongouts.ui.screens.ContactEditScreen
import com.tozeki.ft_hongouts.ui.screens.ContactListScreen
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
        val repository = ContactRepository(DatabaseHelper(this))
        setContent {
            Ft_hongoutsTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            navController.navigate(ContactEditRoute())
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Contact")
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = ContactListRoute,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // 連絡先一覧画面
                        composable<ContactListRoute> {
                            ContactListScreen(
                                repository = repository,
                                onContactClick = { id ->
                                    navController.navigate(ContactDetailRoute(contactId = id))
                                }
                            )
                        }
                        // 連絡先詳細画面
                        composable<ContactDetailRoute> { backStackEntry ->
                            val route: ContactDetailRoute = backStackEntry.toRoute()
                            ContactDetailScreen(
                                contactId = route.contactId,
                                repository = repository,
                                onEditClick = { id ->
                                    navController.navigate(ContactEditRoute(contactId = id))
                                },
                                onDeleteSuccess = { navController.popBackStack() },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        // 連絡先編集・作成画面
                        composable<ContactEditRoute> { backStackEntry ->
                            val route: ContactEditRoute = backStackEntry.toRoute()
                            ContactEditScreen(
                                contactId = route.contactId,
                                repository = repository,
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
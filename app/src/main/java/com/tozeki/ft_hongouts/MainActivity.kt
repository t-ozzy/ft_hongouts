package com.tozeki.ft_hongouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import com.tozeki.ft_hongouts.data.DatabaseHelper
import com.tozeki.ft_hongouts.ui.theme.Ft_hongoutsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

// --- 各画面の実装 ---

@Composable
fun ContactListScreen(
    repository: ContactRepository,
    onContactClick: (Long) -> Unit
) {
    var contacts by remember { mutableStateOf(emptyList<Contact>()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            contacts = repository.getAllContacts()
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(contacts) { contact ->
            ListItem(
                headlineContent = { Text("${contact.lastName} ${contact.firstName}") },
                supportingContent = { Text(contact.phoneNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onContactClick(contact.id) }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun ContactDetailScreen(
    contactId: Long,
    repository: ContactRepository,
    onEditClick: (Long) -> Unit,
    onDeleteSuccess: () -> Unit,
    onBackClick: () -> Unit
) {
    var contact by remember { mutableStateOf<Contact?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(contactId) {
        withContext(Dispatchers.IO) {
            contact = repository.getContactById(contactId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        contact?.let {
            Text(text = "姓: ${it.lastName}")
            Text(text = "名: ${it.firstName}")
            Text(text = "電話番号: ${it.phoneNumber}")
            it.email?.let { email -> Text(text = "メール: $email") }
            it.memo?.let { memo -> Text(text = "メモ: $memo") }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onEditClick(contactId) }) {
                    Text("編集")
                }
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        repository.delete(contactId)
                        withContext(Dispatchers.Main) {
                            onDeleteSuccess()
                        }
                    }
                }) {
                    Text("削除")
                }
            }
        } ?: Text("読み込み中...")

        Button(onClick = onBackClick) {
            Text("戻る")
        }
    }
}

@Composable
fun ContactEditScreen(
    contactId: Long?,
    repository: ContactRepository,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(contactId) {
        if (contactId != null) {
            withContext(Dispatchers.IO) {
                repository.getContactById(contactId)?.let {
                    firstName = it.firstName
                    lastName = it.lastName
                    phone = it.phoneNumber
                    email = it.email ?: ""
                    memo = it.memo ?: ""
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        androidx.compose.material3.OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("姓") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("名") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("電話番号") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("メール") },
            modifier = Modifier.fillMaxWidth()
        )
        androidx.compose.material3.OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("メモ") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    // Note: email,memo 以外は値が空白かどうかのvalidateはしていない。今後修正予定。
                    val contact = Contact(
                        id = contactId ?: 0,
                        firstName = firstName,
                        lastName = lastName,
                        phoneNumber = phone,
                        email = email.takeIf { it.isNotBlank() },
                        memo = memo.takeIf { it.isNotBlank() }
                    )
                    if (contactId == null) {
                        repository.insert(contact)
                    } else {
                        repository.update(contact)
                    }
                    withContext(Dispatchers.Main) {
                        onSaveClick()
                    }
                }
            }) {
                Text("保存")
            }
            Button(onClick = onCancelClick) {
                Text("キャンセル")
            }
        }
    }
}
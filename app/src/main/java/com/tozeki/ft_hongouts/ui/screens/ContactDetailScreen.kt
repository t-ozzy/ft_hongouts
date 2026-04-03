package com.tozeki.ft_hongouts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

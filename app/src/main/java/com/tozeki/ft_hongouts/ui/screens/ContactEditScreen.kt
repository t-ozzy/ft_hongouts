package com.tozeki.ft_hongouts.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
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
        OutlinedTextField(
            value = lastName,
            onValueChange = { lastName = it },
            label = { Text("姓") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = firstName,
            onValueChange = { firstName = it },
            label = { Text("名") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("電話番号") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("メール") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("メモ") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
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

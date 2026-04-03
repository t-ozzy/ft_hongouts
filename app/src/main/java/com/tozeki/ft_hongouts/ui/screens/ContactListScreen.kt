package com.tozeki.ft_hongouts.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tozeki.ft_hongouts.data.Contact
import com.tozeki.ft_hongouts.data.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

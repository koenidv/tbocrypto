package com.koenidv.tbocrypto.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.koenidv.tbocrypto.R

@Composable
fun ErrorMessage(message: String, retryAction: (() -> Unit)? = null) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.error
        )
        retryAction?.let { action ->
            TextButton(action) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}
package com.example.myapplication.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.myapplication.R

@Composable
fun InfoDialog(
    showErrorDialog: MutableState<Boolean>,
    error: String?
) {
    Dialog(
        onDismissRequest = { showErrorDialog.value = false }
    ) {
        Card(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp,

            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 16.dp),
                    text = error ?: "",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = { showErrorDialog.value = false }
                ) {
                    Text(text = stringResource(R.string.got_it))
                }
            }
        }
    }
}

@Preview
@Composable
fun InfoDialogPreview() {
    val showErrorDialog = remember { mutableStateOf(false) }
    InfoDialog(
        showErrorDialog = showErrorDialog,
        error = "Hello my name is ddddddddddddddddddddddddddddd"
    )
}
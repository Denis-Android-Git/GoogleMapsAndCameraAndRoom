package com.example.myapplication.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel = koinViewModel()
) {
    val text by searchViewModel.searchText.collectAsStateWithLifecycle()

    val isSearching by searchViewModel.isSearching.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary,
        topBar = {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (isSearching) 0.dp else 16.dp),
                shadowElevation = 10.dp,
                colors =  SearchBarDefaults.colors(
                    //containerColor = MaterialTheme.colorScheme.primary
                ),
                inputField = {
                    SearchBarDefaults.InputField(
                        query = text,
                        onQueryChange = searchViewModel::onQueryChange,
                        onSearch = searchViewModel::onQueryChange,
                        expanded = isSearching,
                        onExpandedChange = { searchViewModel.onExpandedChange() },
                        placeholder = { Text("Поиск мест") },
                        leadingIcon = {
                            AnimatedVisibility(isSearching) {
                                IconButton(
                                    onClick = {
                                        searchViewModel.onExpandedChange()
                                    }
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = null
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            AnimatedVisibility(isSearching && text.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchViewModel.onQueryChange("")
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = null)
                                }
                            }
                        }
                    )
                },
                expanded = isSearching,
                onExpandedChange = { searchViewModel.onExpandedChange() }
            ) {

            }
        }
    ) {
    }
}
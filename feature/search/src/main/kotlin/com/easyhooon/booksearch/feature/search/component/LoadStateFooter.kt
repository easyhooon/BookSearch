package com.easyhooon.booksearch.feature.search.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.easyhooon.booksearch.core.designsystem.R
import com.easyhooon.booksearch.core.designsystem.theme.Black
import com.easyhooon.booksearch.core.designsystem.theme.Neutral200
import com.easyhooon.booksearch.core.designsystem.theme.Neutral500
import com.easyhooon.booksearch.core.designsystem.theme.White
import com.easyhooon.booksearch.core.designsystem.theme.body2Regular

@Composable
fun LoadStateFooter(
    footerState: FooterState,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        when (footerState) {
            is FooterState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Neutral500,
                )
            }

            is FooterState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = footerState.message,
                        color = Black,
                        style = body2Regular,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onRetryClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = White,
                            contentColor = Black,
                        ),
                        border = BorderStroke(width = 1.dp, color = Neutral200),
                    ) {
                        Text(text = stringResource(R.string.retry))
                    }
                }
            }

            is FooterState.End -> {
                Text(
                    text = stringResource(R.string.no_more_result),
                    color = Neutral500,
                    style = body2Regular,
                )
            }

            is FooterState.Idle -> {
                // No content
            }
        }
    }
}

@Immutable
sealed interface FooterState {
    data object Idle : FooterState
    data object Loading : FooterState
    data object End : FooterState
    data class Error(val message: String) : FooterState
}

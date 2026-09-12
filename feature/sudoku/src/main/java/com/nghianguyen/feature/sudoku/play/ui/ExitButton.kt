package com.nghianguyen.feature.sudoku.play.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A button used to exit the play screen.
 *
 * @param modifier Modifier for the button.
 * @param onClick Callback for the exit action.
 */
@Composable
fun ExitButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(PlayScreenConstants.EXIT_BUTTON_SIZE),
        shape = CircleShape,
        border =
            BorderStroke(
                PlayScreenConstants.DEFAULT_BORDER_WIDTH,
                MaterialTheme.colorScheme.outline,
            ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Exit",
            modifier = Modifier.size(PlayScreenConstants.EXIT_ICON_SIZE),
        )
    }
}

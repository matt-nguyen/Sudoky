package com.nghianguyen.sudoky

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nghianguyen.feature.sudoku.play.ui.PlayContent
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayAction
import com.nghianguyen.feature.sudoku.play.viewmodel.PlayScreenState
import com.nghianguyen.ui.component.sudokugrid.DigitCellItem
import com.nghianguyen.ui.component.sudokugrid.SudokuGridState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayContentTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun notesMode_whenSelectionHasNotes_exposesStateAndDispatchesKeypadActions() {
        val actions = mutableListOf<PlayAction>()
        var notesMode by mutableStateOf(true)
        val state =
            PlayScreenState(
                sudokuGridState = SudokuGridState(noteItems = listOf(DigitCellItem(7, 0, 1))),
                selectedCell = 0 to 1,
            )
        composeRule.setContent {
            MaterialTheme {
                PlayContent(
                    state = state.copy(notesMode = notesMode),
                    onAction = {
                        actions.add(it)
                        if (it == PlayAction.OnNotesModeToggled) notesMode = !notesMode
                    },
                )
            }
        }

        val selection =
            SemanticsMatcher.expectValue(
                SemanticsProperties.StateDescription,
                "Row 1, column 2, notes 7",
            )
        composeRule.onNode(selection).assertExists()
        val modeKey = composeRule.onNodeWithContentDescription("Notes mode")
        modeKey
            .performScrollTo()
            .assertIsOn()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    "Entering notes",
                ),
            )
            .performClick()
        modeKey
            .assertIsOff()
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.StateDescription,
                    "Entering digits",
                ),
            )
        composeRule.onNodeWithText("2").performScrollTo().performClick()
        composeRule.onNodeWithText("Clear").performScrollTo().performClick()
        composeRule.runOnIdle {
            assertEquals(
                listOf(
                    PlayAction.OnNotesModeToggled,
                    PlayAction.OnDigitEntered(2),
                    PlayAction.OnDigitEntered(0),
                ),
                actions,
            )
        }
    }
}

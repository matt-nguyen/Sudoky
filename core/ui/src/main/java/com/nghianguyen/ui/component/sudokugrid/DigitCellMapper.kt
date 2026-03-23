package com.nghianguyen.ui.component.sudokugrid

import com.nghianguyen.sudoku.model.DigitCell

/**
 * Maps a [DigitCell] domain model to a [DigitCellItem] UI model.
 *
 * @return A [DigitCellItem] with the same digit value, row, and column as the [DigitCell].
 */
fun DigitCell.toDigitCellItem(): DigitCellItem {
    return DigitCellItem(digit = current, row = row, col = col)
}

/**
 * Maps a list of [DigitCell] domain models to a list of [DigitCellItem] UI models.
 *
 * @return A list of [DigitCellItem]s.
 */
fun List<DigitCell>.toDigitCellItems(): List<DigitCellItem> {
    return map { it.toDigitCellItem() }
}

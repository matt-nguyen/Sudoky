package com.nghianguyen.data.scanner.mlkit

import android.graphics.Point
import android.util.Log
import com.google.mlkit.vision.text.Text
import com.nghianguyen.sudoku.model.ScannedDigitCell

/**
 * Responsible for mapping detected text from an image to a 9x9 Sudoku grid. This class calculates
 * the grid cell positions based on the image dimensions and ensures each cell is only occupied by a
 * single detected digit.
 */
class SudokuGridMapper {

    /**
     * Maps the provided [Text] object to a list of [ScannedDigitCell]s.
     *
     * @param text The [Text] detected by ML Kit containing potential Sudoku digits.
     * @param bitmapHeight The height of the bitmap from which the text was detected. This is used
     *   to calculate the size of each Sudoku cell.
     * @return A list of [ScannedDigitCell] representing the digits found in the grid.
     */
    fun mapTextToGrid(text: Text, bitmapHeight: Int): List<ScannedDigitCell> {
        val foundDigits = mutableListOf<ScannedDigitCell>()
        val size = bitmapHeight / 9

        // Map of <Row, List<Column>> to track occupied cells and avoid duplicates
        val occupiedCells = mutableMapOf<Int, MutableList<Int>>()

        for (block in text.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    element.boundingBox?.let { box ->
                        val center = Point(box.centerX(), box.centerY())
                        val row = center.y / size

                        if (element.text.length > 1) {
                            var columnFirst = center.x / size
                            var columnSecond = columnFirst + 1

                            val distanceFromLeft = center.x - (center.x / size * size)
                            if (distanceFromLeft < size / 2) {
                                columnFirst--
                                columnSecond--
                            }

                            if (addCellIfNotExists(occupiedCells, row, columnFirst)) {
                                foundDigits.add(
                                    ScannedDigitCell(element.text[0].toString(), row, columnFirst)
                                )
                            }

                            if (addCellIfNotExists(occupiedCells, row, columnSecond)) {
                                foundDigits.add(
                                    ScannedDigitCell(
                                        element.text[element.text.length - 1].toString(),
                                        row,
                                        columnSecond,
                                    )
                                )
                            }
                        } else {
                            val column = center.x / size

                            if (addCellIfNotExists(occupiedCells, row, column)) {
                                foundDigits.add(ScannedDigitCell(element.text, row, column))
                                Log.d(
                                    "SudokuGridMapper",
                                    "Single: ${element.text} at row $row, col $column",
                                )
                            }
                        }
                    }
                }
            }
        }

        return foundDigits
    }

    /**
     * Helper function to add a row and column to the [occupiedCells] tracking map if the cell
     * hasn't already been filled.
     *
     * Used to handle the same digit getting detected in multiple [Element]s. Happens when an
     * [Element] containing multiple digits overlaps with a single digit [Element].
     *
     * @param occupiedCells The map tracking which grid cells are already occupied.
     * @param row The row index (0-8).
     * @param column The column index (0-8).
     * @return True if the cell was successfully marked as occupied.
     */
    private fun addCellIfNotExists(
        occupiedCells: MutableMap<Int, MutableList<Int>>,
        row: Int,
        column: Int,
    ): Boolean {
        if (row !in 0..8 || column !in 0..8) return false

        val columnsForRow = occupiedCells.getOrPut(row) { mutableListOf() }
        if (!columnsForRow.contains(column)) {
            columnsForRow.add(column)
            return true
        }
        return false
    }
}

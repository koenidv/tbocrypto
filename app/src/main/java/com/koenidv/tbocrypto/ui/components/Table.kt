package com.koenidv.tbocrypto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LazyTable(data: List<List<Any>>, cellGap: Dp = 8.dp) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(cellGap)) {
        items(data.size) { rowIndex ->
            LazyTableRow(data[rowIndex], rowIndex, data.size, cellGap)
        }
    }
}

@Composable
fun LazyTableRow(rowData: List<Any>, rowIndex: Int, totalRows: Int, cellGap: Dp) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(cellGap)) {
        items(rowData.size) { columnIndex ->
            TableCell(rowData[columnIndex], rowIndex, columnIndex, totalRows, rowData.size)
        }
    }
}

@Composable
fun TableCell(cellData: Any, rowIndex: Int, columnIndex: Int, totalRows: Int, totalColumns: Int) {
    Text(cellData.toString())
}

@Preview
@Composable
fun TablePreview() {
    LazyTable(
        listOf(
            listOf("Day 3", "2€", "A"),
            listOf("Day 2", "3€", "B"),
            listOf("Day 1", "1.50€", "C")
        )
    )
}
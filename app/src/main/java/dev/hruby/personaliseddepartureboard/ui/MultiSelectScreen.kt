package dev.hruby.personaliseddepartureboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.hruby.personaliseddepartureboard.LineOption
import dev.hruby.personaliseddepartureboard.Screen

data class SelectableItem<TId>(
    val id: TId,
    val cardContent: @Composable () -> Unit
)

@Composable
fun <TId> MultiSelectScreen(
    items: List<SelectableItem<TId>>,
    onNext: (List<TId>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    initiallySelectedIds: Set<TId> = emptySet(),
) {
    var selectedIds by remember(items, initiallySelectedIds) {
        mutableStateOf(initiallySelectedIds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
//        LazyColumn(
//            modifier = Modifier.weight(1f),
//            verticalArrangement = Arrangement.spacedBy(12.dp)
//        ) {
//            itemsIndexed(
//                items = items,
//                key = { index, item -> item.id?.hashCode() ?: index }
//            ) { _, item ->
//                val isSelected = item.id in selectedIds
//
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {
//                            selectedIds = selectedIds.toggle(item.id)
//                        }
//                        .padding(vertical = 4.dp),
//                    verticalAlignment = Alignment.Top
//                ) {
//                    Checkbox(
//                        checked = isSelected,
//                        onCheckedChange = {
//                            selectedIds = selectedIds.toggle(item.id)
//                        }
//                    )
//
//                    Column(
//                        modifier = Modifier
//                            .padding(start = 8.dp)
//                            .weight(1f)
//                    ) {
//                        item.cardContent()
//                    }
//                }
//            }
//        }

        Column() {
            for(item in items) {
                val isSelected = item.id in selectedIds
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedIds = selectedIds.toggle(item.id)
                        }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = {
                            selectedIds = selectedIds.toggle(item.id)
                        }
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f)
                    ) {
                        item.cardContent()
                    }
                }
            }
        }


        CancelNextButtons(
            onNext = {
                onNext(selectedIds.toList())
            },
            onCancel = onCancel
        )
    }
}

private fun <T> Set<T>.toggle(value: T): Set<T> {
    return if (value in this) this - value else this + value
}


//@Preview
//@Composable
//fun MultiSelectLinesPreview() {
//    MultiSelectScreen(
//        items = listOf(
//            SelectableItem(id = "1", cardContent = { LineOption("1") }),
//            SelectableItem(id = "2", cardContent = { LineOption("2") })
//        ),
//        onNext = {},
//        onCancel = {},
//        initiallySelectedIds = emptySet()
//    )
//}
package com.squirtles.core.picklist.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.squirtles.core.common.ui.theme.Gray
import com.squirtles.core.common.ui.theme.White
import com.squirtles.picklist.R


@Composable
internal fun EditModeAction(
    isEditMode: Boolean,
    enabled: Boolean,
    isSelectedEmpty: Boolean,
    activateEditMode: () -> Unit,
    selectAllPicks: () -> Unit,
    deselectAllPicks: () -> Unit,
) {
    if (isEditMode) {
        if (isSelectedEmpty) {
            TextButton(
                onClick = selectAllPicks
            ) {
                Text(
                    text = stringResource(R.string.pick_list_select_all_button_text),
                    color = White,
                )
            }
        } else {
            TextButton(
                onClick = deselectAllPicks
            ) {
                Text(
                    text = stringResource(R.string.pick_list_deselect_all_button_text),
                    color = White,
                )
            }
        }
    } else {
        IconButton(
            onClick = activateEditMode,
            enabled = enabled,
            colors = IconButtonDefaults.iconButtonColors().copy(
                contentColor = White,
                disabledContentColor = Gray,
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.pick_list_activate_edit_mode_button_description),
            )
        }
    }
}

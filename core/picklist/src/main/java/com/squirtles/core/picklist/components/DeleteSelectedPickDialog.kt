package com.squirtles.core.picklist.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.squirtles.core.common.ui.DialogTextButton
import com.squirtles.core.common.ui.HorizontalSpacer
import com.squirtles.core.common.ui.MessageAlertDialog
import com.squirtles.core.common.ui.theme.Primary
import com.squirtles.core.picklist.PickListType
import com.squirtles.picklist.R

@Composable
internal fun DeleteSelectedPickDialog(
    selectedPickCount: Int,
    pickListType: PickListType,
    onDismissRequest: () -> Unit,
    onDeletePickClick: () -> Unit,
) {
    MessageAlertDialog(
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.delete_pick_dialog_title),
        body = stringResource(
            when (pickListType) {
                PickListType.FAVORITE -> R.string.delete_selected_favorite_pick_dialog_body
                PickListType.CREATED -> R.string.delete_selected_pick_dialog_body
            },
            selectedPickCount
        ),
        buttons = {
            DialogTextButton(
                onClick = onDismissRequest,
                text = stringResource(R.string.delete_pick_dialog_cancel)
            )

            HorizontalSpacer(8)

            DialogTextButton(
                onClick = onDeletePickClick,
                text = stringResource(R.string.delete_pick_dialog_delete),
                textColor = Primary,
                fontWeight = FontWeight.Bold
            )
        },
    )
}

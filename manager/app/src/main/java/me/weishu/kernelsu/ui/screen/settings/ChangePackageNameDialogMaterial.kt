package me.weishu.kernelsu.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import me.weishu.kernelsu.BuildConfig
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.component.material.ExpressiveDialog

@Composable
fun ChangePackageNameDialogMaterial(
    show: Boolean,
    onDismissRequest: () -> Unit,
) {
    if (!show) return

    val context = LocalContext.current
    var value by remember { mutableStateOf(generateForkPackageName()) }

    ExpressiveDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.settings_change_package_name)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.change_package_name_current, BuildConfig.APPLICATION_ID),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(stringResource(R.string.change_package_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.change_package_name_notice),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { copyPackageNameToClipboard(context, value) }) {
                Text(stringResource(R.string.change_package_name_copy))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { value = generateForkPackageName() }) {
                    Text(stringResource(R.string.change_package_name_random))
                }
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        }
    )
}

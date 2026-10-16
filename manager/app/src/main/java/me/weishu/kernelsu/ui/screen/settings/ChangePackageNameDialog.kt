package me.weishu.kernelsu.ui.screen.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import kotlin.random.Random
import me.weishu.kernelsu.R
import me.weishu.kernelsu.ui.LocalUiMode
import me.weishu.kernelsu.ui.UiMode

private const val FORK_PACKAGE_PREFIX = "me.w3xdev.kernelsu"

/**
 * Generates a valid, unique-looking package name for this fork.
 *
 * The official KernelSU uses `me.weishu.kernelsu`; a different package name lets this build be
 * installed next to it (and the original uninstalled independently).
 */
internal fun generateForkPackageName(): String {
    val letters = "abcdefghijklmnopqrstuvwxyz"
    val alnum = letters + "0123456789"
    val suffix = buildString {
        append(letters[Random.nextInt(letters.length)])
        repeat(5) { append(alnum[Random.nextInt(alnum.length)]) }
    }
    return "$FORK_PACKAGE_PREFIX.fork$suffix"
}

internal fun copyPackageNameToClipboard(context: Context, packageName: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText(packageName, packageName))
    Toast.makeText(context, context.getString(R.string.change_package_name_copied), Toast.LENGTH_SHORT).show()
}

@Composable
fun ChangePackageNameDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
) {
    when (LocalUiMode.current) {
        UiMode.Miuix -> ChangePackageNameDialogMiuix(show, onDismissRequest)
        UiMode.Material -> ChangePackageNameDialogMaterial(show, onDismissRequest)
    }
}

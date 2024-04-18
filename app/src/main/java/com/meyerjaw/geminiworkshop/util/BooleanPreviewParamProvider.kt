package com.meyerjaw.geminiworkshop.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

/**
 * Implementation of [PreviewParameterProvider] for providing true and false values.
 */
class BooleanPreviewParamProvider : PreviewParameterProvider<Boolean> {
    override val values: Sequence<Boolean>
        get() = sequenceOf(true, false)
}
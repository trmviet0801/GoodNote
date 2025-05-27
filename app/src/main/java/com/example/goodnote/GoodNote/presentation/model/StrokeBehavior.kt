package com.example.goodnote.goodNote.presentation.model

import com.example.goodnote.goodNote.action.StrokeAction
import com.example.goodnote.domain.Stroke

data class StrokeBehavior(
    val action: StrokeAction,
    val stroke: Stroke
)

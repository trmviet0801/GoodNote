package com.example.goodnote.goodNote.presentation.model

// serves as source for forward and backward features
data class StrokeBehaviors(
    var behaviors: List<StrokeBehavior> = emptyList<StrokeBehavior>()
)

fun StrokeBehaviors.pushBehavior(behavior: StrokeBehavior): StrokeBehaviors {
    behaviors = behaviors.plus(behavior)
    return this
}

fun StrokeBehaviors.popBehavior(): StrokeBehavior? {
    if (behaviors.isEmpty()) return null
    val currentBehaviors = behaviors.toMutableList()
    val newBehaviors = currentBehaviors.subList(0, currentBehaviors.size - 1)
    behaviors = newBehaviors
    return currentBehaviors[currentBehaviors.size - 1]
}

fun StrokeBehaviors.isEmpty(): Boolean {
    return behaviors.isEmpty()
}

fun StrokeBehaviors.clear(): StrokeBehaviors {
    behaviors = emptyList()
    return this
}
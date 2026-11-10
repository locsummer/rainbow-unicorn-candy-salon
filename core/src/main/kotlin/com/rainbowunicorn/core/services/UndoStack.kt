package com.rainbowunicorn.core.services

import com.rainbowunicorn.core.models.CharacterState

class UndoStack(val maxCapacity: Int = 20) {

    private val undoList = ArrayDeque<CharacterState>()
    private val redoList = ArrayDeque<CharacterState>()

    val canUndo: Boolean get() = undoList.size > 1
    val canRedo: Boolean get() = redoList.isNotEmpty()
    val size: Int get() = undoList.size

    fun initialize(initialState: CharacterState) {
        undoList.clear()
        redoList.clear()
        undoList.addLast(initialState)
    }

    fun push(newState: CharacterState) {
        if (undoList.isNotEmpty() && undoList.last() == newState) {
            return // Skip duplicate states
        }
        undoList.addLast(newState)
        if (undoList.size > maxCapacity) {
            undoList.removeFirst() // Drop oldest item
        }
        redoList.clear()
    }

    fun undo(): CharacterState? {
        if (!canUndo) return null
        val current = undoList.removeLast()
        redoList.addLast(current)
        return undoList.last()
    }

    fun redo(): CharacterState? {
        if (!canRedo) return null
        val next = redoList.removeLast()
        undoList.addLast(next)
        return next
    }

    fun clear() {
        undoList.clear()
        redoList.clear()
    }
}

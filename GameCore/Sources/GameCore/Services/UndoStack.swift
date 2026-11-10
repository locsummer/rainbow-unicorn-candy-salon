import Foundation

public final class UndoStack {

    public let maxCapacity: Int
    private var undoList: [CharacterState] = []
    private var redoList: [CharacterState] = []

    public var canUndo: Bool { undoList.count > 1 }
    public var canRedo: Bool { !redoList.isEmpty }
    public var size: Int { undoList.count }

    public init(maxCapacity: Int = 20) {
        self.maxCapacity = maxCapacity
    }

    public func initialize(with state: CharacterState) {
        undoList = [state]
        redoList = []
    }

    public func push(_ newState: CharacterState) {
        if let last = undoList.last, last == newState {
            return
        }
        undoList.append(newState)
        if undoList.count > maxCapacity {
            undoList.removeFirst()
        }
        redoList.removeAll()
    }

    public func undo() -> CharacterState? {
        guard canUndo else { return nil }
        let current = undoList.removeLast()
        redoList.append(current)
        return undoList.last
    }

    public func redo() -> CharacterState? {
        guard canRedo else { return nil }
        let next = redoList.removeLast()
        undoList.append(next)
        return next
    }

    public func clear() {
        undoList.removeAll()
        redoList.removeAll()
    }
}

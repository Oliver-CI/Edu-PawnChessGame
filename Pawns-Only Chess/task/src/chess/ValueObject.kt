package chess

data class ChessPosition(
    val column: String,
    val row: Int
) {
    override fun toString(): String {
        return "$column$row"
    }
}

data class Player(
    val name: String,
    val color: Color
)

enum class Color(val fullColor: String, val boardValue: String) {
    WHITE("white", "W"), BLACK("black", "B")
}

abstract class Pawn(val color: Color) {
    companion object {
        val WHITE_PAWN = White()
        val BLACK_PAWN = Black()
    }

    abstract fun validAttackSpeed(start: ChessPosition, end: ChessPosition): Boolean
    abstract fun validMoveSpeed(start: ChessPosition, end: ChessPosition): Boolean
    abstract fun atOppositeSide(currentPosition: ChessPosition): Boolean
    abstract fun getBlockedPosition(currentPosition: ChessPosition): ChessPosition
}

class White : Pawn(Color.WHITE) {
    companion object {
        const val startRank = 2
        const val endRank = 8
    }

    override fun validAttackSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        return (end.row - start.row) == 1
    }

    override fun validMoveSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        val diff = end.row - start.row
        return if (start.row == startRank) {
            diff == 1 || diff == 2
        } else {
            diff == 1
        }
    }

    override fun atOppositeSide(currentPosition: ChessPosition): Boolean = currentPosition.row == endRank
    override fun getBlockedPosition(currentPosition: ChessPosition): ChessPosition {
        return ChessPosition(currentPosition.column, currentPosition.row + 1)
    }
}

class Black : Pawn(Color.BLACK) {
    companion object {
        const val startRank = 7
        const val endRank = 1
    }

    override fun validAttackSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        return (start.row - end.row) == 1
    }

    override fun validMoveSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        val diff = start.row - end.row
        return if (start.row == startRank) {
            diff == 1 || diff == 2
        } else {
            diff == 1
        }
    }

    override fun atOppositeSide(currentPosition: ChessPosition): Boolean = currentPosition.row == endRank
    override fun getBlockedPosition(currentPosition: ChessPosition): ChessPosition {
        return ChessPosition(currentPosition.column, currentPosition.row - 1)
    }

}

class EnPassant(color: Color) : Pawn(color) {
    override fun validAttackSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        return false
    }

    override fun validMoveSpeed(start: ChessPosition, end: ChessPosition): Boolean {
        return false
    }

    override fun atOppositeSide(currentPosition: ChessPosition): Boolean {
        return false
    }

    override fun getBlockedPosition(currentPosition: ChessPosition): ChessPosition {
        TODO("Not yet implemented")
    }
}

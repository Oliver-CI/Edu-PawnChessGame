package chess

const val SEPARATOR = "  +---+---+---+---+---+---+---+---+"
const val BOTTOM = "    a   b   c   d   e   f   g   h"

fun printBoard(board: HashMap<ChessPosition, Pawn>) {
    println(SEPARATOR)
    for (i in 8 downTo 1) {
        var cellLine = "$i "
        for (j in 'a'..'h') {
            cellLine += "| ${getValueOnBoard(i, j, board)} "
            if (j == 'h') {
                cellLine += "|"
            }
        }
        println(cellLine)
        println(SEPARATOR)
    }
    println(BOTTOM)
}

fun getValueOnBoard(cellLine: Int, columnLetter: Char, board: HashMap<ChessPosition, Pawn>): String {
    val chessPosition = ChessPosition(columnLetter.toString(), cellLine)
    if (board.containsKey(chessPosition)) {
        val s: Pawn = board[chessPosition]!!
        return s.color.boardValue
    }
    return " "
}

fun getSideColumns(column: String): Pair<String, String> {
    val middleValue = column.chars().findFirst().asInt
    val left = (middleValue - 1).toChar().toString()
    val right = (middleValue + 1).toChar().toString()
    return Pair(left, right)
}

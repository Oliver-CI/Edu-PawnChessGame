package chess

import java.util.*
import kotlin.math.abs


const val EXIT_CODE = "exit"
val BOARD: HashMap<ChessPosition, Pawn> = HashMap()
var lastMove = ""
val players = mutableListOf<Player>()


fun loadBoard() {
    for (ch in 'a'..'h') {
        BOARD[ChessPosition(ch.toString(), Black.startRank)] = Pawn.BLACK_PAWN
        BOARD[ChessPosition(ch.toString(), White.startRank)] = Pawn.WHITE_PAWN
    }
}

fun main() {
    println("Pawns-Only Chess")

    println("First Player's name:")
    players.add(Player(readLine()!!, Color.WHITE))
    println("Second Player's name:")
    players.add(Player(readLine()!!, Color.BLACK))

    loadBoard()
    var activePlayer = players[0]
    var input: String
    printBoard(BOARD)
    do {
        if (hasGameEnded(lastMove, activePlayer)) {
            input = EXIT_CODE
        } else {
            println("${activePlayer.name}'s turn:")
            input = readLine()!!
        }
        if (makeMove(input, activePlayer)) {
            printBoard(BOARD)
            activePlayer = togglePlayer(activePlayer)
            lastMove = input
        } else if (input == EXIT_CODE) {
            println("Bye!")
        }
    } while (input != EXIT_CODE)
}

fun hasGameEnded(chessMove: String, activePlayer: Player): Boolean {
    return hasPlayerWon(chessMove, activePlayer) || playerAtStalemate(activePlayer)
}

private fun playerAtStalemate(activePlayer: Player): Boolean {
    val remainingPieces = BOARD.filter { it.value.color == activePlayer.color }
    if (remainingPieces.size > 1) {
        return false
    }
    val lastPiecePosition = remainingPieces.keys.first()
    val lastPiece = BOARD[lastPiecePosition]!!
    val rowDiff = if (lastPiece.color == Color.WHITE) 1 else -1
    val (leftColumn, rightColumn) = getSideColumns(lastPiecePosition.column)
    val left = ChessPosition(leftColumn, lastPiecePosition.row + rowDiff)
    val right = ChessPosition(rightColumn, lastPiecePosition.row + rowDiff)
    if (BOARD.containsKey(left) || BOARD.containsKey(right)) {
        return false
    }
    val blockedPosition = lastPiece.getBlockedPosition(lastPiecePosition)
    return if (BOARD.containsKey(blockedPosition)) {
        println("Stalemate!")
        true
    } else false
}

private fun hasPlayerWon(chessMove: String, activePlayer: Player): Boolean {
    if (chessMove.isBlank()) return false
    val (_, end) = createChessPositions(chessMove)
    val pawn = BOARD[end]!!
    val remainingPieces = BOARD.filter { it.value.color == activePlayer.color }
    if (remainingPieces.isEmpty() || pawn.atOppositeSide(end)) {
        val lastPlayer = togglePlayer(activePlayer)
        val winningColor = lastPlayer.color.fullColor.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        println("$winningColor Wins!")
        return true
    } else return false
}

fun makeMove(chessMove: String, activePlayer: Player): Boolean {
    if (!validateInput(chessMove)) return false

    val (start, end) = createChessPositions(chessMove)

    if (lastMove.isNotBlank()) {
        val (lastStart, lastEnd) = createChessPositions(lastMove)
        if (isDoubleMove(lastStart, lastEnd)) {
            val passantRow = if (lastEnd.row > lastStart.row) {
                lastEnd.row - 1
            } else {
                lastEnd.row + 1
            }
            BOARD[ChessPosition(lastEnd.column, passantRow)] = EnPassant(BOARD[lastEnd]!!.color)
        }
    }
    return if (playerDoesNotOwnColor(start, activePlayer.color)) {
        println("No ${activePlayer.color} pawn at $start")
        false
    } else if (!checkDirection(start, end)) {
        println("Invalid Input")
        false
    } else {
        if (BOARD.containsKey(end) && BOARD[end] is EnPassant) {
            BOARD.remove(createChessPositions(lastMove).second)
        }
        BOARD[end] = BOARD.remove(start)!!
        BOARD.filter { it.value is EnPassant }.keys.forEach { k ->
            BOARD.remove(k)
        }
        true
    }
}

private fun createChessPositions(chessMove: String): Pair<ChessPosition, ChessPosition> {
    val startPosition = chessMove.substring(0, 2)
    val start = ChessPosition("${startPosition[0]}", "${startPosition[1]}".toInt())
    val endPosition = chessMove.substring(2)
    val end = ChessPosition("${endPosition[0]}", "${endPosition[1]}".toInt())
    return Pair(start, end)
}

fun isDoubleMove(start: ChessPosition, end: ChessPosition): Boolean {
    return abs(start.row - end.row) == 2
}

private fun playerDoesNotOwnColor(start: ChessPosition, color: Color): Boolean {
    if (!BOARD.containsKey(start)) {
        return true
    }
    return if (color == Color.WHITE) {
        BOARD[start] is Black
    } else {
        BOARD[start] is White
    }
}

fun checkDirection(start: ChessPosition, end: ChessPosition): Boolean {
    return if (BOARD.containsKey(end)) {
        validAttack(start, end)
    } else {
        validMovement(start, end)
    }
}

private fun validAttack(start: ChessPosition, end: ChessPosition): Boolean {
    val validAttackSpeed = BOARD[start]!!.validAttackSpeed(start, end)
    val diffX = abs(start.column.chars().findFirst().asInt - end.column.chars().findFirst().asInt)
    return diffX == 1 && validAttackSpeed
}

private fun validMovement(start: ChessPosition, end: ChessPosition): Boolean {
    val validMoveSpeed = BOARD[start]!!.validMoveSpeed(start, end)

    return start.column == end.column && validMoveSpeed
}

fun togglePlayer(activePlayer: Player): Player = players.find { it.color != activePlayer.color }!!

fun validateInput(chessMove: String): Boolean {
    if (chessMove == EXIT_CODE) return false
    if (chessMove.length != 4) return false
    val startPosition = chessMove.substring(0, 2)
    val endPosition = chessMove.substring(2)
    val b = isValidPosition(startPosition) && isValidPosition(endPosition)
    if (!b) println("Invalid Input")
    return b
}

fun isValidPosition(pos: String): Boolean {
    return pos.matches(Regex("[a-hA-H][1-8]"))
}

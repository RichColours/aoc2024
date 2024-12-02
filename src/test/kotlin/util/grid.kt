package util

interface Grid<T> : Collection<Grid.GridElem<T>> {

    enum class Position {
        TL, T, TR, L, M, R, BL, B, BR, U
    }

    class GridElem<T>(
        val grid: Grid<T>,
        val x: Int,
        val y: Int,
        val position: Position?
    ) {

        constructor(x: Int, y: Int) : this(emptyGrid(), x, y, null)

        fun neighboursInc(): List<GridElem<T>> {
            return (-1..1).flatMap { itY ->
                (-1..1).flatMap { itX ->

                    val x = this.x + itX
                    val y = this.y + itY

                    if (x < 0 || x > this.grid.maxX || y < 0 || y > this.grid.maxY) {
                        emptyList()

                    } else {

                        val position = locationToPositionMap[itY to itX]

                        if (position == null)
                            println("he..o")

                        listOf(GridElem(this.grid, x, y, position))
                    }
                }
            }
        }

        fun neighboursExc() = neighboursInc().filter {
            val isSame = it.x == this.x && it.y == this.y

            !isSame
        }

        fun value(): T {
            return grid.valueAt(x, y)
        }

        override fun equals(other: Any?): Boolean {
            val otherGrid: GridElem<*>? = other as? GridElem<*>

            return otherGrid != null && this.x == otherGrid.x && this.y == otherGrid.y
        }

        override fun hashCode(): Int {
            return listOf(x, y).hashCode()
        }

        companion object {
            private val locationToPositionMap = mapOf(
                (-1 to -1) to Position.TL,
                (-1 to 0) to Position.T,
                (-1 to 1) to Position.TR,
                (0 to -1) to Position.L,
                (0 to 0) to Position.M,
                (0 to 1) to Position.R,
                (1 to -1) to Position.BL,
                (1 to 0) to Position.B,
                (1 to 1) to Position.BR
            )

            private fun <T> emptyGrid(): Grid<T> = ListOfRowsGrid(emptyList())
        }
    }

    val maxX: Int
    val maxY: Int
    val width: Int
    val height: Int

    fun valueAt(x: Int, y: Int): T

    fun elemAt(x: Int, y: Int): GridElem<T> {
        return GridElem(this, x, y, null)
    }

    fun printGrid()
}

interface MutableGrid<T> : Grid<T> {

    fun setValueAt(x: Int, y: Int, v: T)
}

open class ListOfRowsGrid<T>(
    private val rowsList: List<List<T>>
) : Grid<T>, AbstractCollection<Grid.GridElem<T>>() {

    override val width = if (rowsList.isEmpty()) 0 else rowsList[0].size
    override val height = rowsList.size

    override val maxX = width - 1
    override val maxY = height - 1

    override fun valueAt(x: Int, y: Int): T {
        return rowsList[y][x]
    }

    override val size: Int
        get() = width * height

    override fun iterator(): Iterator<Grid.GridElem<T>> {

        return (0..< this.size)
            .iterator()
            .transform {
                val x = it % this.width
                val y = it / this.width
                Grid.GridElem(this, x, y, null)
            }
    }

    override fun printGrid() {
        rowsList.forEach { row ->
            println(
                row.joinToString("", "", "")
            )
        }
    }
}

fun List<String>.toGrid(): Grid<Char> {
    val xs = this.map { it.toList() }
    return ListOfRowsGrid(xs)
}

class ListOfMutableRowsGrid<T>(
    private val rowsList: List<MutableList<T>>
) : ListOfRowsGrid<T>(
    rowsList
), MutableGrid<T> {
    override fun setValueAt(x: Int, y: Int, v: T) {
        rowsList[y][x] = v
    }
}

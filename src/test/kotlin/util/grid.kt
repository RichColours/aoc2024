package util

import util.Grid.GridElem
import java.util.*
import kotlin.collections.AbstractCollection

interface GridDataSource<T> {

    val width: Int
    val height: Int

    val size: Int
        get() = width * height

    fun valueAt(x: Int, y: Int): T

    fun row(y: Int): List<T>
}

fun <T> GridDataSource<T>.newGrid() = BaseGrid(this)

open class ListOfStringsDataSource(
    private val listOfRows: List<String>
) : GridDataSource<Char> {

    override fun row(y: Int): List<Char> = listOfRows[y].toCharArray().toList()

    override fun valueAt(x: Int, y: Int): Char = listOfRows[y][x]

    override val width: Int
        get() = if (listOfRows.isEmpty()) 0 else listOfRows[0].length

    override val height: Int
        get() = listOfRows.size
}

open class FlatListDataSource<T>(
    private val flatList: List<T>,
    private val inputWidth: Int
) : GridDataSource<T> {

    init {
        assert(flatList.size % inputWidth == 0)
    }

    override fun row(y: Int): List<T> = flatList.subList(y * inputWidth, (y * inputWidth) + inputWidth)

    override fun valueAt(x: Int, y: Int): T = flatList[(y * inputWidth) + x]

    override val width: Int
        get() = inputWidth

    override val height: Int
        get() = flatList.size / width
}

interface Grid<T> : Collection<GridElem<T>> {

    enum class Position {
        TL, T, TR, L, M, R, BL, B, BR
    }

    class GridElem<T>(
        val grid: Grid<T>,
        val x: Int,
        val y: Int,
        val position: Position?
    ) {

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

        fun neighboursHorizontalAndVertical() =
            this.neighboursExc().filter { it.position in listOf(Position.T, Position.R, Position.B, Position.L) }

        fun neighbour(p: Position): GridElem<T>? {
            return neighboursInc().find { it.position == p }
        }

        fun value(): T {
            return grid.valueAt(x, y)
        }

        /**
         * But should not need because neighbour return is grid-bounded.
         * @see Grid.isInGrid
         */
        fun isInGrid(): Boolean = this.grid.isInGrid(this.x, this.y)

        override fun equals(other: Any?): Boolean {
            val otherElem: GridElem<*>? = other as? GridElem<*>

            return otherElem != null && this.x == otherElem.x && this.y == otherElem.y
        }

        override fun hashCode(): Int {
            return Objects.hash(this.x, this.y)
        }

        override fun toString(): String {
            return "([$x, $y] = ${value()})"
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

            // private fun <T> emptyGrid(): Grid<T> = ListOfRowsGrid(emptyList())
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

    fun toGridString(): String =
        (0..maxY).map { itY ->
            (0..maxX).map { itX ->
                this.valueAt(itX, itY)
            }.joinToString("", "", "\n")
        }.joinToString("")

    fun printGrid() {
        println(toGridString())
    }

    /**
     * But should not need because we try to only generate grid-bounded elements.
     * @see Grid.GridElem.isInGrid
     */
    fun isInGrid(x: Int, y: Int): Boolean = x in (0..maxX) && y in (0..maxY)
}

interface MutableGrid<T> : Grid<T> {

    fun setValueAt(x: Int, y: Int, v: T)
}

open class BaseGrid<T>(

    private val dataSource: GridDataSource<T>

) : Grid<T>, AbstractCollection<GridElem<T>>() {

    override val width = dataSource.width
    override val height = dataSource.height

    override val maxX = width - 1
    override val maxY = height - 1

    override fun valueAt(x: Int, y: Int): T {
        return dataSource.valueAt(x, y)
    }

    override val size: Int
        get() = width * height

    override fun iterator(): Iterator<GridElem<T>> {

        return (0..<this.size)
            .iterator()
            .transform {
                val x = it % this.width
                val y = it / this.width
                GridElem(this, x, y, null)
            }
    }
}

fun List<String>.toGrid(): Grid<Char> = BaseGrid(ListOfStringsDataSource(this))

fun <T> List<T>.toGrid(inputWidth: Int): Grid<T> = BaseGrid(FlatListDataSource(this, inputWidth))

open class BaseProxyingGrid<T>(
    private val proxied: Grid<T>
) : Grid<T> {

    override val width: Int
        get() = proxied.width

    override val height: Int
        get() = proxied.height

    override val maxX: Int
        get() = proxied.maxX

    override val maxY: Int
        get() = proxied.maxY

    override val size: Int
        get() = proxied.size

    override fun valueAt(x: Int, y: Int): T = proxied.valueAt(x, y)

    override fun elemAt(x: Int, y: Int): GridElem<T> = proxied.elemAt(x, y)

    override fun printGrid() = proxied.printGrid()

    override fun isEmpty(): Boolean = proxied.isEmpty()

    override fun iterator(): Iterator<GridElem<T>> = proxied.iterator()

    override fun contains(element: GridElem<T>): Boolean = proxied.contains(element)

    override fun containsAll(elements: Collection<GridElem<T>>): Boolean = proxied.containsAll(elements)

}

/**
 * Rotated 90 clockwise
 * E.g. new 0,0 is taken from
 */
fun <T> Grid<T>.rotatedView(): Grid<T> = object : BaseProxyingGrid<T>(this) {

    override val width: Int
        get() = this@rotatedView.height

    override val height: Int
        get() = this@rotatedView.width

    override val maxX: Int
        get() = this@rotatedView.maxY

    override val maxY: Int
        get() = this@rotatedView.maxX

    fun transform(x: Int, y: Int): Pair<Int, Int> {
        val oldX = y
        val oldY = this@rotatedView.maxY - x
        return oldX to oldY
    }

    override fun valueAt(x: Int, y: Int): T {

        val trans = transform(x, y)
        return this@rotatedView.valueAt(trans.first, trans.second)
    }

    /** I think can be deleted *
     * TODO
     */
    override fun elemAt(x: Int, y: Int): GridElem<T> {

        val trans = transform(x, y)
        return this@rotatedView.elemAt(trans.first, trans.second)
    }
}

open class SingleValueOverrideGridDataSource<T>(
    private val backingSource: GridDataSource<T>,
    private val x: Int,
    private val y: Int,
    private val newValue: T
) : GridDataSource<T> {

    override val width: Int
        get() = backingSource.width
    override val height: Int
        get() = backingSource.height

    override fun row(y: Int): List<T> {
        return if (y == this.y) {
            // Reconstruct row
            backingSource.row(y).mapIndexed { index, it ->
                if (index == this.x) {
                    newValue
                } else {
                    it
                }
            }
        } else {
            backingSource.row(y)
        }
    }

    override fun valueAt(x: Int, y: Int): T {
        return if (x == this.x && y == this.y) {
            newValue
        } else {
            backingSource.valueAt(x, y)
        }
    }
}

fun <T> GridDataSource<T>.overrideSingleValue(x: Int, y: Int, v: T) =
    SingleValueOverrideGridDataSource(this, x, y, v)
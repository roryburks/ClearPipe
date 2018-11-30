package rb.extendo.extensions


infix fun <T> Sequence<T>.then(other: Sequence<T>) =
    ThenSequence(this,other)

class ThenSequence<T>(
    private val first: Sequence<T>,
    private  val second: Sequence<T>)
    : Sequence<T>
{
    override fun iterator() = IteratorImp(first,second)

    class IteratorImp<T>(
        private val first: Sequence<T>,
        private val second: Sequence<T>)
        :Iterator<T>
    {
        private var onFirst = true
        private var iterator: Iterator<T>? = null

        override fun hasNext() : Boolean {
            val iterator = iterator
            return when {
                iterator == null -> {this.iterator = first.iterator(); hasNext()}
                onFirst && !iterator.hasNext() -> {
                    onFirst = false
                    this.iterator = second.iterator()
                    hasNext()
                }
                else -> iterator.hasNext()
            }
        }

        override fun next(): T  {
            val iterator = iterator
            return when {
                iterator == null -> {this.iterator = first.iterator() ; next()}
                onFirst && !iterator.hasNext() -> {
                    onFirst = false
                    this.iterator = second.iterator()
                    next()
                }
                else -> iterator.next()
            }
        }
    }
}

class ThenIterator<T>(
    private val first: Iterable<T>,
    private val second: Iterable<T>)
    :Iterator<T>
{
    private var onFirst = true
    private var iterator: Iterator<T>? = null

    override fun hasNext() : Boolean {
        val iterator = iterator
        return when {
            iterator == null -> {this.iterator = first.iterator(); hasNext()}
            onFirst -> {
                onFirst = false
                this.iterator = second.iterator()
                hasNext()
            }
            else -> iterator.hasNext()
        }
    }

    override fun next(): T  {
        val iterator = iterator
        return when {
            iterator == null -> {this.iterator = first.iterator() ; next()}
            onFirst && !iterator.hasNext() -> {
                onFirst = false
                this.iterator = second.iterator()
                next()
            }
            else -> iterator.next()
        }
    }
}